<template>
  <el-tree-select
    v-model="selectedValue"
    :data="options"
    :props="treeProps"
    :render-after-expand="false"
    :filterable="filterable"
    :placeholder="placeholder"
    :clearable="clearable"
    :check-strictly="checkStrictly"
    :disabled="disabled"
    style="width: 100%"
    @node-click="handleNodeClick"
  />
</template>

<script setup>
import { computed, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: [Number, String, Array],
    default: undefined
  },
  options: {
    type: Array,
    default: () => []
  },
  placeholder: {
    type: String,
    default: '请选择'
  },
  filterable: {
    type: Boolean,
    default: true
  },
  clearable: {
    type: Boolean,
    default: true
  },
  checkStrictly: {
    type: Boolean,
    default: true
  },
  disabled: {
    type: Boolean,
    default: false
  },
  normalizer: {
    type: Function,
    default: null
  },
  valueKey: {
    type: String,
    default: 'id'
  },
  labelKey: {
    type: String,
    default: 'label'
  },
  childrenKey: {
    type: String,
    default: 'children'
  }
})

const emit = defineEmits(['update:modelValue', 'node-click'])

// 树形属性配置
const treeProps = computed(() => {
  return {
    value: props.valueKey,
    label: props.labelKey,
    children: props.childrenKey,
    disabled: 'disabled'
  }
})

// 格式化选项数据
const normalizedOptions = computed(() => {
  if (props.normalizer) {
    return normalizeData(props.options, props.normalizer)
  }
  return props.options
})

// 递归格式化数据
function normalizeData(data, normalizer) {
  if (!Array.isArray(data)) return []
  return data.map(item => {
    const normalized = normalizer(item)
    if (normalized.children && normalized.children.length) {
      normalized.children = normalizeData(normalized.children, normalizer)
    }
    return normalized
  })
}

// 选中值
const selectedValue = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emit('update:modelValue', val)
  }
})

// 节点点击事件
const handleNodeClick = (data) => {
  emit('node-click', data)
}
</script>
