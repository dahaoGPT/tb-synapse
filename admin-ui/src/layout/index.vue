<template>
  <el-container class="app-wrapper">
    <!-- 侧边栏 -->
    <Sidebar :is-collapse="isCollapse" />
    <!-- 主内容区 -->
    <el-container class="main-container" :class="{ 'has-tags-view': showTagsView }">
      <!-- 顶部导航栏 -->
      <Navbar :is-collapse="isCollapse" @toggle-sidebar="toggleSidebar" />
      <!-- 标签页导航 -->
      <TagsView v-if="showTagsView" />
      <!-- 主内容区域 -->
      <AppMain />
    </el-container>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import Sidebar from './components/Sidebar.vue'
import Navbar from './components/Navbar.vue'
import TagsView from './components/TagsView.vue'
import AppMain from './components/AppMain.vue'

const isCollapse = ref(false)
const showTagsView = ref(true)

const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}
</script>

<style lang="scss" scoped>
.app-wrapper {
  position: relative;
  height: 100%;
  width: 100%;
}

.main-container {
  min-height: 100%;
  transition: margin-left 0.28s;
  margin-left: 210px;
  position: relative;
  display: flex;
  flex-direction: column;
}

.main-container.has-tags-view {
  padding-top: 34px;
}

.hideSidebar .main-container {
  margin-left: 64px;
}
</style>
