<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" class="search-form">
      <el-form-item label="菜单名称" prop="menuName">
        <el-input
          v-model="queryParams.menuName"
          placeholder="请输入菜单名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="菜单状态" clearable style="width: 120px">
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb10">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" v-hasPermi="['system:menu:add']" @click="handleAdd()">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="Sort" @click="toggleExpandAll">展开/折叠</el-button>
      </el-col>
    </el-row>

    <!-- 树形表格 -->
    <el-table
      v-loading="loading"
      :data="menuList"
      row-key="menuId"
      :default-expand-all="isExpandAll"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      border
      stripe
    >
      <el-table-column label="菜单名称" prop="menuName" min-width="180" show-overflow-tooltip>
        <template #default="{ row }">
          <el-icon v-if="row.icon" style="margin-right: 5px;">
            <component :is="row.icon" />
          </el-icon>
          <span>{{ row.menuName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="图标" align="center" width="80">
        <template #default="{ row }">
          <el-icon v-if="row.icon">
            <component :is="row.icon" />
          </el-icon>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="排序" prop="sort" align="center" width="80" />
      <el-table-column label="权限标识" prop="perms" align="center" min-width="150" show-overflow-tooltip>
        <template #default="{ row }">
          <span v-if="row.perms">{{ row.perms }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="类型" align="center" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.menuType === 'D'" type="">目录</el-tag>
          <el-tag v-else-if="row.menuType === 'M'" type="success">菜单</el-tag>
          <el-tag v-else-if="row.menuType === 'B'" type="warning">按钮</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === '0' ? 'success' : 'danger'">
            {{ row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" align="center" min-width="160" show-overflow-tooltip />
      <el-table-column label="操作" align="center" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" icon="Edit" v-hasPermi="['system:menu:edit']" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="primary" icon="Plus" v-hasPermi="['system:menu:add']" @click="handleAdd(row)">新增</el-button>
          <el-button link type="primary" icon="Delete" v-hasPermi="['system:menu:remove']" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="680px" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="上级菜单" prop="parentId">
              <TreeSelect
                v-model="form.parentId"
                :options="menuOptions"
                placeholder="请选择上级菜单"
                :normalizer="menuNormalizer"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="菜单类型" prop="menuType">
              <el-radio-group v-model="form.menuType">
                <el-radio value="D">目录</el-radio>
                <el-radio value="M">菜单</el-radio>
                <el-radio value="B">按钮</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="菜单图标" prop="icon" v-if="form.menuType !== 'B'">
              <el-input v-model="form.icon" placeholder="请输入图标名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="菜单名称" prop="menuName">
              <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="form.sort" :min="0" :max="999" controls-position="right" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20" v-if="form.menuType !== 'B'">
          <el-col :span="12">
            <el-form-item label="路由地址" prop="path">
              <el-input v-model="form.path" placeholder="请输入路由地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType === 'M'">
            <el-form-item label="组件路径" prop="component">
              <el-input v-model="form.component" placeholder="请输入组件路径" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20" v-if="form.menuType === 'B'">
          <el-col :span="12">
            <el-form-item label="权限标识" prop="perms">
              <el-input v-model="form.perms" placeholder="请输入权限标识" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20" v-if="form.menuType !== 'B'">
          <el-col :span="12">
            <el-form-item label="是否外链" prop="isFrame">
              <el-radio-group v-model="form.isFrame">
                <el-radio value="0">是</el-radio>
                <el-radio value="1">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否缓存" prop="isCache">
              <el-radio-group v-model="form.isCache">
                <el-radio value="0">缓存</el-radio>
                <el-radio value="1">不缓存</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20" v-if="form.menuType !== 'B'">
          <el-col :span="12">
            <el-form-item label="显示状态" prop="visible">
              <el-radio-group v-model="form.visible">
                <el-radio value="0">显示</el-radio>
                <el-radio value="1">隐藏</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio value="0">正常</el-radio>
                <el-radio value="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listMenu, getMenu, addMenu, updateMenu, delMenu, getMenuTreeSelect } from '@/api/menu'
import TreeSelect from '@/components/TreeSelect.vue'

// 查询参数
const queryParams = reactive({
  menuName: '',
  status: ''
})

const loading = ref(false)
const menuList = ref([])
const isExpandAll = ref(true)
const queryFormRef = ref(null)

// 菜单树选项
const menuOptions = ref([])

// 菜单树格式化
const menuNormalizer = (node) => {
  if (node.children && node.children.length === 0) {
    delete node.children
  }
  return {
    id: node.id || node.menuId,
    label: node.label || node.menuName,
    children: node.children
  }
}

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const form = ref(getDefaultForm())

function getDefaultForm() {
  return {
    menuId: undefined,
    parentId: 0,
    menuName: '',
    menuType: 'M',
    icon: '',
    sort: 0,
    path: '',
    component: '',
    perms: '',
    isFrame: '1',
    isCache: '0',
    visible: '0',
    status: '0'
  }
}

const rules = {
  menuName: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' }
  ],
  sort: [
    { required: true, message: '请输入排序', trigger: 'blur' }
  ],
  path: [
    { required: true, message: '请输入路由地址', trigger: 'blur' }
  ],
  perms: [
    { required: true, message: '请输入权限标识', trigger: 'blur' }
  ]
}

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const res = await listMenu(queryParams)
    const data = res.data || res
    menuList.value = buildTree(data || [])
  } catch (error) {
    menuList.value = []
  } finally {
    loading.value = false
  }
}

// 构建树形结构
function buildTree(data) {
  const map = {}
  const tree = []
  data.forEach(item => {
    map[item.menuId] = { ...item, children: [] }
  })
  data.forEach(item => {
    if (item.parentId === 0) {
      tree.push(map[item.menuId])
    } else if (map[item.parentId]) {
      map[item.parentId].children.push(map[item.menuId])
    }
  })
  return tree
}

// 搜索
const handleQuery = () => {
  getList()
}

// 重置搜索
const resetQuery = () => {
  if (queryFormRef.value) {
    queryFormRef.value.resetFields()
  }
  handleQuery()
}

// 展开/折叠
const toggleExpandAll = () => {
  isExpandAll.value = !isExpandAll.value
  // 通过重新赋值触发表格更新
  const data = menuList.value
  menuList.value = []
  nextTick(() => {
    menuList.value = data
  })
}

// 新增
const handleAdd = (row) => {
  resetForm()
  if (row && row.menuId) {
    form.value.parentId = row.menuId
  }
  dialogTitle.value = '新增菜单'
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row) => {
  resetForm()
  try {
    const res = await getMenu(row.menuId)
    const data = res.data || res
    form.value = {
      menuId: data.menuId,
      parentId: data.parentId,
      menuName: data.menuName,
      menuType: data.menuType,
      icon: data.icon,
      sort: data.sort,
      path: data.path,
      component: data.component,
      perms: data.perms,
      isFrame: data.isFrame || '1',
      isCache: data.isCache || '0',
      visible: data.visible || '0',
      status: data.status || '0'
    }
    dialogTitle.value = '编辑菜单'
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取菜单信息失败')
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(`确认要删除菜单"${row.menuName}"吗？`, '系统提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await delMenu(row.menuId)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      // 错误已在拦截器中处理
    }
  }).catch(() => {})
}

// 提交表单
const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.value.menuId) {
          await updateMenu(form.value)
          ElMessage.success('修改成功')
        } else {
          await addMenu(form.value)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        getList()
      } catch (error) {
        // 错误已在拦截器中处理
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  form.value = getDefaultForm()
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 获取菜单树
const getMenuTreeData = async () => {
  try {
    const res = await getMenuTreeSelect()
    const data = res.data || res
    menuOptions.value = data || []
  } catch (error) {
    menuOptions.value = []
  }
}

import { nextTick } from 'vue'

onMounted(() => {
  getList()
  getMenuTreeData()
})
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;
}

.search-form {
  margin-bottom: 10px;
}

.mb10 {
  margin-bottom: 10px;
}
</style>
