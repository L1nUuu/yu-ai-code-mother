<template>
  <Header class="header">
    <div class="header-container">
      <div class="header-left">
        <div class="logo">
          <img src="@/assets/logo.png" alt="Logo" class="logo-img" />
          <span class="logo-text">编程导航</span>
        </div>
      </div>
      <div class="header-right">
        <Button type="primary">登录</Button>
      </div>
    </div>
  </Header>
</template>

<script setup lang="ts">
import { Layout, Button } from 'ant-design-vue'

const { Header } = Layout

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