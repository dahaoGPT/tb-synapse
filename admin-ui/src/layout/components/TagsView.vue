<template>
  <div class="tags-view-container">
    <el-scrollbar>
      <div class="tags-view-wrapper">
        <router-link
          v-for="tag in visitedViews"
          :key="tag.path"
          :to="tag.path"
          class="tags-view-item"
          :class="{ active: isActive(tag) }"
          @contextmenu.prevent="openContextMenu($event, tag)"
        >
          <span>{{ tag.meta?.title }}</span>
          <el-icon
            v-if="!tag.meta?.affix"
            class="tags-view-item-close"
            @click.prevent.stop="closeTag(tag)"
          >
            <Close />
          </el-icon>
        </router-link>
      </div>
    </el-scrollbar>
  </div>
</template>

<script setup>
import { ref, watch, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Close } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 已访问的视图列表
const visitedViews = ref([])

// 固定的标签页
const affixTags = ref([
  { path: '/dashboard', name: 'Dashboard', meta: { title: '首页', affix: true } }
])

// 判断是否为当前激活的标签
const isActive = (tag) => {
  return tag.path === route.path
}

// 添加标签
const addTag = () => {
  if (route.meta?.title) {
    const exists = visitedViews.value.some(tag => tag.path === route.path)
    if (!exists) {
      visitedViews.value.push({
        path: route.path,
        name: route.name,
        meta: { ...route.meta }
      })
    }
  }
}

// 关闭标签
const closeTag = (tag) => {
  const index = visitedViews.value.findIndex(item => item.path === tag.path)
  if (index > -1) {
    visitedViews.value.splice(index, 1)
    // 如果关闭的是当前激活的标签，则跳转到上一个标签
    if (isActive(tag)) {
      const lastView = visitedViews.value[visitedViews.value.length - 1]
      if (lastView) {
        router.push(lastView.path)
      } else {
        router.push('/')
      }
    }
  }
}

// 右键菜单（暂不实现复杂功能）
const openContextMenu = (e, tag) => {
  // 可以扩展右键菜单功能
}

// 初始化固定标签
onMounted(() => {
  affixTags.value.forEach(tag => {
    const exists = visitedViews.value.some(item => item.path === tag.path)
    if (!exists) {
      visitedViews.value.push(tag)
    }
  })
  addTag()
})

// 监听路由变化
watch(
  () => route.path,
  () => {
    addTag()
  }
)
</script>

<style lang="scss" scoped>
.tags-view-container {
  height: 34px;
  width: 100%;
  background: #fff;
  border-bottom: 1px solid #d8dce5;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.04), 0 1px 2px -1px rgba(0, 0, 0, 0.04);
  position: fixed;
  top: 50px;
  right: 0;
  left: 210px;
  z-index: 999;
  transition: left 0.28s;
}

.hideSidebar .tags-view-container {
  left: 64px;
}

.tags-view-wrapper {
  display: flex;
  align-items: center;
  padding: 0 10px;
  height: 34px;
  white-space: nowrap;
  overflow: hidden;
}

.tags-view-item {
  display: inline-flex;
  align-items: center;
  height: 26px;
  padding: 0 8px;
  margin-right: 5px;
  font-size: 12px;
  color: #495060;
  background: #fff;
  border: 1px solid #d8dce5;
  border-radius: 3px;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    color: #409eff;
  }

  &.active {
    background-color: #409eff;
    color: #fff;
    border-color: #409eff;

    .tags-view-item-close {
      color: #fff;
    }
  }

  .tags-view-item-close {
    margin-left: 4px;
    font-size: 12px;
    border-radius: 50%;
    width: 16px;
    height: 16px;
    line-height: 16px;
    text-align: center;
    transition: all 0.2s;

    &:hover {
      background-color: rgba(0, 0, 0, 0.15);
      color: #fff;
    }
  }
}
</style>
