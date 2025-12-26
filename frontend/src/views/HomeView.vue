<template>
  <div class="home">
    <div class="banner">
      <h2>发现健康美味，分享饮食快乐</h2>
    </div>
    
    <div class="section-title">
      <h3>每日精选菜谱</h3>
      <el-link type="primary" @click="router.push('/recipes')">更多 ></el-link>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading" :size="40"><Loading /></el-icon>
      <p>正在加载美食数据...</p>
    </div>

    <!-- 错误提示 -->
    <el-alert v-else-if="error" title="数据加载失败" type="warning" :closable="false" show-icon>
      <p>{{ errorMessage }}</p>
      <p style="margin-top: 10px; font-size: 12px;">
        <strong>请确保：</strong>
        <br>1. 后端服务已启动（Spring Boot运行在 http://localhost:8080）
        <br>2. 数据库已成功导入数据
        <br>3. 刷新页面重试
      </p>
    </el-alert>

    <!-- 食谱列表 -->
    <el-row :gutter="20" v-else>
      <el-col :span="6" v-for="recipe in recipes" :key="recipe.id">
        <el-card :body-style="{ padding: '0px' }" class="recipe-card" @click="router.push(`/recipe/${recipe.id}`)">
          <div class="image-wrapper">
            <el-image 
              :src="recipe.coverImage" 
              class="image" 
              fit="cover"
              lazy
            >
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                  <span>图片加载失败</span>
                </div>
              </template>
            </el-image>
          </div>
          <div style="padding: 14px">
            <div class="recipe-title">{{ recipe.title }}</div>
            <div class="bottom">
              <span class="author">by {{ recipe.authorName }}</span>
              <div class="stats">
                <span>❤️ {{ recipe.likeCount }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 空状态 -->
    <el-empty v-if="!loading && !error && recipes.length === 0" description="暂无食谱数据" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Loading, Picture } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()

interface Recipe {
  id: number
  title: string
  coverImage: string
  authorName: string
  likeCount: number
}

const recipes = ref<Recipe[]>([])
const loading = ref(true)
const error = ref(false)
const errorMessage = ref('')

const fetchRecipes = async () => {
  try {
    loading.value = true
    error.value = false
    const res = await axios.get('http://localhost:8080/api/recipes/list?page=1&size=8', {
      timeout: 5000
    })
    recipes.value = res.data.records || []
  } catch (err: any) {
    error.value = true
    if (err.code === 'ECONNABORTED') {
      errorMessage.value = '请求超时，后端服务可能未启动'
    } else if (err.code === 'ERR_NETWORK') {
      errorMessage.value = '网络错误，请确认后端服务运行在 http://localhost:8080'
    } else {
      errorMessage.value = err.message || '未知错误'
    }
    console.error('获取菜谱失败:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchRecipes()
})
</script>

<style scoped>
.banner {
  height: 300px;
  background-color: #f5f7fa;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 30px;
  border-radius: 8px;
  background-image: linear-gradient(rgba(0,0,0,0.1), rgba(0,0,0,0.1)), url('https://images.unsplash.com/photo-1490818387583-1baba5e638af?ixlib=rb-1.2.1&auto=format&fit=crop&w=1000&q=80');
  background-size: cover;
  background-position: center;
  color: white;
  text-shadow: 0 2px 4px rgba(0,0,0,0.5);
}
.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.loading-container {
  text-align: center;
  padding: 60px 0;
  color: #999;
}
.loading-container p {
  margin-top: 20px;
}
.recipe-card {
  margin-bottom: 20px;
  transition: transform 0.3s;
  cursor: pointer;
}
.recipe-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}
.image-wrapper {
  height: 200px;
  overflow: hidden;
  background: #f5f7fa;
}
.image {
  width: 100%;
  height: 100%;
}
.image-error {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #999;
  font-size: 12px;
}
.image-error .el-icon {
  font-size: 40px;
  margin-bottom: 10px;
}
.recipe-title {
  font-weight: bold;
  margin-bottom: 10px;
  height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #999;
}
.author {
  color: #ff9d00;
}
</style>
