<template>
  <Layout class="basic-layout">
    <!-- 顶部导航栏 -->
    <GlobalHeader />
    
    <!-- 内容区域 -->
    <Content class="content">
      <div class="content-container">
        <RouterView />
      </div>
    </Content>
    
    <!-- 底部版权信息 -->
    <GlobalFooter />
  </Layout>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
import { Layout } from 'ant-design-vue'
import GlobalHeader from '@/components/GlobalHeader.vue'
import GlobalFooter from '@/components/GlobalFooter.vue'

const { Content } = Layout
</script>

<style scoped>
.basic-layout {
  min-height: 100vh;
}

.content {
  padding: 80px 24px 80px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 144px);
}

.content-container {
  max-width: 1440px;
  margin: 0 auto;
  background-color: #fff;
  border-radius: 2px;
  padding: 24px;
  min-height: 280px;
}

@media (max-width: 768px) {
  .content {
    padding: 80px 16px 80px;
  }
  
  .content-container {
    padding: 16px;
  }
}
</style>