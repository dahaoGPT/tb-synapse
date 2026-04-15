import { defineStore } from 'pinia'
import { getRouters } from '@/api/auth'
import Layout from '@/layout/index.vue'
import router from '@/router'

// 组件映射表：将后端返回的component路径映射到实际组件
const viewModules = import.meta.glob('@/views/**/*.vue')

/**
 * 将后端返回的路由数据转换为Vue Router可用的路由配置
 * @param {Array} routes - 后端返回的路由数据
 * @returns {Array} Vue Router路由配置
 */
function generateRoutesFromData(routes) {
  const accessedRoutes = []

  routes.forEach((route) => {
    const tmp = { ...route }
    if (tmp.component) {
      if (tmp.component === 'Layout') {
        tmp.component = Layout
      } else {
        // 将后端返回的组件路径映射到实际组件
        const componentPath = resolveComponent(tmp.component)
        tmp.component = viewModules[componentPath] || (() => import('@/views/dashboard/index.vue'))
      }
    }

    // 递归处理子路由
    if (tmp.children && tmp.children.length) {
      tmp.children = generateRoutesFromData(tmp.children)
    }

    accessedRoutes.push(tmp)
  })

  return accessedRoutes
}

/**
 * 解析组件路径
 * @param {string} path - 后端返回的组件路径，如 'system/user/index'
 * @returns {string} 实际组件路径
 */
function resolveComponent(path) {
  // 尝试多种路径格式
  const candidates = [
    `/src/views/${path}.vue`,
    `/src/views/${path}/index.vue`
  ]
  for (const candidate of candidates) {
    if (viewModules[candidate]) {
      return candidate
    }
  }
  // 默认返回第一个候选
  return candidates[0]
}

const usePermissionStore = defineStore('permission', {
  state: () => ({
    routes: [],       // 完整路由（含静态路由）
    menuList: [],     // 菜单列表（用于侧边栏渲染）
    dynamicRoutes: [] // 动态路由
  }),

  actions: {
    // 生成动态路由
    async generateRoutes() {
      try {
        const res = await getRouters()
        const data = res.data || res
        const asyncRoutes = generateRoutesFromData(data || [])
        this.dynamicRoutes = asyncRoutes
        this.menuList = data || []

        // 将动态路由添加到路由器
        asyncRoutes.forEach((route) => {
          router.addRoute(route)
        })

        // 添加兜底路由 - 匹配所有未定义的路由到404
        router.addRoute({
          path: '/:pathMatch(.*)*',
          redirect: '/404',
          meta: { hidden: true }
        })

        // 合并静态路由和动态路由
        this.routes = router.getRoutes()
        return asyncRoutes
      } catch (error) {
        return Promise.reject(error)
      }
    }
  }
})

export default usePermissionStore
