<template>
  <el-container class="layout-container">
    <el-header class="header">
      <div class="logo" @click="goHome">
        <img src="https://cp1.douguo.com/static/static/index/img/logo.png" alt="Logo" @error="handleLogoError" />
        <span>健康饮食交流平台</span>
      </div>
      
      <!-- 主导航菜单 -->
      <el-menu :default-active="route.path" mode="horizontal" :ellipsis="false" class="menu" @select="handleSelect">
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/recipes">精选菜谱</el-menu-item>
        <el-menu-item index="/community">健康社区</el-menu-item>
        <el-menu-item index="/shares">食材共享</el-menu-item>
        <div class="flex-grow" />
        
        <el-menu-item index="/publish">发布食谱</el-menu-item>

        <!-- 右上角用户信息区域 -->
        <div class="user-area">
          <template v-if="!currentUser">
            <el-button type="primary" link @click="router.push('/login')">登录 | 注册</el-button>
          </template>
          <template v-else>
            <el-dropdown @command="handleCommand">
              <span class="user-link">
                <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" style="margin-right: 8px" />
                {{ currentUser.username }}
                <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="my-recipes">我的作品</el-dropdown-item>
                  <el-dropdown-item command="favorites">我的收藏</el-dropdown-item>
                  <el-dropdown-item v-if="currentUser.role === 'ADMIN'" command="admin">系统管理</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </div>
      </el-menu>
    </el-header>

    <el-main class="main-content">
      <router-view />
    </el-main>
    
    <el-footer class="footer">
      <p>© 2025 健康饮食交流平台 - 数据库课程大作业演示</p>
    </el-footer>
  </el-container>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const currentUser = ref<any>(null)

const checkUser = () => {
  const userStr = localStorage.getItem('user')
  currentUser.value = userStr ? JSON.parse(userStr) : null
}

onMounted(() => {
  checkUser()
})

// 监听路由变化，以便在登录成功跳转后立即刷新用户信息
watch(() => route.path, () => {
  checkUser()
})

const handleSelect = (key: string) => {
  if (key.startsWith('/')) {
    router.push(key)
  }
}

const handleCommand = (command: string) => {
  if (command === 'logout') {
    localStorage.removeItem('user')
    currentUser.value = null
    ElMessage.success('已退出登录')
    router.push('/')
  } else if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'my-recipes') {
    router.push('/my-recipes')
  } else if (command === 'favorites') {
    router.push('/favorites')
  } else if (command === 'admin') {
    router.push('/admin')
  }
}

const goHome = () => {
  router.push('/')
}

const handleLogoError = (e: any) => {
  e.target.style.display = 'none'
}
</script>

<style>
body {
  margin: 0;
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
}
</style>

<style scoped>
.layout-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.header {
  display: flex;
  align-items: center;
  border-bottom: 1px solid #eee;
  padding: 0 50px;
  background-color: white;
  position: sticky;
  top: 0;
  z-index: 100;
}
.logo {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: bold;
  color: #ff9d00;
  margin-right: 40px;
  cursor: pointer;
}
.logo img {
  height: 35px;
  margin-right: 10px;
}
.menu {
  flex: 1;
  border-bottom: none;
  display: flex;
  align-items: center;
}
.flex-grow {
  flex-grow: 1;
}
.user-area {
  display: flex;
  align-items: center;
  margin-left: 20px;
}
.user-link {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #606266;
  outline: none;
}
.main-content {
  padding: 20px 50px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}
.footer {
  text-align: center;
  color: #999;
  border-top: 1px solid #eee;
  padding: 20px 0;
  margin-top: auto;
}
</style>
