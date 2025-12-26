<template>
  <div class="favorites-view">
    <div class="header">
      <h2>⭐ 我的收藏</h2>
      <p>这里记录了你感兴趣的所有健康内容</p>
    </div>
    <el-divider />

    <el-tabs v-model="activeTab" class="fav-tabs">
      <!-- 收藏的食谱 -->
      <el-tab-pane label="食谱收藏" name="recipes">
        <el-row :gutter="20" v-loading="loading">
          <el-col :span="6" v-for="recipe in favorites.recipes" :key="recipe.id">
            <el-card :body-style="{ padding: '0px' }" class="fav-card" @click="router.push(`/recipe/${recipe.id}`)">
              <el-image :src="recipe.coverImage" class="fav-image" fit="cover" />
              <div class="info">
                <h4>{{ recipe.title }}</h4>
                <div class="meta">
                  <span>{{ recipe.authorName }}</span>
                  <span class="like-tag">❤️ {{ recipe.likeCount }}</span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
        <el-empty v-if="!loading && favorites.recipes.length === 0" description="暂无收藏食谱" />
      </el-tab-pane>

      <!-- 收藏的文章 -->
      <el-tab-pane label="文章收藏" name="posts">
        <el-row :gutter="20" v-loading="loading">
          <el-col :span="8" v-for="post in favorites.posts" :key="post.id">
            <el-card :body-style="{ padding: '0px' }" class="fav-card" @click="router.push(`/post/${post.id}`)">
              <el-image :src="post.coverImage" class="fav-image" fit="cover" />
              <div class="info">
                <h4>{{ post.title }}</h4>
                <div class="meta">
                  <span>{{ new Date(post.createdAt).toLocaleDateString() }}</span>
                  <span class="view-tag">👁️ {{ post.viewCount }}</span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
        <el-empty v-if="!loading && favorites.posts.length === 0" description="暂无收藏文章" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const activeTab = ref('recipes')
const loading = ref(false)
const favorites = ref({ recipes: [], posts: [] })

const fetchFavorites = async () => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  if (!user.id) return
  
  loading.value = true
  try {
    const res = await axios.get(`http://localhost:8080/api/profile/favorites?userId=${user.id}`)
    favorites.value = res.data
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
}

onMounted(fetchFavorites)
</script>

<style scoped>
.favorites-view { padding: 20px 0; }
.header { text-align: center; margin-bottom: 30px; }
.fav-card { margin-bottom: 20px; cursor: pointer; transition: transform 0.3s; }
.fav-card:hover { transform: translateY(-5px); }
.fav-image { width: 100%; height: 160px; }
.info { padding: 12px; }
.info h4 { margin: 0 0 10px 0; height: 40px; overflow: hidden; }
.meta { display: flex; justify-content: space-between; font-size: 12px; color: #999; }
.like-tag { color: #f56c6c; }
</style>

