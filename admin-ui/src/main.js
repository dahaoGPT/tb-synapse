import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import { createPinia } from 'pinia'
import router from './router'
import { useUserStore } from './store/modules/user'
import './permission'
import './styles/index.scss'

const app = createApp(App)
const pinia = createPinia()

// 先安装 Pinia，确保指令中可以访问 store
app.use(pinia)

// 注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 注册自定义权限指令 v-hasPermi
// 用法: v-hasPermi="['system:user:add']"
app.directive('hasPermi', {
  mounted(el, binding) {
    const { value } = binding
    const allPermission = '*:*:*'
    const userStore = useUserStore()
    const permissions = userStore.permissions || []

    if (value && Array.isArray(value) && value.length > 0) {
      const hasPermission = permissions.some(permission => {
        return allPermission === permission || value.includes(permission)
      })
      if (!hasPermission) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else {
      throw new Error('需要指定权限标识，如 v-hasPermi="[\'system:user:add\']"')
    }
  }
})

app.use(router)
app.use(ElementPlus, { locale: zhCn })

app.mount('#app')
