<template>
  <div class="shares-view">
    <div class="header">
      <h2>🥬 食材共享邻里互助</h2>
      <p>拒绝浪费，分享你的余量食材给有需要的邻居</p>
    </div>

    <el-card class="filter-card">
      <div class="filter-box">
        <span>📍 选择区域：</span>
        <el-select v-model="selectedRegion" placeholder="选择地区" @change="fetchShares">
          <el-option label="全部地区" value="" />
          <el-option label="北京市-朝阳区" value="北京市-朝阳区" />
          <el-option label="北京市-海淀区" value="北京市-海淀区" />
          <el-option label="上海市-浦东新区" value="上海市-浦东新区" />
          <el-option label="广州市-天河区" value="广州市-天河区" />
        </el-select>
        <el-button type="success" icon="Plus" style="margin-left: 20px;" @click="showPublish = true">我要分享</el-button>
      </div>
    </el-card>

    <div class="share-list" v-loading="loading">
      <el-empty v-if="shares.length === 0" description="该区域暂时没有分享中的食材" />
      <el-row :gutter="20">
        <el-col :span="12" v-for="share in shares" :key="share.id">
          <el-card class="share-card" shadow="hover">
            <div class="share-info">
              <h3>{{ share.name }}</h3>
              <div class="tags">
                <el-tag size="small" type="warning">数量: {{ share.quantity }}</el-tag>
                <el-tag size="small" type="danger" style="margin-left: 10px;">有效期: {{ share.expiryDate }}</el-tag>
              </div>
              <p class="region">📍 {{ share.region }}</p>
              <div class="status-box">
                <el-tag :type="share.status === 'AVAILABLE' ? 'success' : 'info'">
                  {{ share.status === 'AVAILABLE' ? '领取中' : '已领完' }}
                </el-tag>
              </div>
            </div>
            <div class="actions">
              <el-button type="primary" :disabled="share.status !== 'AVAILABLE'" @click="handleApply(share)">
                {{ share.status === 'AVAILABLE' ? '立即申请' : '已领完' }}
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 发布弹窗 -->
    <el-dialog v-model="showPublish" title="发布分享食材" width="500px">
      <el-form :model="newShare" label-width="100px">
        <el-form-item label="食材名称" required>
          <el-input v-model="newShare.name" placeholder="如：两根胡萝卜" />
        </el-form-item>
        <el-form-item label="剩余用量" required>
          <el-input v-model="newShare.quantity" placeholder="如：200g" />
        </el-form-item>
        <el-form-item label="保质期至">
          <el-date-picker v-model="newShare.expiryDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="所在区域" required>
          <el-select v-model="newShare.region" placeholder="请选择区域" style="width: 100%">
            <el-option label="北京市-朝阳区" value="北京市-朝阳区" />
            <el-option label="北京市-海淀区" value="北京市-海淀区" />
            <el-option label="上海市-浦东新区" value="上海市-浦东新区" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPublish = false">取消</el-button>
        <el-button type="success" @click="submitShare">发布分享</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const selectedRegion = ref('')
const shares = ref([])
const loading = ref(false)
const showPublish = ref(false)

const newShare = reactive({
  name: '',
  quantity: '',
  expiryDate: '',
  region: ''
})

const fetchShares = async () => {
  loading.value = true
  try {
    const url = selectedRegion.value 
      ? `http://localhost:8080/api/shares/nearby?region=${selectedRegion.value}`
      : 'http://localhost:8080/api/shares/list'
    const res = await axios.get(url)
    shares.value = res.data
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
}

const submitShare = async () => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  if (!user.id) {
    ElMessage.warning('请先登录')
    return
  }
  
  try {
    await axios.post('http://localhost:8080/api/shares/publish', {
      ...newShare,
      providerId: user.id,
      status: 'AVAILABLE'
    })
    ElMessage.success('发布成功！')
    showPublish.value = false
    fetchShares()
  } catch (err) {
    ElMessage.error('发布失败')
  }
}

const handleApply = (share: any) => {
  ElMessage.success(`申请已发送给发布者，请耐心等待回复！`)
}

onMounted(fetchShares)
</script>

<style scoped>
.shares-view { padding: 20px 0; }
.header { text-align: center; margin-bottom: 30px; }
.filter-card { margin-bottom: 25px; }
.filter-box { display: flex; align-items: center; justify-content: center; }
.share-card { margin-bottom: 20px; display: flex; justify-content: space-between; align-items: center; padding: 10px; }
.share-info h3 { margin: 0 0 10px 0; color: #333; }
.tags { margin-bottom: 10px; }
.region { font-size: 13px; color: #666; margin: 10px 0; }
.status-box { margin-top: 5px; }
</style>
