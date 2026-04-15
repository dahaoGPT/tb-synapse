<template>
  <el-aside :width="isCollapse ? '64px' : '210px'" class="sidebar-container">
    <div class="logo-container">
      <img src="" alt="logo" class="logo-img" />
      <span v-show="!isCollapse" class="logo-title">后台管理系统</span>
    </div>
    <el-scrollbar>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        :unique-opened="true"
        router
      >
        <!-- 静态路由菜单 -->
        <template v-for="route in menuList" :key="route.path">
          <!-- 没有子路由或只有一个子路由 -->
          <template v-if="!route.children || route.children.length === 0">
            <el-menu-item :index="resolvePath(route.path)">
              <el-icon v-if="route.meta?.icon">
                <component :is="route.meta.icon" />
              </el-icon>
              <template #title>{{ route.meta?.title || route.name }}</template>
            </el-menu-item>
          </template>

          <!-- 只有一个子路由，直接显示 -->
          <template v-else-if="route.children.length === 1 && !route.children[0].children">
            <el-menu-item :index="resolvePath(route.path, route.children[0].path)">
              <el-icon v-if="route.children[0].meta?.icon || route.meta?.icon">
                <component :is="route.children[0].meta?.icon || route.meta?.icon" />
              </el-icon>
              <template #title>{{ route.children[0].meta?.title || route.children[0].name }}</template>
            </el-menu-item>
          </template>

          <!-- 多个子路由，显示子菜单 -->
          <el-sub-menu v-else :index="resolvePath(route.path)">
            <template #title>
              <el-icon v-if="route.meta?.icon">
                <component :is="route.meta.icon" />
              </el-icon>
              <span>{{ route.meta?.title || route.name }}</span>
            </template>
            <el-menu-item
              v-for="child in route.children"
              :key="child.path"
              :index="resolvePath(route.path, child.path)"
            >
              <el-icon v-if="child.meta?.icon">
                <component :is="child.meta.icon" />
              </el-icon>
              <template #title>{{ child.meta?.title || child.name }}</template>
            </el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>
    </el-scrollbar>
  </el-aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { constantRoutes } from '@/router/index'
import usePermissionStore from '@/store/modules/permission'

defineProps({
  isCollapse: {
    type: Boolean,
    default: false
  }
})

const route = useRoute()
const permissionStore = usePermissionStore()

// 合并静态路由和动态路由的菜单列表
const menuList = computed(() => {
  const dynamicMenuList = permissionStore.menuList || []
  // 将动态路由转换为菜单格式
  const formattedDynamicRoutes = formatRoutes(dynamicMenuList)
  // 过滤掉隐藏的路由
  const staticMenuList = constantRoutes.filter(item => !item.meta?.hidden)
  return [...staticMenuList, ...formattedDynamicRoutes]
})

// 格式化动态路由为菜单格式
function formatRoutes(routes) {
  return routes.filter(item => !item.meta?.hidden).map(item => {
    const route = {
      path: item.path,
      name: item.name,
      meta: item.meta || {},
      children: []
    }
    if (item.children && item.children.length) {
      route.children = formatRoutes(item.children)
    }
    return route
  })
}

// 解析完整路径
function resolvePath(parentPath, childPath) {
  if (childPath) {
    if (childPath.startsWith('/')) {
      return childPath
    }
    return `${parentPath}/${childPath}`.replace(/\/+/g, '/')
  }
  return parentPath
}

// 当前激活的菜单
const activeMenu = computed(() => {
  return route.path
})
</script>

<style lang="scss" scoped>
.sidebar-container {
  background-color: #304156;
  height: 100%;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1001;
  overflow: hidden;
  transition: width 0.28s;
}

.logo-container {
  height: 50px;
  line-height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #263445;
  padding: 0 15px;
  overflow: hidden;

  .logo-img {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background-color: #409eff;
  }

  .logo-title {
    color: #fff;
    font-size: 14px;
    font-weight: 600;
    margin-left: 10px;
    white-space: nowrap;
  }
}

:deep(.el-menu) {
  border-right: none;
}

:deep(.el-scrollbar) {
  height: calc(100% - 50px);
}
</style>
