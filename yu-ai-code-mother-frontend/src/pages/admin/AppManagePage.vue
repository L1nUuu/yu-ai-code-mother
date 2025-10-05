<template>
  <div id="appManagePage">
    <!-- 搜索表单（放置在表格上方，位于页面顶部） -->
    <a-form class="search-form" layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="ID">
        <a-input v-model:value="searchParams.id" placeholder="应用ID" style="width: 180px" />
      </a-form-item>
      <a-form-item label="名称">
        <a-input v-model:value="searchParams.appName" placeholder="输入应用名称" style="width: 240px" />
      </a-form-item>
      <a-form-item label="创建者ID">
        <a-input-number v-model:value="searchParams.userId" :min="1" placeholder="用户ID" style="width: 160px" />
      </a-form-item>
      <a-form-item label="生成类型">
        <a-select v-model:value="searchParams.codeGenType" allow-clear placeholder="选择生成类型" style="width: 200px">
          <a-select-option v-for="opt in CODE_GEN_TYPE_OPTIONS" :key="opt.value" :value="opt.value">{{ opt.label }}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="优先级">
        <a-input-number v-model:value="searchParams.priority" :min="0" placeholder="优先级" style="width: 120px" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" class="btn-search">搜索</a-button>
      </a-form-item>
    </a-form>

    <!-- 表格 -->
    <a-table
      :columns="columns"
      :data-source="data"
      :pagination="pagination"
      row-key="id"
      @change="doTableChange"
    >
      <template #bodyCell="{ column, record }">
        <!-- 封面小图 100x100 -->
        <template v-if="column.dataIndex === 'cover'">
          <div class="cover-thumb">
            <a-image :src="record.cover" style="width: 100px; height: 100px; object-fit: cover" />
          </div>
        </template>
        <!-- 创建者信息 -->
        <template v-else-if="column.dataIndex === 'user'">
          <UserInfo :user="record.user" />
        </template>
        <!-- 生成类型显示（格式化） -->
        <template v-else-if="column.dataIndex === 'codeGenType'">
          {{ formatCodeGenType(record.codeGenType) }}
        </template>
        <!-- 优先级：99 显示橙色精选方框，其余显示数字 -->
        <template v-else-if="column.dataIndex === 'priority'">
          <a-tag v-if="record.priority === 99" color="orange">精选</a-tag>
          <span v-else>{{ record.priority ?? 0 }}</span>
        </template>
        <!-- 创建时间格式化显示 -->
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <!-- 操作按钮分组显示：编辑（蓝）、删除（红）、精选（橙/蓝） -->
        <template v-else-if="column.key === 'action'">
          <div class="row-actions">
            <a-space class="action-group">
              <a-button type="primary" class="btn-edit" @click="toEdit(record.id)">编辑</a-button>
            </a-space>
            <a-space class="action-group">
              <a-popconfirm title="确定删除该应用吗？" @confirm="doDelete(record.id)" ok-text="确定" cancel-text="取消">
                <a-button class="btn-delete">删除</a-button>
              </a-popconfirm>
            </a-space>
            <a-space class="action-group">
              <a-button v-if="record.priority === 99" class="btn-feature-cancel" @click="cancelFeatured(record.id)">取消精选</a-button>
              <a-button v-else class="btn-feature" @click="setFeatured(record.id)">精选</a-button>
            </a-space>
          </div>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import UserInfo from '@/components/UserInfo.vue'
import { deleteAppByAdmin, listAppVoByPageByAdmin, updateAppByAdmin } from '@/api/appController'
import { CODE_GEN_TYPE_OPTIONS, formatCodeGenType } from '@/utils/codeGenTypes'

const router = useRouter()

const columns = [
  { title: 'ID', dataIndex: 'id', width: 110, align: 'center' },
  { title: '应用名称', dataIndex: 'appName', width: 220, align: 'left', ellipsis: true },
  { title: '封面', dataIndex: 'cover', width: 120, align: 'center' },
  { title: '生成类型', dataIndex: 'codeGenType', width: 160, align: 'left' },
  { title: '优先级', dataIndex: 'priority', width: 100, align: 'center' },
  { title: '创建者', dataIndex: 'user', width: 160, align: 'left' },
  { title: '创建时间', dataIndex: 'createTime', width: 180, align: 'center' },
  { title: '操作', key: 'action', width: 260, align: 'center' },
]

// 展示的数据
const data = ref<API.AppVO[]>([])
const total = ref(0)

// 搜索条件（管理员不限每页数量，但支持分页）
const searchParams = reactive<API.AppQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

// 获取数据
const fetchData = async () => {
  const res = await listAppVoByPageByAdmin({ ...searchParams })
  if (res.data.data) {
    data.value = res.data.data.records ?? []
    total.value = res.data.data.totalRow ?? 0
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
}

// 分页参数
const pagination = computed(() => ({
  current: searchParams.pageNum ?? 1,
  pageSize: searchParams.pageSize ?? 10,
  total: total.value,
  showSizeChanger: true,
  showTotal: (t: number) => `共 ${t} 条`,
}))

// 定义表格分页变化类型，避免使用 any
type TablePageChange = { current: number; pageSize: number }

// 表格变化处理
const doTableChange = (page: TablePageChange) => {
  searchParams.pageNum = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// 搜索数据
const doSearch = () => {
  searchParams.pageNum = 1
  fetchData()
}

// 删除数据
const doDelete = async (id: string) => {
  if (!id) return
  const res = await deleteAppByAdmin({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
    fetchData()
  } else {
    message.error('删除失败：' + res.data.message)
  }
}

// 设置精选（优先级 99）
const setFeatured = async (id: string) => {
  const res = await updateAppByAdmin({ id, priority: 99 })
  if (res.data.code === 0) {
    message.success('已设置为精选')
    fetchData()
  } else {
    message.error('操作失败：' + res.data.message)
  }
}

// 取消精选（恢复优先级为 0）
const cancelFeatured = async (id: string) => {
  const res = await updateAppByAdmin({ id, priority: 0 })
  if (res.data.code === 0) {
    message.success('已取消精选')
    fetchData()
  } else {
    message.error('操作失败：' + res.data.message)
  }
}
// 跳转编辑
const toEdit = (id: string) => {
  router.push(`/app/edit/${id}`)
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
#appManagePage {
  max-width: 1400px;
  margin: 0 auto;
  padding: 12px 24px;
}
.search-form {
  margin-bottom: 20px;
}
.cover-thumb {
  width: 100px;
  height: 100px;
  border-radius: 6px;
  overflow: hidden;
  margin: 4px auto; /* 与其它信息保持适当间距，居中显示 */
}
/* 操作按钮分组布局 */
.row-actions {
  display: flex;
  gap: 12px; /* 增加按钮之间的空隙，避免拥挤 */
  justify-content: center;
}
.action-group {
  gap: 8px;
}
/* 按钮配色（文字颜色） */
.btn-edit {
  /* 使用主按钮，符合蓝色样式 */
}
.btn-delete {
  color: #ff4d4f;
  border-color: #ff4d4f;
  background-color: transparent;
}
.btn-feature {
  color: #1677ff; /* 蓝色文字（精选） */
  border-color: #1677ff;
  background-color: transparent;
}
.btn-feature-cancel {
  color: #fa8c16; /* 橙色文字（取消精选） */
  border-color: #fa8c16;
  background-color: transparent;
}
</style>