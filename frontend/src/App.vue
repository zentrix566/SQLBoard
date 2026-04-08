<template>
  <div id="app">
    <!-- 登录页面不显示侧边栏和头部 -->
    <template v-if="$route.path === '/login'">
      <router-view />
    </template>
    <!-- 其他页面显示侧边栏和头部 -->
    <template v-else>
      <el-container>
        <el-aside width="200px">
          <el-menu
            :default-active="$route.path"
            router
            background-color="#304156"
            text-color="#bfcbd9"
            active-text-color="#409EFF"
            style="height: 100%"
          >
            <el-menu-item index="/sql">
              <el-icon><Grid /></el-icon>
              <span>SQL执行</span>
            </el-menu-item>
            <el-menu-item index="/connections">
              <el-icon><Connection /></el-icon>
              <span>数据源管理</span>
            </el-menu-item>
            <el-menu-item index="/saved-operations">
              <el-icon><Document /></el-icon>
              <span>常用操作</span>
            </el-menu-item>
            <el-menu-item index="/history">
              <el-icon><Timer /></el-icon>
              <span>操作历史</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-container>
          <el-header>
            <div style="display: flex; justify-content: space-between; align-items: center; height: 60px;">
              <h2 style="color: white; margin: 0; line-height: 60px">SQLBoard</h2>
              <div v-if="username" style="color: white; display: flex; align-items: center; gap: 16px;">
                <span>{{ nickname || username }}</span>
                <el-button type="text" style="color: white" @click="handleLogout">退出登录</el-button>
              </div>
            </div>
          </el-header>
          <el-main>
            <router-view />
          </el-main>
        </el-container>
      </el-container>
    </template>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { Grid, Connection, Document, Timer } from '@element-plus/icons-vue'
import { logout } from './api/auth'
import { useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const username = ref(localStorage.getItem('username') || '')
const nickname = ref(localStorage.getItem('nickname') || '')

const handleLogout = () => {
  logout()
  username.value = ''
  nickname.value = ''
  router.push('/login')
}
</script>

<style>
#app {
  height: 100vh;
}

el-aside {
  background-color: #304156;
}

el-header {
  background-color: #409EFF;
}
</style>
