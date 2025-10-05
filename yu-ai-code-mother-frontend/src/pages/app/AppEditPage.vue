<template>
  <div id="appEditPage">
    <a-space direction="vertical" style="width: 100%">
      <a-page-header title="编辑应用信息" @back="goBack" />

      <!-- 基本信息 -->
      <a-card title="基本信息">
        <div class="form-wrap">
          <a-form :model="form" layout="vertical" @finish="onSubmit">
            <a-form-item label="应用名称" name="appName" :rules="[{ required: true, message: '请输入应用名称' }]">
              <a-input v-model:value="form.appName" :maxlength="50" placeholder="请输入应用名称" allow-clear style="width: 520px">
                <template #suffix>
                  <span class="input-count">{{ nameCount }} / 50</span>
                </template>
              </a-input>
            </a-form-item>

            <!-- 封面地址 -->
            <a-form-item label="封面地址" name="cover">
              <a-input v-model:value="form.cover" placeholder="支持图片链接，建议尺寸：400x300" :disabled="!isAdmin" style="width: 520px" />
            </a-form-item>

            <!-- 封面预览：仅当有地址时显示 -->
            <a-form-item v-if="form.cover || appInfo?.cover" label="封面图预览">
              <div class="cover-preview">
                <a-image :src="form.cover || appInfo?.cover || ''" :preview="false" style="width: 400px; height: 300px; object-fit: cover;" />
              </div>
            </a-form-item>

            <!-- 优先级：仅管理员可见且可编辑 -->
            <a-form-item v-if="isAdmin" label="优先级" name="priority" extra="设置为99表示精选应用">
              <a-input-number v-model:value="form.priority" :min="0" style="width: 160px" />
            </a-form-item>

            <!-- 初始提示词（只读） -->
            <a-form-item label="初始提示词">
              <a-textarea :value="appInfo?.initPrompt || ''" :auto-size="{ minRows: 4, maxRows: 10 }" placeholder="初始提示词不可修改" disabled style="width: 520px" />
            </a-form-item>

            <a-form-item label="生成类型">
              <a-input :value="appInfo?.codeGenType || ''" disabled style="width: 520px" />
            </a-form-item>

            <a-form-item label="部署密钥">
              <a-input :value="appInfo?.deployKey || ''" disabled style="width: 520px" />
            </a-form-item>

            <a-space>
              <a-button type="primary" html-type="submit" :loading="saving">保存修改</a-button>
              <a-button @click="resetForm">重置</a-button>
              <a-button type="default" @click="goToChat">进入对话</a-button>
            </a-space>
          </a-form>
        </div>
      </a-card>

      <!-- 应用信息 -->
      <a-card title="应用信息">
        <a-descriptions bordered :column="1">
          <a-descriptions-item label="应用ID">{{ appInfo?.id }}</a-descriptions-item>
          <a-descriptions-item label="创建时间">{{ formatTime(appInfo?.createTime) }}</a-descriptions-item>
          <a-descriptions-item label="部署时间">{{ formatTime(appInfo?.deployedTime) }}</a-descriptions-item>
          <a-descriptions-item label="访问链接">
            <a-typography-paragraph>
              <a :href="previewLink" target="_blank">查看预览</a>
            </a-typography-paragraph>
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
    </a-space>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { getAppVoById, getAppVoByIdByAdmin, updateApp, updateAppByAdmin } from '@/api/appController'
import { getDeployUrl, getStaticPreviewUrl } from '@/config/env'
import { formatTime } from '@/utils/time'

const route = useRoute()
const router = useRouter()
const loginUserStore = useLoginUserStore()

const isAdmin = computed(() => loginUserStore.loginUser.userRole === 'admin')

// 用于展示的应用信息
const appInfo = ref<API.AppVO>()

// 表单（仅包含允许修改的字段）
const form = reactive<API.AppAdminUpdateRequest>({
  id: undefined,
  appName: '',
  cover: '',
  priority: undefined,
})

const original = ref<API.AppAdminUpdateRequest | null>(null)

const saving = ref(false)
const nameCount = computed(() => (form.appName ? form.appName.length : 0))

const fetchDetail = async () => {
  const id = String(route.params.id)
  if (!id) {
    message.error('缺少应用ID')
    router.push('/')
    return
  }
  try {
    const res = isAdmin.value ? await getAppVoByIdByAdmin({ id }) : await getAppVoById({ id })
    if (res.data.code === 0 && res.data.data) {
      const app = res.data.data
      appInfo.value = app

      form.id = app.id
      form.appName = app.appName || ''
      form.cover = app.cover || ''
      form.priority = app.priority

      original.value = { ...form }

      // 权限校验：普通用户只能编辑自己的应用
      if (!isAdmin.value && app.userId !== loginUserStore.loginUser.id) {
        message.error('无权限编辑该应用')
        router.push('/')
      }
    } else {
      message.error('获取应用信息失败：' + res.data.message)
      router.push('/')
    }
  } catch (e) {
    console.error(e)
    message.error('获取应用信息失败')
    router.push('/')
  }
}

const onSubmit = async () => {
  if (!form.id) return
  saving.value = true
  try {
    const res = isAdmin.value
      ? await updateAppByAdmin({
          id: form.id,
          appName: form.appName,
          cover: form.cover,
          priority: form.priority,
        })
      : await updateApp({ id: form.id, appName: form.appName })
    if (res.data.code === 0) {
      message.success('保存成功')
      // 更新展示信息
      if (appInfo.value) {
        appInfo.value.appName = form.appName
        appInfo.value.cover = form.cover
        appInfo.value.priority = form.priority
      }
    } else {
      message.error('保存失败：' + res.data.message)
    }
  } catch (e) {
    console.error(e)
    message.error('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

const resetForm = () => {
  if (original.value) {
    form.appName = original.value.appName || ''
    form.cover = original.value.cover || ''
    form.priority = original.value.priority
  }
}

const goBack = () => {
  if (isAdmin.value) {
    router.push('/admin/appManage')
  } else {
    router.push({ path: `/app/chat/${String(form.id)}`, query: { view: '1' } })
  }
}

const goToChat = () => {
  router.push({ path: `/app/chat/${String(form.id)}`, query: { view: '1' } })
}

const previewLink = computed(() => {
  if (!appInfo.value) return ''
  if (appInfo.value.deployKey) {
    return getDeployUrl(appInfo.value.deployKey)
  }
  return getStaticPreviewUrl(appInfo.value.codeGenType || 'multi_file', String(appInfo.value.id))
})

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
#appEditPage {
  /* 左对齐整体布局 */
  max-width: 100%;
  margin: 0;
  padding-left: 24px;
}
.form-wrap {
  /* 控制表单内容区域宽度，左对齐 */
  width: 560px;
}
.form-wrap :deep(.ant-form-item) {
  margin-bottom: 16px;
}
.input-count {
  color: #999;
}
.cover-preview {
  /* 预览容器，确保图片尺寸一致 */
  width: 400px;
  height: 300px;
  border-radius: 6px;
  overflow: hidden;
}
</style>