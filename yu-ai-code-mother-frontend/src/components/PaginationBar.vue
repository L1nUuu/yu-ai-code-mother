<template>
  <a-pagination
    :current="safeCurrent"
    :pageSize="safePageSize"
    :total="total"
    :showTotal="(t: number) => `共 ${t} 个${totalLabel}`"
    @change="(page: number) => emit('change', page)"
  />
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  current?: number
  pageSize?: number
  total: number
  totalLabel?: string
}

const props = withDefaults(defineProps<Props>(), {
  totalLabel: '应用',
})

const safeCurrent = computed(() => props.current ?? 1)
const safePageSize = computed(() => props.pageSize ?? 10)

const emit = defineEmits<{
  (e: 'change', page: number): void
}>()
</script>

<style scoped>
/* 可在此添加统一分页样式 */
</style>