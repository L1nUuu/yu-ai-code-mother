<script setup lang="ts">
import BasicLayout from './layouts/BasicLayout.vue'
// 导入Ant Design Vue的样式和组件
import 'ant-design-vue/dist/reset.css'
import { ConfigProvider } from 'ant-design-vue'
import zhCN from 'ant-design-vue/locale/zh_CN'

import { healthCheck } from '@/api/healthController.ts'
healthCheck().then((res) => {
  console.log(res)
})

</script>

<template>
  <ConfigProvider :locale="zhCN">
    <BasicLayout />
  </ConfigProvider>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

body {
  margin: 0;
  padding: 0;
}

#app {
  min-height: 100vh;
}
</style>
