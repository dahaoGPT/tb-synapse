<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" class="search-form">
      <el-form-item label="部门名称" prop="deptName">
        <el-input
          v-model="queryParams.deptName"
          placeholder="请输入部门名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="部门状态" clearable style="width: 120px">
          <el-option label="正常" :value="1" />
          <el-option label="停用" :value="0" />
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
        <el-button type="primary" plain icon="Plus" v-hasPermi="['system:dept:add']" @click="handleAdd()">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="Sort" @click="toggleExpandAll">展开/折叠</el-button>
      </el-col>
    </el-row>

    <!-- 树形表格 -->
    <el-table
      v-loading="loading"
      :data="deptList"
      row-key="id"
      :default-expand-all="isExpandAll"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      border
      stripe
    >
      <el-table-column label="部门名称" prop="deptName" min-width="200" show-overflow-tooltip />
      <el-table-column label="排序" prop="sortOrder" align="center" width="80" />
      <el-table-column label="状态" align="center" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="负责人" prop="leader" align="center" min-width="120" show-overflow-tooltip>
        <template #default="{ row }">
          <span>{{ row.leader || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="联系电话" prop="phone" align="center" min-width="130" show-overflow-tooltip>
        <template #default="{ row }">
          <span>{{ row.phone || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" align="center" min-width="160" show-overflow-tooltip />
      <el-table-column label="操作" align="center" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" icon="Edit" v-hasPermi="['system:dept:edit']" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="primary" icon="Plus" v-hasPermi="['system:dept:add']" @click="handleAdd(row)">新增</el-button>
          <el-button link type="primary" icon="Delete" v-hasPermi="['system:dept:remove']" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="24" v-if="form.id !== undefined">
            <el-form-item label="上级部门" prop="parentId">
              <TreeSelect
                v-model="form.parentId"
                :options="deptOptions"
                placeholder="请选择上级部门"
                :normalizer="deptNormalizer"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="部门名称" prop="deptName">
              <el-input v-model="form.deptName" placeholder="请输入部门名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示排序" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" :min="0" :max="999" controls-position="right" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="负责人" prop="leader">
              <el-input v-model="form.leader" placeholder="请输入负责人" maxlength="20" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" maxlength="11" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio :value="1">正常</el-radio>
                <el-radio :value="0">停用</el-radio>
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
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listDept, getDept, addDept, updateDept, delDept, getDeptTreeSelect } from '@/api/dept'
import TreeSelect from '@/components/TreeSelect.vue'

// 查询参数
const queryParams = reactive({
  deptName: '',
  status: ''
})

const loading = ref(false)
const deptList = ref([])
const isExpandAll = ref(true)
const queryFormRef = ref(null)

// 部门树选项
const deptOptions = ref([])

// 部门树格式化
const deptNormalizer = (node) => {
  if (node.children && node.children.length === 0) {
    delete node.children
  }
  return {
    id: node.id || node.deptId,
    label: node.label || node.deptName,
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
    id: undefined,
    parentId: 0,
    deptName: '',
    sortOrder: 0,
    leader: '',
    phone: '',
    email: '',
    status: 1
  }
}

const rules = {
  deptName: [
    { required: true, message: '请输入部门名称', trigger: 'blur' }
  ],
  sortOrder: [
    { required: true, message: '请输入排序', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const res = await listDept(queryParams)
    const data = res.data || res
    deptList.value = buildTree(data || [])
  } catch (error) {
    deptList.value = []
  } finally {
    loading.value = false
  }
}

// 构建树形结构
function buildTree(data) {
  // If backend already returns tree structure, use it directly
  if (data.length > 0 && data[0].children) {
    return data
  }
  const map = {}
  const tree = []
  data.forEach(item => {
    map[item.id] = { ...item, children: [] }
  })
  data.forEach(item => {
    if (item.parentId === 0) {
      tree.push(map[item.id])
    } else if (map[item.parentId]) {
      map[item.parentId].children.push(map[item.id])
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
  const data = deptList.value
  deptList.value = []
  nextTick(() => {
    deptList.value = data
  })
}

// 新增
const handleAdd = (row) => {
  resetForm()
  if (row && row.id) {
    form.value.parentId = row.id
  }
  dialogTitle.value = '新增部门'
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row) => {
  resetForm()
  try {
    const res = await getDept(row.id)
    const data = res.data || res
    form.value = {
      id: data.id,
      parentId: data.parentId,
      deptName: data.deptName,
      sortOrder: data.sortOrder,
      leader: data.leader,
      phone: data.phone,
      email: data.email,
      status: data.status !== undefined ? data.status : 1
    }
    dialogTitle.value = '编辑部门'
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取部门信息失败')
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(`确认要删除部门"${row.deptName}"吗？`, '系统提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await delDept(row.id)
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
        if (form.value.id) {
          await updateDept(form.value)
          ElMessage.success('修改成功')
        } else {
          await addDept(form.value)
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

// 获取部门树
const getDeptTreeData = async () => {
  try {
    const res = await getDeptTreeSelect()
    const data = res.data || res
    deptOptions.value = data || []
  } catch (error) {
    deptOptions.value = []
  }
}

onMounted(() => {
  getList()
  getDeptTreeData()
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
