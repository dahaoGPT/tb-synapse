<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" class="search-form">
      <el-form-item label="用户名" prop="username">
        <el-input
          v-model="queryParams.username"
          placeholder="请输入用户名"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input
          v-model="queryParams.phone"
          placeholder="请输入手机号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="用户状态" clearable style="width: 120px">
          <el-option label="启用" value="1" />
          <el-option label="未启用" value="0" />
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
        <el-button type="primary" plain icon="Plus" v-hasPermi="['system:user:add']" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" v-hasPermi="['system:user:remove']" @click="handleBatchDelete">删除</el-button>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-table
      v-loading="loading"
      :data="userList"
      border
      stripe
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="用户名" prop="username" align="center" min-width="100" show-overflow-tooltip />
      <el-table-column label="昵称" prop="nickname" align="center" min-width="100" show-overflow-tooltip />
      <el-table-column label="部门" prop="deptName" align="center" min-width="100" show-overflow-tooltip />
      <el-table-column label="手机号" prop="phone" align="center" min-width="120" show-overflow-tooltip />
      <el-table-column label="状态" align="center" min-width="80">
        <template #default="{ row }">
          <el-switch
            v-model="row.status"
            :active-value="1"
            :inactive-value="0"
            @change="handleStatusChange(row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" align="center" min-width="160" show-overflow-tooltip />
      <el-table-column label="操作" align="center" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" icon="Edit" v-hasPermi="['system:user:edit']" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="primary" icon="Delete" v-hasPermi="['system:user:remove']" @click="handleDelete(row)">删除</el-button>
          <el-button link type="primary" icon="Key" v-hasPermi="['system:user:resetPwd']" @click="handleResetPwd(row)">重置</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <Pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名" :disabled="form.userId !== undefined" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="form.nickname" placeholder="请输入昵称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20" v-if="form.userId === undefined">
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="sex">
              <el-select v-model="form.sex" placeholder="请选择性别">
                <el-option label="男" :value="0" />
                <el-option label="女" :value="1" />
                <el-option label="未知" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio :value="1">启用</el-radio>
                <el-radio :value="0">未启用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="部门" prop="deptId">
              <TreeSelect
                v-model="form.deptId"
                :options="deptOptions"
                placeholder="请选择部门"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="岗位" prop="postIds">
              <el-select v-model="form.postIds" multiple placeholder="请选择岗位">
                <el-option label="董事长" value="1" />
                <el-option label="项目经理" value="2" />
                <el-option label="人力资源" value="3" />
                <el-option label="普通员工" value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="角色" prop="roleIds">
              <el-select v-model="form.roleIds" multiple placeholder="请选择角色" style="width: 100%">
                <el-option
                  v-for="item in roleOptions"
                  :key="item.roleId"
                  :label="item.roleName"
                  :value="item.roleId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" :rows="3" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </template>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog title="重置密码" v-model="resetPwdVisible" width="400px" append-to-body>
      <el-form ref="resetPwdFormRef" :model="resetPwdForm" :rules="resetPwdRules" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="resetPwdForm.username" disabled />
        </el-form-item>
        <el-form-item label="新密码" prop="password">
          <el-input v-model="resetPwdForm.password" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="resetPwdForm.confirmPassword" type="password" placeholder="请确认密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetPwdVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitResetPwd">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listUser, getUser, addUser, updateUser, delUser, resetUserPwd, getDeptTree } from '@/api/user'
import { listRole } from '@/api/role'
import Pagination from '@/components/Pagination.vue'
import TreeSelect from '@/components/TreeSelect.vue'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  phone: '',
  status: ''
})

const loading = ref(false)
const userList = ref([])
const total = ref(0)
const selectedRows = ref([])
const queryFormRef = ref(null)

