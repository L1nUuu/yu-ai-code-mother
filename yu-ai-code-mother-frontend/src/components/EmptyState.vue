<template>
  <a-empty :description="description" />
</template>

<script setup lang="ts">
interface Props {
  description: string
}

defineProps<Props>()
</script>

<style scoped>
/* 可根据需求扩展统一的空状态样式 */
</style>