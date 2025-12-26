<template>
  <div class="profile-view">
    <el-row :gutter="20">
      <!-- 左侧：个人资料 -->
      <el-col :span="8">
        <el-card class="profile-card">
          <div class="avatar-container">
            <el-avatar :size="100" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
            <h3>{{ profile.nickname || '未设置昵称' }}</h3>
            <p class="role-tag">
              <el-tag size="small">{{ user.role }}</el-tag>
            </p>
          </div>
          <el-divider />
          
          <div v-if="!isEditingProfile" class="info-list">
            <div class="info-item">
              <span class="label">用户名:</span>
              <span>{{ user.username }}</span>
            </div>
            <div class="info-item">
              <span class="label">邮箱:</span>
              <span>{{ user.email || '未绑定' }}</span>
            </div>
            <div class="info-item">
              <span class="label">性别:</span>
              <span>{{ genderMap[profile.gender] || '保密' }}</span>
            </div>
            <div class="info-item">
              <span class="label">简介:</span>
              <p class="bio">{{ profile.bio || '这个用户很懒，什么都没写。' }}</p>
            </div>
            <el-button type="primary" link style="width: 100%; margin-top: 20px;" @click="isEditingProfile = true">编辑基本资料</el-button>
          </div>

          <!-- 编辑模式 -->
          <el-form v-else label-width="60px" style="text-align: left;">
            <el-form-item label="昵称">
              <el-input v-model="profileForm.nickname" />
            </el-form-item>
            <el-form-item label="性别">
              <el-select v-model="profileForm.gender" placeholder="请选择" style="width: 100%;">
                <el-option label="男" value="M" />
                <el-option label="女" value="F" />
                <el-option label="保密" value="S" />
              </el-select>
            </el-form-item>
            <el-form-item label="简介">
              <el-input v-model="profileForm.bio" type="textarea" :rows="3" />
            </el-form-item>
            <div style="text-align: center; margin-top: 20px;">
              <el-button type="primary" size="small" @click="saveProfile">保存</el-button>
              <el-button size="small" @click="isEditingProfile = false">取消</el-button>
            </div>
          </el-form>
        </el-card>
      </el-col>

      <!-- 右侧：健康档案管理 -->
      <el-col :span="16">
        <el-card class="health-card">
          <template #header>
            <div class="card-header">
              <span>🩺 健康档案管理</span>
            </div>
          </template>

          <el-form label-width="100px" class="health-form">
            <el-form-item label="当前体重">
              <el-input-number v-model="editData.weight" :precision="1" :step="0.1" @change="calcBmi" />
              <span style="margin-left: 10px;">Kg</span>
            </el-form-item>

            <el-form-item label="当前身高">
              <el-input-number v-model="editData.height" :precision="1" :step="0.1" @change="calcBmi" />
              <span style="margin-left: 10px;">cm</span>
            </el-form-item>

            <el-form-item label="BMI 指数">
              <div class="bmi-container">
                <el-statistic :value="editData.bmi" :precision="2" />
                <el-tag :type="bmiStatus.type" style="margin-left: 20px;">{{ bmiStatus.text }}</el-tag>
              </div>
            </el-form-item>

            <el-form-item label="过敏史">
              <el-input v-model="editData.allergies" type="textarea" placeholder="请填写您的过敏史或食物禁忌" />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" :loading="updating" @click="handleUpdate">保存健康档案</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const user = ref<any>({})
const profile = ref<any>({})
const health = ref<any>({})
const updating = ref(false)
const isEditingProfile = ref(false)

const profileForm = reactive({
  nickname: '',
  gender: '',
  bio: ''
})

const editData = reactive({
  weight: 70.5,
  height: 175.0,
  bmi: 22.5,
  allergies: ''
})

const genderMap: any = { 'M': '男', 'F': '女', 'S': '保密' }

const bmiStatus = computed(() => {
  const val = editData.bmi
  if (val < 18.5) return { text: '偏瘦', type: 'info' }
  if (val < 24) return { text: '标准', type: 'success' }
  if (val < 28) return { text: '偏胖', type: 'warning' }
  return { text: '肥胖', type: 'danger' }
})

const calcBmi = () => {
  if (editData.height > 0) {
    const heightInMeters = editData.height / 100
    editData.bmi = editData.weight / (heightInMeters * heightInMeters)
  }
}

const fetchProfile = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/profile/1')
    user.value = res.data.user || {}
    profile.value = res.data.profile || {}
    health.value = res.data.health || {}
    
    editData.weight = parseFloat(res.data.displayWeight)
    editData.height = parseFloat(res.data.displayHeight)
    editData.bmi = health.value.bmi
    editData.allergies = health.value.allergies

    profileForm.nickname = profile.value.nickname
    profileForm.gender = profile.value.gender
    profileForm.bio = profile.value.bio
  } catch (error) {
    console.error('获取资料失败:', error)
  }
}

const saveProfile = async () => {
  try {
    const res = await axios.post('http://localhost:8080/api/profile/updateProfile', {
      userId: 1,
      nickname: profileForm.nickname,
      gender: profileForm.gender,
      bio: profileForm.bio
    })
    ElMessage.success('个人资料更新成功')
    isEditingProfile.value = false
    fetchProfile()
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

const handleUpdate = async () => {
  try {
    updating.value = true
    const res = await axios.post('http://localhost:8080/api/profile/updateHealth', {
      userId: 1,
      bmi: editData.bmi,
      allergies: editData.allergies
    })
    
    if (res.data.startsWith('更新成功')) {
      ElMessage.success('健康档案保存成功')
      fetchProfile()
    } else {
      ElMessageBox.alert('保存失败：输入的数据不符合健康逻辑规范', '系统提示', { type: 'error' })
    }
  } catch (error) {
    ElMessage.error('网络请求失败')
  } finally {
    updating.value = false
  }
}

onMounted(() => {
  fetchProfile()
})
</script>

<style scoped>
.profile-card {
  text-align: center;
}
.avatar-container {
  padding: 20px 0;
}
.avatar-container h3 {
  margin: 10px 0;
}
.info-list {
  text-align: left;
}
.info-item {
  margin-bottom: 15px;
}
.label {
  font-weight: bold;
  color: #666;
  margin-right: 10px;
}
.bio {
  font-size: 14px;
  color: #999;
  line-height: 1.6;
}
.bmi-container {
  display: flex;
  align-items: center;
  background: #f5f7fa;
  padding: 10px 20px;
  border-radius: 4px;
}
</style>
