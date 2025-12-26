<template>
  <div class="recipe-detail" v-if="!loading">
    <!-- 头部信息 -->
    <div class="recipe-header">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-image :src="detail.coverImage" class="cover-image" fit="cover">
            <template #error>
              <div class="image-slot">图片加载失败</div>
            </template>
          </el-image>
        </el-col>
        <el-col :span="12">
          <h1>{{ detail.title }}</h1>
          <div class="meta-info">
            <el-tag :type="difficultyType">{{ difficultyText }}</el-tag>
            <span class="time">⏱ {{ detail.cookingTime }}分钟</span>
          </div>
          <div class="author-info">
            <span class="author">作者：{{ detail.authorName }}</span>
          </div>
          <div class="stats">
            <el-statistic title="点赞收藏" :value="detail.likeCount">
              <template #suffix>
                <el-icon v-if="isLiked" color="#f56c6c"><StarFilled /></el-icon>
                <el-icon v-else><Star /></el-icon>
              </template>
            </el-statistic>
            <el-statistic title="用户评论" :value="detail.commentCount" />
          </div>
          <div class="actions">
            <el-button 
              :type="isLiked ? 'danger' : 'info'" 
              :icon="isLiked ? 'StarFilled' : 'Star'" 
              @click="handleLike"
              :plain="!isLiked"
            >
              {{ isLiked ? '已收藏' : '收藏食谱' }}
            </el-button>
            <el-button type="primary" icon="ChatDotRound" @click="showCommentDialog = true">发表评论</el-button>
          </div>
        </el-col>
      </el-row>
    </div>

    <el-divider />

    <!-- 食材清单 -->
    <div class="section">
      <h2>🥗 所需食材</h2>
      <el-card shadow="never">
        <el-row :gutter="20">
          <el-col :span="8" v-for="(ing, idx) in ingredients" :key="idx" class="ingredient-item">
            <el-tag type="success" effect="light" round>{{ ing.name }}</el-tag>
            <span class="amount">{{ ing.amount }}</span>
          </el-col>
        </el-row>
      </el-card>
    </div>

    <!-- 烹饪步骤 -->
    <div class="section">
      <h2>👨‍🍳 烹饪步骤</h2>
      <el-timeline>
        <el-timeline-item 
          v-for="step in steps" 
          :key="step.id"
          :timestamp="`步骤 ${step.stepNumber}`"
          placement="top"
        >
          <el-card shadow="hover">
            <p>{{ step.instruction }}</p>
            <el-image v-if="step.imageUrl" :src="step.imageUrl" style="width: 200px; margin-top: 10px; border-radius: 4px;" />
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </div>

    <!-- 评论区 -->
    <div class="section">
      <h2>💬 用户评论 ({{ comments.length }})</h2>
      <el-empty v-if="comments.length === 0" description="期待你的精彩点评..." />
      <div v-else class="comments-list">
        <el-card v-for="comment in comments" :key="comment.id" class="comment-card" shadow="never">
          <div class="comment-header">
            <span class="comment-user">用户 {{ comment.userId }}</span>
            <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
          </div>
          <p class="comment-content">{{ comment.content }}</p>
        </el-card>
      </div>
    </div>

    <!-- 评论输入弹窗 -->
    <el-dialog v-model="showCommentDialog" title="发表您的评论" width="500px">
      <el-input
        v-model="commentText"
        type="textarea"
        :rows="4"
        placeholder="写下你的真实评价，帮助更多小伙伴..."
      />
      <template #footer>
        <el-button @click="showCommentDialog = false">取消</el-button>
        <el-button type="primary" @click="handleComment">提交评论</el-button>
      </template>
    </el-dialog>
  </div>
  
  <div v-else class="loading-container">
    <el-icon class="is-loading" :size="40"><Loading /></el-icon>
    <p>正在努力加载美味详情...</p>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Loading, Star, StarFilled, ChatDotRound } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const detail = ref<any>({})
