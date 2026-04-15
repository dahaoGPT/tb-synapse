<template>
  <el-header class="navbar">
    <div class="hamburger-container" @click="$emit('toggle-sidebar')">
      <el-icon :size="20">
        <component :is="isCollapse ? 'Expand' : 'Fold'" />
      </el-icon>
    </div>

    <div class="breadcrumb-container">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
          <span v-if="item.redirect === 'noRedirect' || item === breadcrumbs[breadcrumbs.length - 1]">
            {{ item.meta?.title }}
          </span>
          <router-link v-else :to="item.path">
            {{ item.meta?.title }}
          </router-link>
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="right-menu">
      <el-dropdown trigger="click" @command="handleCommand">
        <div class="avatar-container">
          <el-avatar :size="30" icon="UserFilled" />
          <span class="username">{{ userInfo.userName || '管理员' }}</span>
          <el-icon><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">
              <el-icon><User /></el-icon>
              个人中心
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </el-header>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Expand, Fold, ArrowDown, User, SwitchButton } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import useUserStore from '@/store/modules/user'

defineProps({
  isCollapse: {
    type: Boolean,
    default: false
  }
})

defineEmits(['toggle-sidebar'])

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo)

// 面包屑导航
const breadcrumbs = computed(() => {
  const matched = route.matched.filter(item => item.meta && item.meta.title)
  const first = matched[0]
  if (first && first.path !== '/dashboard') {
    matched.unshift({ path: '/dashboard', meta: { title: '首页' } })
  }
  return matched
})

// 下拉菜单命令处理
const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
  }).catch(() => {})
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 50px;
  display: flex;
  align-items: center;
  padding: 0 15px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  background: #fff;
  position: fixed;
  top: 0;
  right: 0;
  left: 210px;
  z-index: 1000;
  transition: left 0.28s;
}

.hideSidebar .navbar {
  left: 64px;
}

.hamburger-container {
  cursor: pointer;
  display: flex;
  align-items: center;
  padding: 0 10px;

  &:hover {
    background-color: rgba(0, 0, 0, 0.025);
  }
}

.breadcrumb-container {
  flex: 1;
  margin-left: 10px;
}

.right-menu {
  display: flex;
  align-items: center;

  .avatar-container {
    display: flex;
    align-items: center;
    cursor: pointer;
    padding: 0 10px;

    .username {
      margin: 0 5px;
      font-size: 14px;
      color: #333;
    }

    &:hover {
      background-color: rgba(0, 0, 0, 0.025);
    }
  }
}
</style>
