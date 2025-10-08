<template>
  <div id="chatManagePage">
    <!-- 搜索表单 -->
    <div class="search-form">
      <a-form
        :model="searchParams"
        layout="inline"
        @finish="handleSearch"
        @reset="handleReset"
      >
        <a-form-item label="应用ID" name="appId">
          <a-input
            v-model:value="searchParams.appId"
            placeholder="请输入应用ID"
            style="width: 200px"
          />
        </a-form-item>
        <a-form-item label="消息类型" name="messageType">
          <a-select
            v-model:value="searchParams.messageType"
            placeholder="请选择消息类型"
            style="width: 150px"
            allow-clear
          >
            <a-select-option value="user">用户消息</a-select-option>
            <a-select-option value="ai">AI消息</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="用户ID" name="userId">
          <a-input
            v-model:value="searchParams.userId"
            placeholder="请输入用户ID"
            style="width: 200px"
          />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" :loading="loading">
            <template #icon>
              <SearchOutlined />
            </template>
            搜索
          </a-button>
          <a-button html-type="reset" style="margin-left: 8px">
            <template #icon>
              <ReloadOutlined />
            </template>
            重置
          </a-button>
        </a-form-item>
      </a-form>
    </div>

    <!-- 数据表格 -->
    <a-table
      :columns="columns"
      :data-source="dataList"
      :pagination="pagination"
      :loading="loading"
      row-key="id"
      @change="handleTableChange"
      :scroll="{ x: 1200 }"
    >
      <!-- 消息内容列 -->
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'message'">
          <div class="message-content">
            <a-tooltip :title="record.message" placement="topLeft">
              <div class="message-text">{{ record.message }}</div>
            </a-tooltip>
          </div>
        </template>
        
        <!-- 消息类型列 -->
        <template v-else-if="column.key === 'messageType'">
          <a-tag :color="record.messageType === 'user' ? 'blue' : 'green'">
            {{ record.messageType === 'user' ? '用户消息' : 'AI消息' }}
          </a-tag>
        </template>

        <!-- 创建时间列 -->
        <template v-else-if="column.key === 'createTime'">
          {{ formatTime(record.createTime) }}
        </template>

        <!-- 操作列 -->
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a-button
              type="link"
              size="small"
              @click="viewChatDetail(record)"
            >
              <template #icon>
                <EyeOutlined />
              </template>
              查看详情
            </a-button>
            <a-popconfirm
              title="确定要删除这条对话记录吗？"
              ok-text="确定"
              cancel-text="取消"
              @confirm="deleteChatHistory(record.id)"
            >
              <a-button
                type="link"
                size="small"
                danger
              >
                <template #icon>
                  <DeleteOutlined />
                </template>
                删除
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>

    <!-- 对话详情弹窗 -->
    <a-modal
      v-model:open="detailModalVisible"
      title="对话详情"
      :footer="null"
      width="800px"
    >
      <div v-if="selectedRecord" class="chat-detail">
        <a-descriptions :column="2" bordered>
          <a-descriptions-item label="对话ID">
            {{ selectedRecord.id }}
          </a-descriptions-item>
          <a-descriptions-item label="应用ID">
            {{ selectedRecord.appId }}
          </a-descriptions-item>
          <a-descriptions-item label="用户ID">
            {{ selectedRecord.userId }}
          </a-descriptions-item>
          <a-descriptions-item label="消息类型">
            <a-tag :color="selectedRecord.messageType === 'user' ? 'blue' : 'green'">
              {{ selectedRecord.messageType === 'user' ? '用户消息' : 'AI消息' }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="创建时间" :span="2">
            {{ formatTime(selectedRecord.createTime) }}
          </a-descriptions-item>
          <a-descriptions-item label="消息内容" :span="2">
            <div class="message-detail">
              {{ selectedRecord.message }}
            </div>
          </a-descriptions-item>
        </a-descriptions>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { message } from 'ant-design-vue'
import { listAllChatHistoryByPageAdmin } from '@/api/chatHistoryController'
import {
  SearchOutlined,
  ReloadOutlined,
  EyeOutlined,
  DeleteOutlined,
} from '@ant-design/icons-vue'

// 搜索参数
const searchParams = reactive({
  appId: '',
  messageType: undefined as string | undefined,
  userId: '',
})

// 数据列表
const dataList = ref<API.ChatHistory[]>([])
const loading = ref(false)

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number, range: [number, number]) =>
    `第 ${range[0]}-${range[1]} 条，共 ${total} 条`,
})

