<template>
  <div id="homePage">
    <div class="page-gradient-bg" aria-hidden="true"></div>
    <!-- 顶部标题与提示词输入区域 -->
    <section class="hero">
      <!-- 新增顶部主/副标语 -->
      <div class="hero-slogans">
        <h1 class="title">AI 应用生成平台</h1>
        <p class="sub">一句话轻松创建网站应用</p>
      </div>
      <div class="prompt-box">
        <a-textarea
          v-model:value="prompt"
          :rows="4"
          placeholder="请描述你想生成的网站，越详细效果越好哦"
        />
        <a-button class="submit-arrow" type="primary" shape="circle" @click="createApp">
          <UpOutlined />
        </a-button>
        <!-- 删除内部的 prompt-actions/suggest-tags -->
      </div>
      <!-- 将提示块移到输入框外 -->
      <div class="suggest-tags suggest-tags--outside">
        <a-tag v-for="t in shortTags" :key="t.label" @click="prompt = t.full">{{ t.label }}</a-tag>
      </div>
    </section>

    <!-- 我的应用 -->
    <section class="section">
      <div class="section-header">
        <h2>我的作品</h2>
        <a-input
          v-model:value="mySearch.appName"
          placeholder="按名称搜索"
          style="width: 240px"
          @pressEnter="fetchMyApps"
        />
      </div>
      <a-row :gutter="[16, 24]">
        <a-col v-for="item in myApps" :key="item.id" :span="6">
          <AppCard :app="item" @view-chat="toChat" @view-work="openWork" />
        </a-col>
      </a-row>
      <EmptyState v-if="myApps.length === 0" description="暂无作品" />
      <div class="section-footer">
        <PaginationBar
          :current="mySearch.pageNum"
          :pageSize="mySearch.pageSize"
          :total="myTotal"
          totalLabel="应用"
          @change="onMyPageChange"
        />
      </div>
    </section>

    <!-- 精选应用 -->
    <section class="section">
      <div class="section-header">
        <h2>精选案例</h2>
        <a-input
          v-model:value="goodSearch.appName"
          placeholder="按名称搜索"
          style="width: 240px"
          @pressEnter="fetchGoodApps"
        />
      </div>
      <a-row :gutter="[16, 24]">
        <a-col v-for="item in goodApps" :key="item.id" :span="6">
          <AppCard :app="item" :featured="true" @view-chat="toChat" @view-work="openWork" />
        </a-col>
      </a-row>
      <EmptyState v-if="goodApps.length === 0" description="暂无精选案例" />
      <div class="section-footer">
        <PaginationBar
          :current="goodSearch.pageNum"
          :pageSize="goodSearch.pageSize"
          :total="goodTotal"
          totalLabel="案例"
          @change="onGoodPageChange"
        />
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import AppCard from '@/components/AppCard.vue'
import { addApp, listMyAppVoByPage, listGoodAppVoByPage } from '@/api/appController'
import { getDeployUrl, getStaticPreviewUrl } from '@/config/env'
import { UpOutlined } from '@ant-design/icons-vue'

import EmptyState from '@/components/EmptyState.vue'
import PaginationBar from '@/components/PaginationBar.vue'

const router = useRouter()

// 提示词
const prompt = ref('')
const creating = ref(false)

// 短标签 + 完整提示词映射，点击短标签填充完整文本到输入框
const shortTags = [
  { label: '个人博客', full: '请为我生成一个个人博客网站：包含首页、文章列表、分类与标签、归档页、关于我、站点搜索、RSS、SEO优化、深色主题与响应式布局；文章支持Markdown渲染与代码高亮，并集成评论与点赞功能。' },
  { label: '企业官网', full: '生成一个企业官网：包含公司简介、产品与服务页、案例展示、新闻资讯、联系我们（表单+邮箱）、地址与地图、SEO友好路由、PC与移动端自适应；设计风格专业、可信赖。' },
  { label: '小型商店', full: '搭建一个小型网上商店：含商品列表与详情、购物车与下单流程、订单页面、常见问题与售后支持、优惠券模块、SEO优化、响应式布局；商品图片需支持懒加载，整体风格简洁现代。' },
  { label: '简历作品集', full: '制作一个在线简历与作品集网站：含个人简介、技能与经历时间线、项目作品展示（可筛选）、联系方式表单、下载PDF简历、暗色模式、移动端友好；页面视觉简洁并突出重点。' },
]

// 创建应用
const createApp = async () => {
  if (!prompt.value.trim()) {
    message.warning('请先输入提示词')
    return
  }
  creating.value = true
  try {
    const res = await addApp({ initPrompt: prompt.value })
    if (res.data.code === 0 && res.data.data) {
      const id = res.data.data
      router.push(`/app/chat/${id}`)
    } else {
      message.error('创建失败：' + res.data.message)
    }
  } catch (e) {
    console.error(e)
    message.error('创建失败，请重试')
  } finally {
    creating.value = false
  }
}

// 我的应用列表
const myApps = ref<API.AppVO[]>([])
const myTotal = ref(0)
const mySearch = reactive<API.AppQueryRequest>({ pageNum: 1, pageSize: 20 })
const fetchMyApps = async () => {
  const res = await listMyAppVoByPage({ ...mySearch })
  if (res.data.code === 0 && res.data.data) {
    myApps.value = res.data.data.records || []
    myTotal.value = res.data.data.totalRow || 0
  } else {
    message.error('获取我的应用失败：' + res.data.message)
  }
}
const onMyPageChange = (page: number) => {
  mySearch.pageNum = page
  fetchMyApps()
}

