<template>
  <div class="post-detail" v-loading="loading">
    <div v-if="post" class="content-wrapper">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/community' }">健康社区</el-breadcrumb-item>
        <el-breadcrumb-item>正文</el-breadcrumb-item>
      </el-breadcrumb>

      <div class="header">
        <h1>{{ post.title }}</h1>
        <div class="meta">
          <span>📅 {{ formatDate(post.createdAt) }}</span>
          <span>👁️ {{ post.viewCount }} 次阅读</span>
          <el-tag v-for="tag in (post.tags || '').split(',')" :key="tag" size="small" style="margin-left: 10px">
            {{ tag }}
          </el-tag>
        </div>
      </div>

      <el-image :src="post.coverImage" class="main-image" fit="cover">
        <template #error>
          <div class="image-placeholder">健康生活</div>
        </template>
      </el-image>

      <div class="article-body">
        <p v-for="(p, idx) in post.content.split('\n')" :key="idx">{{ p }}</p>
      </div>

      <!-- 收藏与操作区 -->
      <div class="action-bar">
        <el-button 
          :type="isLiked ? 'warning' : 'default'" 
          :icon="isLiked ? 'StarFilled' : 'Star'" 
          @click="handleLike"
          size="large"
          round
        >
          {{ isLiked ? '已收藏' : '收藏文章' }} ({{ likeCount }})
        </el-button>
      </div>

      <!-- 评论区 -->
      <div class="comment-section">
        <h3>💬 社区讨论 ({{ comments.length }})</h3>
        
        <div class="comment-input">
          <el-input
            v-model="newComment"
            type="textarea"
            :rows="3"
            placeholder="写下你的想法，一起交流健康心得..."
          />
          <div class="submit-btn">
            <el-button type="primary" @click="submitComment">发表评论</el-button>
          </div>
        </div>

        <div class="comment-list">
          <div v-for="c in comments" :key="c.id" class="comment-item">
            <el-avatar :size="40" :src="c.userAvatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
            <div class="comment-info">
              <div class="user-meta">
                <span class="nickname">{{ c.userName }}</span>
                <span class="time">{{ formatDate(c.created_at) }}</span>
              </div>
              <p class="content">{{ c.content }}</p>
            </div>
          </div>
          <el-empty v-if="comments.length === 0" description="暂无评论，快来抢沙发吧~" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, StarFilled } from '@element-plus/icons-vue'
import axios from 'axios'

const route = useRoute()
const post = ref<any>(null)
const comments = ref([])
const isLiked = ref(false)
const likeCount = ref(0)
const loading = ref(false)
const newComment = ref('')

const fetchPost = async () => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  loading.value = true
  try {
    const res = await axios.get(`http://localhost:8080/api/posts/${route.params.id}?userId=${user.id || ''}`)
    post.value = res.data.post
    comments.value = res.data.comments
    isLiked.value = res.data.isLiked
    likeCount.value = res.data.likeCount
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
}

const handleLike = async () => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  if (!user.id) {
    ElMessage.warning('请先登录再进行操作')
    return
  }
  
  try {
    const res = await axios.post('http://localhost:8080/api/posts/like', {
      userId: user.id,
      postId: post.value.id
    })
    if (res.data === 'liked') {
      isLiked.value = true
      likeCount.value++
      ElMessage.success('已添加到收藏')
    } else {
      isLiked.value = false
      likeCount.value--
      ElMessage.info('已取消收藏')
    }
  } catch (err) {
    ElMessage.error('操作失败')
  }
}

const submitComment = async () => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  if (!user.id) {
    ElMessage.warning('请先登录再发表评论')
    return
  }
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  try {
    await axios.post('http://localhost:8080/api/posts/comment', {
      postId: post.value.id,
      userId: user.id,
      content: newComment.value
    })
    ElMessage.success('评论发表成功')
    newComment.value = ''
    fetchPost()
  } catch (err) {
    ElMessage.error('评论发表失败')
  }
}

const formatDate = (date: string) => {
  return new Date(date).toLocaleString()
}

onMounted(fetchPost)
</script>

<style scoped>
.post-detail { max-width: 900px; margin: 0 auto; padding: 20px 0; }
.content-wrapper { background: #fff; padding: 40px; border-radius: 8px; box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05); }
.header { margin: 30px 0; }
.header h1 { font-size: 32px; color: #333; margin-bottom: 20px; }
.meta { color: #999; font-size: 14px; }
.meta span { margin-right: 20px; }
.main-image { width: 100%; height: 400px; border-radius: 8px; margin-bottom: 30px; }
.article-body { line-height: 1.8; font-size: 18px; color: #444; margin-bottom: 40px; }
.article-body p { margin-bottom: 1.5em; text-indent: 2em; }

.action-bar { text-align: center; margin: 40px 0; padding-bottom: 40px; border-bottom: 1px solid #eee; }

.comment-section h3 { margin-bottom: 20px; color: #333; }
.comment-input { margin-bottom: 30px; }
.submit-btn { margin-top: 10px; text-align: right; }
.comment-item { display: flex; margin-bottom: 20px; padding-bottom: 20px; border-bottom: 1px dotted #eee; }
.comment-info { flex: 1; margin-left: 15px; }
.user-meta { margin-bottom: 5px; }
.nickname { font-weight: bold; color: #409EFF; margin-right: 15px; }
.time { font-size: 12px; color: #999; }
.content { color: #555; line-height: 1.6; }

.image-placeholder { background: #f5f7fa; height: 100%; display: flex; align-items: center; justify-content: center; color: #999; }
</style>
