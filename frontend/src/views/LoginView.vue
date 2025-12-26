<template>
  <div class="login-view">
    <div class="login-container">
      <h2>用户登录</h2>
      <el-form :model="loginForm" label-width="0" class="login-form">
        <el-form-item>
          <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        
        <el-form-item>
          <el-input v-model="loginForm.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" style="width: 100%" @click="handleLogin">登录</el-button>
        </el-form-item>
        
        <el-form-item>
          <el-button style="width: 100%" @click="handleRegister">注册新账户</el-button>
        </el-form-item>
      </el-form>
      
      <el-alert title="演示提示" type="info" :closable="false" style="margin-top: 20px;">
        <p>登录功能演示版</p>
        <p>数据库中已有账户：</p>
        <ul>
          <li>用户名: admin / 密码: admin (管理员)</li>
          <li>用户名: testuser / 密码: test (普通用户)</li>
        </ul>
      </el-alert>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()

const loginForm = reactive({
  username: '',
  password: ''
})

const handleLogin = () => {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  
  // 演示模式：模拟存储登录用户信息
  const mockUser = {
    id: loginForm.username === 'admin' ? 1 : 2,
    username: loginForm.username,
    role: loginForm.username === 'admin' ? 'ADMIN' : 'USER'
  }
  localStorage.setItem('user', JSON.stringify(mockUser))
  
  ElMessage.success('登录成功！')
  setTimeout(() => {
    router.push('/')
  }, 1000)
}

const handleRegister = () => {
  ElMessage.info('演示模式：可以调用数据库存储过程 sp_register_user 进行注册')
}
</script>

<style scoped>
.login-view {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}
.login-container {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}
.login-container h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}
.login-form {
  margin-top: 20px;
}
.login-view ul {
  margin-top: 10px;
  padding-left: 20px;
  font-size: 12px;
}
.login-view li {
  margin: 5px 0;
}
</style>



