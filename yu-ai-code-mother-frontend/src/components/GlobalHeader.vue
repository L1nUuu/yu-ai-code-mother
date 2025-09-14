<template>
  <Header class="header">
    <div class="header-container">
      <!-- 左侧 Logo 和标题 -->
      <div class="header-left">
        <div class="logo" @click="goHome">
          <img src="@/assets/logo.png" alt="Logo" class="logo-img" />
          <span class="logo-text">编程导航</span>
        </div>
        
        <!-- 菜单项 -->
        <Menu
          :selectedKeys="[currentPath]"
          mode="horizontal"
          class="header-menu"
          :items="menuItems"
          @click="handleMenuClick"
        />
      </div>
      
      <!-- 右侧用户信息 -->
      <div class="header-right">
        <Button type="primary" @click="handleLogin">登录</Button>
      </div>
    </div>
  </Header>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { Menu, Button, Layout } from 'ant-design-vue'
import type { MenuProps } from 'ant-design-vue'

const { Header } = Layout

const router = useRouter()
const currentPath = ref<string>('')

// 菜单项配置
const menuItems: MenuProps['items'] = [
  {
    key: '/',
    label: '首页'
  },
  {
    key: '/about',
    label: '关于我们'
  }
]

// 处理菜单点击
const handleMenuClick = (e: any) => {
  // 确保key是字符串类型
  const path = String(e.key)
  router.push(path)
}

// 处理登录
const handleLogin = () => {
  // 登录逻辑
  console.log('登录点击')
}

// 点击logo返回首页
const goHome = () => {
  router.push('/')
}

// 组件挂载时设置当前路径并监听路由变化
onMounted(() => {
  // 设置初始路径
  currentPath.value = router.currentRoute.value.path
  
  // 监听路由变化
  const routeWatcher = router.afterEach((to) => {
    currentPath.value = to.path
  })
  
  // 组件卸载时移除监听器
  onBeforeUnmount(() => {
    routeWatcher()
  })
})
</script>

<style scoped>
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.09);
}

.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 64px;
  padding: 0 24px;
  max-width: 1440px;
  margin: 0 auto;
}

.header-left {
  display: flex;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  margin-right: 32px;
  cursor: pointer;
}

.logo-img {
  width: 32px;
  height: 32px;
  margin-right: 8px;
}

.logo-text {
  font-size: 20px;
  font-weight: bold;
  color: #1890ff;
}

.header-menu {
  border-bottom: none !important;
}

.header-right {
  display: flex;
  align-items: center;
}

@media (max-width: 768px) {
  .header-container {
    padding: 0 16px;
  }
  
  .logo {
    margin-right: 16px;
  }
  
  .logo-text {
    display: none;
  }
}
</style>