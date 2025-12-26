<template>
  <div class="publish-view">
    <div class="header">
      <h2>✨ 创作中心</h2>
      <el-radio-group v-model="contentType" size="large" style="margin-bottom: 20px;">
        <el-radio-button label="recipe">发布食谱</el-radio-button>
        <el-radio-button label="post">发布文章</el-radio-button>
      </el-radio-group>
    </div>
    <el-divider />

    <!-- 食谱发布表单 -->
    <div v-if="contentType === 'recipe'" class="form-container">
      <el-form :model="recipeForm" label-width="100px" label-position="top">
        <el-card class="form-section">
          <template #header><span>1. 食谱基础信息</span></template>
          <el-form-item label="标题" required>
            <el-input v-model="recipeForm.title" placeholder="输入食谱名称" />
          </el-form-item>
          <el-form-item label="描述">
            <el-input v-model="recipeForm.description" type="textarea" :rows="3" />
          </el-form-item>
        </el-card>

        <el-card class="form-section">
          <template #header>
            <div class="card-header">
              <span>2. 食材与步骤</span>
              <el-button type="primary" size="small" @click="addIngredient">添加食材</el-button>
            </div>
          </template>
          <div v-for="(ing, idx) in recipeForm.ingredients" :key="idx" class="dynamic-item">
            <el-input v-model="ing.name" placeholder="食材" style="width: 150px; margin-right: 10px;" />
            <el-input v-model="ing.amount" placeholder="用量" style="width: 100px; margin-right: 10px;" />
            <el-button type="danger" link icon="Delete" @click="recipeForm.ingredients.splice(idx, 1)" />
          </div>
        </el-card>

        <div class="submit-area">
          <el-button type="primary" size="large" @click="submitRecipe(false)">立即发布</el-button>
          <el-button size="large" @click="submitRecipe(true)">保存草稿</el-button>
        </div>
      </el-form>
    </div>

    <!-- 文章发布表单 -->
    <div v-else class="form-container">
      <el-form :model="postForm" label-width="100px" label-position="top">
        <el-card class="form-section">
          <el-form-item label="文章标题" required>
            <el-input v-model="postForm.title" placeholder="写个吸引人的标题" />
          </el-form-item>
          <el-form-item label="正文内容" required>
            <el-input v-model="postForm.content" type="textarea" :rows="15" placeholder="分享你的健康心得..." />
          </el-form-item>
          <el-form-item label="封面图 URL">
            <el-input v-model="postForm.coverImage" placeholder="http://..." />
          </el-form-item>
        </el-card>

        <div class="submit-area">
          <el-button type="success" size="large" @click="submitPost(false)">发布文章</el-button>
          <el-button size="large" @click="submitPost(true)">保存草稿</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const route = useRoute()
const contentType = ref('recipe')

const recipeForm = reactive({
  id: null,
  title: '',
  description: '',
  difficulty: 'NORMAL',
  cookingTime: 30,
  ingredients: [{ name: '', amount: '' }],
  steps: ['']
})

const postForm = reactive({
  id: null,
  title: '',
  content: '',
  coverImage: '',
  status: 'PUBLISHED'
})

const addIngredient = () => recipeForm.ingredients.push({ name: '', amount: '' })

onMounted(async () => {
  if (route.query.id && route.query.type) {
    contentType.value = route.query.type as string
    if (contentType.value === 'recipe') {
      const res = await axios.get(`http://localhost:8080/api/recipes/${route.query.id}`)
      Object.assign(recipeForm, res.data.detail)
      recipeForm.ingredients = res.data.ingredients
      recipeForm.steps = res.data.steps.map((s: any) => s.instruction)
    } else {
      const res = await axios.get(`http://localhost:8080/api/posts/my?userId=${JSON.parse(localStorage.getItem('user')!).id}`)
      const post = res.data.find((p: any) => p.id == route.query.id)
      if (post) Object.assign(postForm, post)
    }
  }
})

const submitRecipe = async (isDraft: boolean) => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  const res = await axios.post('http://localhost:8080/api/recipes/publish', {
    ...recipeForm,
    authorId: user.id,
    isDraft
  })
  ElMessage.success(res.data)
  router.push('/my-recipes')
}

const submitPost = async (isDraft: boolean) => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  const res = await axios.post('http://localhost:8080/api/posts/publish', {
    ...postForm,
    authorId: user.id,
    status: isDraft ? 'DRAFT' : 'PUBLISHED'
  })
  ElMessage.success(res.data)
  router.push('/my-recipes')
}
</script>

<style scoped>
.publish-view { padding: 20px 0; }
.header { text-align: center; }
.form-container { max-width: 800px; margin: 0 auto; }
.form-section { margin-bottom: 20px; }
.submit-area { text-align: center; margin-top: 30px; }
.dynamic-item { margin-bottom: 10px; display: flex; align-items: center; }
</style>
