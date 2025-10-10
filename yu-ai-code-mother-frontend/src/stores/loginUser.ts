import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getLoginUser } from '@/api/userController.ts'

export const useLoginUserStore = defineStore('loginUser', () => {
  // 默认值
  const loginUser = ref<API.LoginUserVO>({
    userName: '未登录', //默认用户名称
  })

  // 增加请求去重，避免并发重复调用导致后端多次未登录异常
  let fetchingPromise: Promise<void> | null = null

  // 获取登录用户信息（去重并复用在途请求）
  async function fetchLoginUser() {
    if (fetchingPromise) {
      return fetchingPromise
    }
    fetchingPromise = (async () => {
      const res = await getLoginUser()
      if (res.data.code === 0 && res.data.data) {
        loginUser.value = res.data.data
      }
    })()
    try {
      await fetchingPromise
    } finally {
      fetchingPromise = null
    }
  }
  // 更新登录用户信息
  function setLoginUser(newLoginUser: API.LoginUserVO) {
    loginUser.value = newLoginUser
  }

  return { loginUser, setLoginUser, fetchLoginUser }
})