// 部门树选项
const deptOptions = ref([])
// 角色选项
const roleOptions = ref([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const form = ref({
  userId: undefined,
  username: '',
  nickname: '',
  password: '',
  phone: '',
  email: '',
  sex: 0,
  status: 1,
  deptId: undefined,
  postIds: [],
  roleIds: [],
  remark: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在2到20个字符', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 4, max: 20, message: '密码长度在4到20个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 重置密码
const resetPwdVisible = ref(false)
const resetPwdFormRef = ref(null)
const resetPwdForm = reactive({
  userId: undefined,
  username: '',
  password: '',
  confirmPassword: ''
})

const resetPwdRules = {
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 4, max: 20, message: '密码长度在4到20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== resetPwdForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const res = await listUser(queryParams)
    const data = res.data || res
    userList.value = data.rows || data.list || []
    total.value = data.total || 0
  } catch (error) {
    userList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 搜索
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置搜索
const resetQuery = () => {
  if (queryFormRef.value) {
    queryFormRef.value.resetFields()
  }
  handleQuery()
}

// 多选
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 新增
const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增用户'
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row) => {
  resetForm()
  const userId = row.userId
  try {
    const res = await getUser(userId)
    const data = res.data || res
    form.value = {
      userId: data.userId,
      username: data.username,
      nickname: data.nickname,
      password: '',
      phone: data.phone,
      email: data.email,
      sex: data.sex !== undefined ? data.sex : 0,
      status: data.status !== undefined ? data.status : 1,
      deptId: data.deptId,
      postIds: data.postIds || [],
      roleIds: data.roleIds || [],
      remark: data.remark
    }
    dialogTitle.value = '编辑用户'
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(`确认要删除用户"${row.username}"吗？`, '系统提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await delUser(row.userId)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      // 错误已在拦截器中处理
    }
  }).catch(() => {})
}

// 批量删除
const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的用户')
    return
  }
  const ids = selectedRows.value.map(item => item.userId).join(',')
  ElMessageBox.confirm(`确认要删除选中的${selectedRows.value.length}个用户吗？`, '系统提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await delUser(ids)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      // 错误已在拦截器中处理
    }
  }).catch(() => {})
}

// 状态修改
const handleStatusChange = async (row) => {
  const text = row.status === 1 ? '启用' : '停用'
  try {
    ElMessageBox.confirm(`确认要${text}用户"${row.username}"吗？`, '系统提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      await updateUser({ userId: row.userId, status: row.status })
      ElMessage.success(`${text}成功`)
    }).catch(() => {
      row.status = row.status === 1 ? 0 : 1
    })
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1
  }
}

// 重置密码
const handleResetPwd = (row) => {
  resetPwdForm.userId = row.userId
  resetPwdForm.username = row.username
  resetPwdForm.password = ''
  resetPwdForm.confirmPassword = ''
  resetPwdVisible.value = true
}

const submitResetPwd = () => {
  resetPwdFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await resetUserPwd(resetPwdForm.userId, resetPwdForm.password)
        ElMessage.success('密码重置成功')
        resetPwdVisible.value = false
      } catch (error) {
        // 错误已在拦截器中处理
      }
    }
  })
}

// 提交表单
const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.value.userId) {
          await updateUser(form.value)
          ElMessage.success('修改成功')
        } else {
          await addUser(form.value)
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
  form.value = {
    userId: undefined,
    username: '',
    nickname: '',
    password: '',
    phone: '',
    email: '',
    sex: 0,
    status: 1,
    deptId: undefined,
    postIds: [],
    roleIds: [],
    remark: ''
  }
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 获取部门树
const getDeptTreeData = async () => {
  try {
    const res = await getDeptTree()
    const data = res.data || res
    deptOptions.value = data.depts || data || []
  } catch (error) {
    deptOptions.value = []
  }
}

// 获取角色列表
const getRoleList = async () => {
  try {
    const res = await listRole({ pageNum: 1, pageSize: 100 })
    const data = res.data || res
    roleOptions.value = data.rows || data.list || data || []
  } catch (error) {
    roleOptions.value = []
  }
}

onMounted(() => {
  getList()
  getDeptTreeData()
  getRoleList()
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
