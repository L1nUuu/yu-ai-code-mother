<template>
  <div class="user-info">
    <a-avatar :src="avatarSrc" :size="size" />
    <span v-if="showName" class="user-name">{{ user?.userName || '未知用户' }}</span>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import defaultAvatar from '@/assets/aiAvatar.png'

interface Props {
  user?: API.UserVO
  size?: number | 'small' | 'default' | 'large'
  showName?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  size: 'default',
  showName: true,
})

const avatarSrc = computed(() => {
  const url = props.user?.userAvatar
  return url
    ? `https://image.baidu.com/search/down?url=${encodeURIComponent(url)}`
    : defaultAvatar
})
</script>

<style scoped>
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-name {
  font-size: 14px;
  color: #1a1a1a;
}
</style>
