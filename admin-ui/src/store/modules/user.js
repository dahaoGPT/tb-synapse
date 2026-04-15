import { defineStore } from 'pinia'
import { login as loginApi, getInfo as getInfoApi, logout as logoutApi } from '@/api/auth'
import { getToken, setToken, removeToken } from '@/utils/auth'
import router from '@/router'

const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    userInfo: {},
    roles: [],
    permissions: []
  }),

  actions: {
    // 登录
    async login(loginForm) {
      try {
        const res = await loginApi(loginForm)
        const token = res.data?.token || res.token
        this.token = token
        setToken(token)
        return res
      } catch (error) {
        return Promise.reject(error)
      }
    },

    // 获取用户信息
    async getInfo() {
      try {
        const res = await getInfoApi()
        const data = res.data || res
        this.userInfo = data.user || data
        this.roles = data.roles || []
        this.permissions = data.permissions || []
        return data
      } catch (error) {
        return Promise.reject(error)
      }
    },

    // 退出登录
    async logout() {
      try {
        await logoutApi()
      } catch (e) {
        // 忽略退出接口错误
      }
      this.token = ''
      this.userInfo = {}
      this.roles = []
      this.permissions = []
      removeToken()
      router.push('/login')
    }
  }
})

export default useUserStore
