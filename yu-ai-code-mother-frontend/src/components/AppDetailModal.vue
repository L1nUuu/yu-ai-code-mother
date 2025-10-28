<template>
  <a-modal v-model:open="visible" title="应用详情" :footer="null" width="500px">
    <div class="app-detail-content">
      <!-- 应用基础信息 -->
      <div class="app-basic-info">
        <div class="info-item">
          <span class="info-label">创建者：</span>
          <UserInfo :user="app?.user" size="small" />
        </div>
        <div class="info-item">
          <span class="info-label">生成类型：</span>
          <a-tag :color="codeGenMeta.color" class="gen-type">
            <component :is="codeGenMeta.icon" class="gen-type__icon" />
            <span class="gen-type__text">{{ codeGenMeta.label }}</span>
          </a-tag>
        </div>
        <div class="info-item">
          <span class="info-label">创建时间：</span>
          <span>{{ formatTime(app?.createTime) }}</span>
        </div>
      </div>

      <!-- 操作栏（仅本人或管理员可见） -->
      <div v-if="showActions" class="app-actions">
        <a-space>
          <a-button type="primary" @click="handleEdit">
            <template #icon>
              <EditOutlined />
            </template>
            修改
          </a-button>
          <a-popconfirm
            title="确定要删除这个应用吗？"
            @confirm="handleDelete"
            ok-text="确定"
            cancel-text="取消"
          >
            <a-button danger>
              <template #icon>
                <DeleteOutlined />
              </template>
              删除
            </a-button>
          </a-popconfirm>
        </a-space>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { Component } from 'vue'
import { EditOutlined, DeleteOutlined, CodeOutlined, FolderOpenOutlined, ProjectOutlined } from '@ant-design/icons-vue'
import UserInfo from './UserInfo.vue'
import { formatTime } from '@/utils/time'
import { formatCodeGenType, CodeGenTypeEnum } from '@/utils/codeGenTypes'

interface Props {
  open: boolean
  app?: API.AppVO
  showActions?: boolean
}

interface Emits {
  (e: 'update:open', value: boolean): void
  (e: 'edit'): void
  (e: 'delete'): void
}

const props = withDefaults(defineProps<Props>(), {
  showActions: false,
})

const emit = defineEmits<Emits>()

const visible = computed({
  get: () => props.open,
  set: (value) => emit('update:open', value),
})

// 生成类型展示元信息（颜色 + 图标 + 文案）
const codeGenMeta = computed<{ label: string; color: string; icon: Component }>(() => {
  const type = props.app?.codeGenType as CodeGenTypeEnum | undefined
  const label = formatCodeGenType(type)
  let color: string = 'default'
  let icon: Component = CodeOutlined as Component

  switch (type) {
    case CodeGenTypeEnum.HTML:
      color = 'cyan'
      icon = CodeOutlined as Component
      break
    case CodeGenTypeEnum.MULTI_FILE:
      color = 'purple'
      icon = FolderOpenOutlined as Component
      break
    case CodeGenTypeEnum.VUE_PROJECT:
      color = 'geekblue'
      icon = ProjectOutlined as Component
      break
    default:
      color = 'default'
      icon = CodeOutlined as Component
  }

  return { label, color, icon }
})

const handleEdit = () => {
  emit('edit')
}

const handleDelete = () => {
  emit('delete')
}
</script>

<style scoped>
.app-detail-content {
  padding: 8px 0;
}

.app-basic-info {
  margin-bottom: 24px;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.info-label {
  width: 80px;
  color: #666;
  font-size: 14px;
  flex-shrink: 0;
}

.gen-type {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 2px 8px;
  border-radius: 4px;
}

.gen-type__icon {
  font-size: 14px;
}

.gen-type__text {
  line-height: 1;
}

.app-actions {
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}
</style>
