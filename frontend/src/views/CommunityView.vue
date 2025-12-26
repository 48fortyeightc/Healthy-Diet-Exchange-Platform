<template>
  <div class="community-view">
    <div class="header">
      <h2>📰 健康资讯广场</h2>
      <p>学习健康饮食知识，分享生活点滴</p>
    </div>
    
    <div class="post-list" v-loading="loading">
      <el-row :gutter="20">
        <el-col :span="8" v-for="post in posts" :key="post.id">
          <el-card class="post-card" :body-style="{ padding: '0px' }" shadow="hover" @click="viewPost(post.id)" style="cursor: pointer; margin-bottom: 20px;">
            <el-image :src="post.coverImage || 'https://images.unsplash.com/photo-1490645935967-10de6ba17061?w=500'" class="post-image" fit="cover" />
            <div class="post-content">
              <h3>{{ post.title }}</h3>
              <p class="summary">{{ truncate(post.content, 60) }}</p>
              <div class="footer">
                <span class="time">{{ formatDate(post.createdAt) }}</span>
                <el-button type="primary" link>阅读全文</el-button>
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const posts = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(15)
const total = ref(0)

const fetchPosts = async () => {
  loading.value = true
  try {
    const res = await axios.get(`http://localhost:8080/api/posts/list?page=${currentPage.value}&size=${pageSize.value}`)
    posts.value = res.data.records
    total.value = res.data.total
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
}

const handlePageChange = (val: number) => {
  currentPage.value = val
  fetchPosts()
  window.scrollTo(0, 0)
}

const truncate = (text: string, length: number) => {
  return text.length > length ? text.substring(0, length) + '...' : text
}

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString()
}

const viewPost = (id: number) => {
  router.push(`/post/${id}`)
}

onMounted(fetchPosts)
</script>

<style scoped>
.community-view { padding: 20px 0; }
.header { margin-bottom: 30px; text-align: center; }
.header h2 { font-size: 28px; color: #333; }
.post-image { width: 100%; height: 180px; }
.post-content { padding: 15px; }
.post-content h3 { margin: 0 0 10px 0; font-size: 18px; }
.summary { font-size: 14px; color: #666; height: 40px; overflow: hidden; }
.footer { margin-top: 15px; display: flex; justify-content: space-between; align-items: center; }
.time { font-size: 12px; color: #999; }
.pagination-container { margin-top: 40px; display: flex; justify-content: center; }
</style>