// 表格列配置
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 80,
    sorter: true,
  },
  {
    title: '应用ID',
    dataIndex: 'appId',
    key: 'appId',
    width: 100,
  },
  {
    title: '用户ID',
    dataIndex: 'userId',
    key: 'userId',
    width: 100,
  },
  {
    title: '消息类型',
    dataIndex: 'messageType',
    key: 'messageType',
    width: 120,
  },
  {
    title: '消息内容',
    dataIndex: 'message',
    key: 'message',
    width: 300,
    ellipsis: true,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180,
    sorter: true,
  },
  {
    title: '操作',
    key: 'action',
    width: 150,
    fixed: 'right',
  },
]

// 详情弹窗
const detailModalVisible = ref(false)
const selectedRecord = ref<API.ChatHistory | null>(null)

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const params: API.ChatHistoryQueryRequest = {
      current: pagination.current,
      pageSize: pagination.pageSize,
      sortField: 'createTime',
      sortOrder: 'desc',
    }

    // 添加搜索条件
    if (searchParams.appId) {
      params.appId = searchParams.appId
    }
    if (searchParams.messageType) {
      params.messageType = searchParams.messageType
    }
    if (searchParams.userId) {
      params.userId = searchParams.userId
    }

    const res = await listAllChatHistoryByPageAdmin(params)
    
    if (res.data.code === 0 && res.data.data) {
      dataList.value = res.data.data.records || []
      pagination.total = res.data.data.total || 0
    } else {
      message.error('获取数据失败：' + res.data.message)
    }
  } catch (error) {
    console.error('获取数据失败：', error)
    message.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

// 重置
const handleReset = () => {
  searchParams.appId = ''
  searchParams.messageType = undefined
  searchParams.userId = ''
  pagination.current = 1
  fetchData()
}

// 表格变化处理
const handleTableChange = (pag: any, filters: any, sorter: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  
  // 处理排序
  if (sorter && sorter.field) {
    // 这里可以根据需要处理排序逻辑
  }
  
  fetchData()
}

// 查看详情
const viewChatDetail = (record: API.ChatHistory) => {
  selectedRecord.value = record
  detailModalVisible.value = true
}

// 删除对话记录
const deleteChatHistory = async (id: number) => {
  try {
    // 注意：这里需要根据实际的删除API来实现
    // const res = await deleteChatHistoryApi({ id })
    // if (res.data.code === 0) {
    //   message.success('删除成功')
    //   fetchData()
    // } else {
    //   message.error('删除失败：' + res.data.message)
    // }
    
    // 临时提示，因为删除API可能还未实现
    message.info('删除功能待后端API实现')
  } catch (error) {
    console.error('删除失败：', error)
    message.error('删除失败')
  }
}

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

// 页面加载时获取数据
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
#chatManagePage {
  padding: 24px;
  background: #f5f5f5;
  min-height: 100vh;
}

.search-form {
  background: white;
  padding: 24px;
  border-radius: 8px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.message-content {
  max-width: 300px;
}

.message-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100%;
}

.message-detail {
  max-height: 200px;
  overflow-y: auto;
  padding: 8px;
  background: #f5f5f5;
  border-radius: 4px;
  white-space: pre-wrap;
  word-break: break-word;
}

.chat-detail {
  margin-top: 16px;
}

/* 表格样式 */
:deep(.ant-table) {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

:deep(.ant-table-thead > tr > th) {
  background: #fafafa;
  font-weight: 600;
}

/* 操作按钮样式 */
:deep(.ant-btn-link) {
  padding: 0;
  height: auto;
}

:deep(.ant-btn-link.ant-btn-dangerous) {
  color: #ff4d4f;
}

:deep(.ant-btn-link.ant-btn-dangerous:hover) {
  color: #ff7875;
}
</style>