// 精选应用列表
const goodApps = ref<API.AppVO[]>([])
const goodTotal = ref(0)
const goodSearch = reactive<API.AppQueryRequest>({ pageNum: 1, pageSize: 20 })
const fetchGoodApps = async () => {
  const res = await listGoodAppVoByPage({ ...goodSearch })
  if (res.data.code === 0 && res.data.data) {
    goodApps.value = res.data.data.records || []
    goodTotal.value = res.data.data.totalRow || 0
  } else {
    message.error('获取精选应用失败：' + res.data.message)
  }
}
const onGoodPageChange = (page: number) => {
  goodSearch.pageNum = page
  fetchGoodApps()
}

// 跳转到对话页
const toChat = (appId: string | undefined) => {
  if (!appId) return
  router.push({ path: `/app/chat/${appId}`, query: { view: '1' } })
}

// 查看作品（预览或已部署）
const openWork = (app: API.AppVO) => {
  let url = ''
  if (app.deployKey) {
    url = getDeployUrl(app.deployKey)
  } else if (app.id && app.codeGenType) {
    url = getStaticPreviewUrl(app.codeGenType, String(app.id))
  }
  if (url) {
    window.open(url, '_blank')
  } else {
    message.info('该应用暂未生成可预览内容')
  }
}

onMounted(() => {
  fetchMyApps()
  fetchGoodApps()
})
</script>

<style scoped>
#homePage {
  position: relative;
  /* z-index: 1; 移除以避免覆盖全局头部 */
  max-width: 1200px;
  margin: 0 auto;
}

.page-gradient-bg {
  position: fixed;
  inset: 0;
  width: 100vw;
  height: 100vh;
  background-image: linear-gradient(to top, #a8edea 0%, #fed6e3 100%);
  z-index: -1; /* 置于所有内容之后，避免遮挡导航 */
  pointer-events: none; /* 防止拦截点击事件 */
}

.hero {
  background: transparent;
  border-radius: 0;
  padding: 32px 24px;
  text-align: center;
}

.title {
  margin: 0 0 8px;
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
}

.sub {
  color: #475569;
  margin-bottom: 16px;
}

.prompt-box {
  background: #fff;
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 12px;
  padding: 16px;
  max-width: 800px;
  margin: 0 auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  position: relative;
}

/* 为右下角箭头预留空间 */
.prompt-box :deep(.ant-input) {
  border: none !important;
  box-shadow: none !important;
  background: transparent !important;
  padding-right: 52px; /* 防止文字被箭头遮挡 */
}

/* 提交箭头样式与定位 */
.submit-arrow {
  position: absolute;
  right: 20px;
  bottom: 20px;
  box-shadow: 0 6px 16px rgba(24, 144, 255, 0.35);
}
.prompt-actions {
  margin-top: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.suggest-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.section {
  margin-top: 24px;
}
.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
.section-footer {
  margin-top: 12px;
  display: flex;
  justify-content: center;
}

/* 优化输入框的边框与视觉效果 */
.prompt-box :deep(.ant-input) {
  border: none !important;
  box-shadow: none !important;
  background: transparent !important;
}

/* 新增样式块 */
.hero-slogans { text-align: center; margin-bottom: 16px; }
.title { margin: 0 0 8px; font-size: 32px; font-weight: 800; color: #0f172a; }
.sub { color: #475569; margin: 0; }

.prompt-box { background: #fff; border: 1px solid rgba(0,0,0,0.06); border-radius: 16px; padding: 16px; max-width: 800px; margin: 16px auto 0; box-shadow: 0 8px 32px rgba(0,0,0,0.08); position: relative; }
.prompt-box :deep(.ant-input) { border: none !important; box-shadow: none !important; background: transparent !important; padding-right: 52px; }
.submit-arrow { position: absolute; right: 20px; bottom: 20px; box-shadow: 0 6px 16px rgba(24,144,255,0.35); }

.suggest-tags { display: flex; gap: 8px; justify-content: center; flex-wrap: wrap; }

/* 标语区域优化 */
.hero-slogans { text-align: center; margin-bottom: 18px; }
.title { margin: 0 0 10px; font-size: 44px; font-weight: 900; line-height: 1.2; letter-spacing: 0.5px; color: #0f172a; }
.sub { color: #334155; font-size: 18px; margin: 0; }

/* 输入容器优化：更柔和的边框与阴影 */
.prompt-box { background: #fff; border: 1px solid rgba(15,23,42,0.08); border-radius: 20px; padding: 20px; max-width: 840px; margin: 18px auto 0; box-shadow: 0 8px 24px rgba(2,6,23,0.06); position: relative; }
.prompt-box :deep(.ant-input) { border: none !important; box-shadow: none !important; background: transparent !important; padding-right: 56px; }
.submit-arrow { position: absolute; right: 22px; bottom: 22px; box-shadow: 0 6px 16px rgba(24,144,255,0.28); }

/* 外部提示标签：居中排列的圆角胶囊 */
.suggest-tags { display: flex; gap: 10px; justify-content: center; flex-wrap: wrap; }
.suggest-tags--outside { margin-top: 14px; }
.suggest-tags--outside :deep(.ant-tag) { border-radius: 999px; padding: 6px 14px; background: rgba(255,255,255,0.75); border: 1px solid rgba(2,6,23,0.08); color: #334155; }
.suggest-tags--outside :deep(.ant-tag):hover { color: #1f2937; border-color: rgba(2,6,23,0.16); }

/* 固定首页与输入框宽度 */
#homePage { width: 1200px; min-height: 100vh; }
.prompt-box { width: 960px; max-width: none; }

/* 窄屏回退为满宽，保证可用性 */
@media (max-width: 1248px) {
  #homePage { width: 100%; max-width: 100%; }
  .prompt-box { width: 100%; max-width: 840px; }
}
</style>
