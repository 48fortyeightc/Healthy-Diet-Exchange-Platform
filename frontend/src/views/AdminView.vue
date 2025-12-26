<template>
  <div class="admin-view">
    <h2>🛠️ 系统管理模块</h2>
    <el-divider />

    <el-tabs type="border-card">
      <!-- 用户管理 -->
      <el-tab-pane label="用户账号管理">
        <el-table :data="users" style="width: 100%" v-loading="loading">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="email" label="邮箱" />
          <el-table-column prop="role" label="角色" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.role === 'ADMIN' ? 'danger' : 'info'">{{ scope.row.role }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
                {{ scope.row.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150">
            <template #default="scope">
              <el-button 
                v-if="scope.row.status === 1" 
                type="warning" 
                size="small" 
                @click="handleUserStatus(scope.row.id, 0)"
                :disabled="scope.row.role === 'ADMIN'"
              >禁用</el-button>
              <el-button 
                v-else 
                type="success" 
                size="small" 
                @click="handleUserStatus(scope.row.id, 1)"
              >启用</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 内容审核管理 -->
      <el-tab-pane label="食谱内容审核">
        <el-table :data="recipes" style="width: 100%" v-loading="loading">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column label="封面" width="120">
            <template #default="scope">
              <el-image :src="scope.row.coverImage" style="width: 80px; height: 60px" fit="cover" />
            </template>
          </el-table-column>
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="status" label="当前状态" width="120">
            <template #default="scope">
              <el-tag :type="scope.row.status === 'PUBLISHED' ? 'success' : 'danger'">
                {{ scope.row.status === 'PUBLISHED' ? '已发布' : '已下架' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150">
            <template #default="scope">
              <el-button 
                v-if="scope.row.status === 'PUBLISHED'" 
                type="danger" 
                size="small" 
                @click="handleAudit(scope.row.id, 'REJECTED')"
              >下架</el-button>
              <el-button 
                v-else 
                type="success" 
                size="small" 
                @click="handleAudit(scope.row.id, 'PUBLISHED')"
              >重新上架</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 审计日志 -->
      <el-tab-pane label="安全审计日志">
        <div class="audit-header">
          <p>📜 这里记录了所有内容变更历史（含触发器自动记录）</p>
        </div>
        <el-table :data="auditLogs" style="width: 100%" size="small">
          <el-table-column prop="createdAt" label="操作时间" width="180">
            <template #default="scope">
              {{ formatTime(scope.row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column prop="action" label="动作" width="150" />
          <el-table-column prop="targetTable" label="操作表" width="120" />
          <el-table-column prop="targetId" label="目标ID" width="100" />
          <el-table-column prop="oldValue" label="旧值 (JSON)" />
          <el-table-column prop="newValue" label="新值 (JSON)" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const recipes = ref([])
const users = ref([])
const auditLogs = ref([])
const loading = ref(false)

const formatTime = (time: string) => {
  return new Date(time).toLocaleString()
}

const fetchUsers = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/admin/users')
    users.value = res.data
  } catch (err) {
    ElMessage.error('获取用户列表失败')
  }
}

const fetchRecipes = async () => {
  try {
    loading.value = true
    const res = await axios.get('http://localhost:8080/api/admin/recipes')
    recipes.value = res.data
  } catch (err) {
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

const fetchAuditLogs = async () => {
  const res = await axios.get('http://localhost:8080/api/admin/audit-logs')
  auditLogs.value = res.data
}

const handleAudit = async (id: number, status: string) => {
  try {
    const res = await axios.post('http://localhost:8080/api/admin/recipe/audit', { id, status })
    ElMessage.success(res.data)
    fetchRecipes()
    fetchAuditLogs()
  } catch (err) {
    ElMessage.error('操作失败')
  }
}

const handleUserStatus = async (id: number, status: number) => {
  try {
    const res = await axios.post('http://localhost:8080/api/admin/user/status', { id, status })
    ElMessage.success(res.data)
    fetchUsers()
  } catch (err) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  fetchUsers()
  fetchRecipes()
  fetchAuditLogs()
})
</script>

<style scoped>
.admin-view {
  padding: 20px 0;
}
.audit-header {
  margin-bottom: 20px;
  color: #666;
}
</style>

