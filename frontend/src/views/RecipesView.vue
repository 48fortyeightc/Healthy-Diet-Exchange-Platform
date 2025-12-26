<template>
  <div class="recipes-view">
    <h2>精选菜谱</h2>
    <el-divider />
    <el-row :gutter="20">
      <el-col :span="6" v-for="recipe in recipes" :key="recipe.id">
        <el-card :body-style="{ padding: '0px' }" class="recipe-card" @click="$router.push(`/recipe/${recipe.id}`)">
          <div class="image-wrapper">
            <img :src="recipe.coverImage" class="image" v-if="recipe.coverImage" />
          </div>
          <div style="padding: 14px">
            <div class="recipe-title">{{ recipe.title }}</div>
            <div class="recipe-info">
              <el-tag size="small" type="success">{{ recipe.difficulty }}</el-tag>
              <span class="time">⏱ {{ recipe.cookingTime }}分钟</span>
            </div>
            <div class="bottom">
              <span class="author">by {{ recipe.authorName }}</span>
              <span class="stats">❤️ {{ recipe.likeCount }} 💬 {{ recipe.commentCount }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        background
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

const recipes = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const loading = ref(false)

const fetchRecipes = async () => {
  loading.value = true
  try {
    const res = await axios.get(`http://localhost:8080/api/recipes/list?page=${currentPage.value}&size=${pageSize.value}`)
    recipes.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取菜谱失败:', error)
  } finally {
    loading.value = false
  }
}

const handlePageChange = (val: number) => {
  currentPage.value = val
  fetchRecipes()
  window.scrollTo(0, 0)
}

onMounted(() => {
  fetchRecipes()
})
</script>

<style scoped>
.recipes-view h2 {
  margin-bottom: 20px;
  color: #333;
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
}
.image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.recipe-title {
  font-weight: bold;
  margin-bottom: 10px;
  height: 40px;
  overflow: hidden;
}
.recipe-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.time {
  font-size: 12px;
  color: #999;
}
.bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
}
.author {
  color: #ff9d00;
}
.stats {
  color: #999;
}
.pagination-container {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}
</style>

