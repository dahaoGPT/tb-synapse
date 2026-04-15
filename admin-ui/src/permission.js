import router from './index'
import { getToken } from '@/utils/auth'
import useUserStore from '@/store/modules/user'
import usePermissionStore from '@/store/modules/permission'

// 白名单路由，不需要登录即可访问
const whiteList = ['/login', '/404']

// 是否已获取过用户信息标志
let hasRoles = false

router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  if (to.meta?.title) {
    document.title = to.meta.title + ' - 后台管理系统'
  }

  const token = getToken()

  if (token) {
    if (to.path === '/login') {
      // 已登录状态访问登录页，重定向到首页
      next({ path: '/' })
    } else {
      // 已登录，检查是否已获取用户信息
      const userStore = useUserStore()
      if (userStore.roles && userStore.roles.length > 0) {
        next()
      } else {
        try {
          // 获取用户信息
          await userStore.getInfo()
          // 生成动态路由
          const permissionStore = usePermissionStore()
          await permissionStore.generateRoutes()
          // 使用 replace 重新导航到目标路由，确保动态路由已加载
          next({ ...to, replace: true })
        } catch (error) {
          // 获取用户信息失败，清除token并跳转登录页
          const userStore = useUserStore()
          await userStore.logout()
          next(`/login?redirect=${to.path}`)
        }
      }
    }
  } else {
    // 未登录
    if (whiteList.includes(to.path)) {
      // 在白名单中，直接放行
      next()
    } else {
      // 不在白名单中，重定向到登录页
      next(`/login?redirect=${to.path}`)
    }
  }
})

// 路由后置守卫 - 关闭页面加载进度
router.afterEach(() => {
  // 可以在这里添加 NProgress.done() 等操作
})