const steps = ref<any[]>([])
const ingredients = ref<any[]>([])
const comments = ref<any[]>([])

const showCommentDialog = ref(false)
const commentText = ref('')
const isLiked = ref(false)

const difficultyType = computed(() => {
  const map: any = { 'EASY': 'success', 'NORMAL': 'warning', 'HARD': 'danger' }
  return map[detail.value.difficulty] || 'info'
})

const difficultyText = computed(() => {
  const map: any = { 'EASY': '简单', 'NORMAL': '中等', 'HARD': '困难' }
  return map[detail.value.difficulty] || '未知'
})

const formatTime = (time: string) => {
  return new Date(time).toLocaleString('zh-CN')
}

const checkLogin = () => {
  const userStr = localStorage.getItem('user')
  if (!userStr) {
    ElMessage.warning('请先登录后再进行操作')
    router.push('/login')
    return null
  }
  return JSON.parse(userStr)
}

const handleLike = async () => {
  const user = checkLogin()
  if (!user) return

  try {
    const res = await axios.post('http://localhost:8080/api/recipes/like', {
      userId: user.id,
      recipeId: route.params.id,
      isCurrentlyLiked: isLiked.value
    })
    
    // 实时更新前端状态
    if (res.data.action === 'liked') {
      isLiked.value = true
      detail.value.likeCount++
      ElMessage.success('已加入我的收藏')
    } else {
      isLiked.value = false
      detail.value.likeCount--
      ElMessage.info('已取消收藏')
    }
  } catch (error) {
    ElMessage.error('操作失败，请重试')
  }
}

const handleComment = async () => {
  if (!commentText.value.trim()) {
    ElMessage.warning('评论内容不能为空哦')
    return
  }
  
  const user = checkLogin()
  if (!user) return

  try {
    const res = await axios.post('http://localhost:8080/api/recipes/comment', {
      userId: user.id,
      recipeId: route.params.id,
      content: commentText.value
    })
    ElMessage.success(res.data)
    showCommentDialog.value = false
    commentText.value = ''
    fetchDetail() // 刷新列表以显示新评论
  } catch (error) {
    ElMessage.error('发表评论失败')
  }
}

const fetchDetail = async () => {
  try {
    const res = await axios.get(`http://localhost:8080/api/recipes/${route.params.id}`)
    detail.value = res.data.detail
    steps.value = res.data.steps
    ingredients.value = res.data.ingredients
    comments.value = res.data.comments
    
    // 演示模式：随机设置一个收藏状态
    isLiked.value = detail.value.likeCount > 0
  } catch (error) {
    console.error('获取详情失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.recipe-detail {
  padding: 20px 0;
}
.recipe-header {
  margin-bottom: 30px;
}
.cover-image {
  width: 100%;
  height: 400px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}
.recipe-header h1 {
  margin-bottom: 20px;
  color: #333;
  font-size: 28px;
}
.meta-info {
  margin-bottom: 15px;
}
.meta-info .time {
  margin-left: 15px;
  color: #999;
}
.author-info {
  margin-bottom: 25px;
  color: #666;
  font-size: 16px;
}
.author {
  color: #ff9d00;
  font-weight: bold;
}
.stats {
  display: flex;
  gap: 60px;
  margin-bottom: 35px;
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
}
.actions {
  display: flex;
  gap: 15px;
}
.section {
  margin-top: 50px;
}
.section h2 {
  margin-bottom: 25px;
  color: #333;
  display: flex;
  align-items: center;
}
.ingredient-item {
  margin-bottom: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 15px;
}
.amount {
  color: #666;
  font-weight: 500;
}
.comments-list {
  margin-top: 20px;
}
.comment-card {
  margin-bottom: 15px;
  border-radius: 8px;
}
.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}
.comment-user {
  font-weight: bold;
  color: #409eff;
}
.comment-time {
  font-size: 12px;
  color: #999;
}
.comment-content {
  color: #444;
  line-height: 1.8;
}
.loading-container {
  text-align: center;
  padding: 100px 0;
}
</style>
