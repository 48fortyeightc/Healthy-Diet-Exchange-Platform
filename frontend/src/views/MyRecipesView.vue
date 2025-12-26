<template>
  <div class="my-creative-center">
    <div class="header">
      <h2>🎨 作品管理中心</h2>
      <div class="actions">
        <el-button type="primary" icon="Plus" @click="router.push('/publish')">发布新内容</el-button>
      </div>
    </div>
    <el-divider />

    <el-tabs v-model="activeType">
      <!-- 食谱管理 -->
      <el-tab-pane label="我的食谱" name="recipe">
        <el-table :data="recipes" style="width: 100%" v-loading="loading">
          <el-table-column label="封面" width="120">
            <template #default="scope">
              <el-image :src="scope.row.coverImage" style="width: 80px; height: 60px; border-radius: 4px" fit="cover" />
            </template>
          </el-table-column>
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button size="small" @click="handleEditRecipe(scope.row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDeleteRecipe(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 文章管理 -->
      <el-tab-pane label="我的文章" name="post">
        <el-table :data="posts" style="width: 100%" v-loading="loading">
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="发布时间" width="180">
            <template #default="scope">
              {{ new Date(scope.row.createdAt).toLocaleDateString() }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button size="small" @click="handleEditPost(scope.row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDeletePost(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const recipes = ref([])
const posts = ref([])
const activeType = ref('recipe')
const loading = ref(false)

const getStatusType = (status: string) => {
  const map: any = { 'PUBLISHED': 'success', 'DRAFT': 'info', 'REJECTED': 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: any = { 'PUBLISHED': '已发布', 'DRAFT': '草稿', 'REJECTED': '未通过' }
  return map[status] || '未知'
}

const fetchData = async () => {
  const userStr = localStorage.getItem('user')
  if (!userStr) return
  const user = JSON.parse(userStr)
  loading.value = true
  try {
    const [recipeRes, postRes] = await Promise.all([
      axios.get(`http://localhost:8080/api/recipes/my?userId=${user.id}`),
      axios.get(`http://localhost:8080/api/posts/my?userId=${user.id}`)
    ])
    recipes.value = recipeRes.data
    posts.value = postRes.data
  } catch (err) {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const handleEditRecipe = (recipe: any) => {
  router.push({ path: '/publish', query: { id: recipe.id, type: 'recipe' } })
}

const handleEditPost = (post: any) => {
  router.push({ path: '/publish', query: { id: post.id, type: 'post' } })
}

const handleDeleteRecipe = (id: number) => {
  ElMessageBox.confirm('确定删除该食谱吗？', '提示').then(async () => {
    await axios.delete(`http://localhost:8080/api/recipes/delete/${id}`)
    ElMessage.success('已删除')
    fetchData()
  })
}

const handleDeletePost = (id: number) => {
  ElMessageBox.confirm('确定删除该文章吗？', '提示').then(async () => {
    await axios.delete(`http://localhost:8080/api/posts/delete/${id}`)
    ElMessage.success('已删除')
    fetchData()
  })
}

onMounted(fetchData)
</script>

<style scoped>
.my-creative-center { padding: 20px 0; }
.header { display: flex; justify-content: space-between; align-items: center; }
</style>
