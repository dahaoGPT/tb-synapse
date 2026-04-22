<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" class="search-form">
      <el-form-item label="角色名称" prop="roleName">
        <el-input
          v-model="queryParams.roleName"
          placeholder="请输入角色名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="角色状态" clearable style="width: 120px">
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
        <el-button type="primary" plain icon="Plus" v-hasPermi="['system:role:add']" @click="handleAdd">新增</el-button>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="roleList" border stripe>
      <el-table-column label="角色名称" prop="roleName" align="center" min-width="120" show-overflow-tooltip />
      <el-table-column label="角色编码" prop="roleCode" align="center" min-width="120" show-overflow-tooltip />
      <el-table-column label="排序" prop="sortOrder" align="center" width="80" />
      <el-table-column label="状态" align="center" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" align="center" min-width="160" show-overflow-tooltip />
      <el-table-column label="操作" align="center" width="280" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" icon="Edit" v-hasPermi="['system:role:edit']" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="primary" icon="Delete" v-hasPermi="['system:role:remove']" @click="handleDelete(row)">删除</el-button>
          <el-button link type="primary" icon="CircleCheck" v-hasPermi="['system:role:edit']" @click="handleAuthMenu(row)">分配菜单</el-button>
          <el-button link type="primary" icon="DataLine" v-hasPermi="['system:role:edit']" @click="handleAuthDataScope(row)">数据权限</el-button>
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
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="请输入角色编码" :disabled="form.id !== undefined" />
        </el-form-item>
        <el-form-item label="角色排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" controls-position="right" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单权限" prop="menuIds">
          <el-checkbox v-model="menuExpand" @change="handleCheckedTreeExpand">展开/折叠</el-checkbox>
          <el-checkbox v-model="menuNodeAll" @change="handleCheckedTreeNodeAll">全选/全不选</el-checkbox>
          <el-tree
            ref="menuTreeRef"
            :data="menuOptions"
            show-checkbox
            node-key="id"
            :check-strictly="false"
            empty-text="加载中..."
            :props="{ label: 'menuName', children: 'children' }"
            class="menu-tree"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </template>
    </el-dialog>

    <!-- 分配菜单权限对话框 -->
    <el-dialog title="分配菜单权限" v-model="authMenuVisible" width="500px" append-to-body destroy-on-close>
      <el-form label-width="80px">
        <el-form-item label="角色名称">
          <el-input v-model="authMenuForm.roleName" disabled />
        </el-form-item>
        <el-form-item label="权限标识">
          <el-input v-model="authMenuForm.roleCode" disabled />
        </el-form-item>
        <el-form-item label="菜单权限">
          <el-checkbox v-model="menuExpand" @change="handleCheckedTreeExpand">展开/折叠</el-checkbox>
          <el-checkbox v-model="menuNodeAll" @change="handleCheckedTreeNodeAll">全选/全不选</el-checkbox>
          <el-tree
            ref="authMenuTreeRef"
            :data="menuOptions"
            show-checkbox
            node-key="id"
            :check-strictly="false"
            empty-text="加载中..."
            :props="{ label: 'menuName', children: 'children' }"
            class="menu-tree"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="authMenuVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitAuthMenu">确 定</el-button>
      </template>
    </el-dialog>

    <!-- 分配数据权限对话框 -->
    <el-dialog title="分配数据权限" v-model="dataScopeVisible" width="500px" append-to-body destroy-on-close>
      <el-form ref="dataScopeFormRef" :model="dataScopeForm" :rules="dataScopeRules" label-width="100px">
        <el-form-item label="角色名称">
          <el-input v-model="dataScopeForm.roleName" disabled />
        </el-form-item>
        <el-form-item label="权限字符">
          <el-input v-model="dataScopeForm.roleCode" disabled />
        </el-form-item>
        <el-form-item label="数据范围" prop="dataScope">
          <el-select v-model="dataScopeForm.dataScope" style="width: 100%">
            <el-option label="全部数据权限" value="1" />
            <el-option label="自定数据权限" value="2" />
            <el-option label="本部门数据权限" value="3" />
            <el-option label="本部门及以下数据权限" value="4" />
            <el-option label="仅本人数据权限" value="5" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="dataScopeForm.dataScope === '2'" label="部门权限">
          <el-checkbox v-model="deptExpand" @change="handleDeptCheckedTreeExpand">展开/折叠</el-checkbox>
          <el-checkbox v-model="deptNodeAll" @change="handleDeptCheckedTreeNodeAll">全选/全不选</el-checkbox>
          <el-tree
            ref="deptTreeRef"
            :data="deptOptions"
            show-checkbox
            node-key="id"
            :check-strictly="false"
            empty-text="加载中..."
            :props="{ label: 'deptName', children: 'children' }"
            class="menu-tree"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dataScopeVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitDataScope">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listRole, getRole, addRole, updateRole, delRole, getRoleMenuTree, updateRoleMenu, getRoleDeptTree, updateRoleDataScope } from '@/api/role'
import { getMenuTreeSelect } from '@/api/menu'
import { getDeptTreeSelect } from '@/api/dept'
import Pagination from '@/components/Pagination.vue'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  roleName: '',
  status: ''
})

const loading = ref(false)
const roleList = ref([])
const total = ref(0)
const queryFormRef = ref(null)

// 菜单树选项
const menuOptions = ref([])
// 部门树选项
const deptOptions = ref([])

// 展开/折叠
const menuExpand = ref(false)
const menuNodeAll = ref(false)
const deptExpand = ref(false)
const deptNodeAll = ref(false)

// 树引用
const menuTreeRef = ref(null)
const authMenuTreeRef = ref(null)
const deptTreeRef = ref(null)

// 新增/编辑对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const form = ref({
  id: undefined,
  roleName: '',
  roleCode: '',
  sortOrder: 0,
  status: 1,
  menuIds: [],
  remark: ''
})

const rules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ],
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' }
  ],
  sortOrder: [
    { required: true, message: '请输入排序', trigger: 'blur' }
  ]
}

// 分配菜单权限
const authMenuVisible = ref(false)
const authMenuForm = reactive({
  roleId: undefined,
  roleName: '',
  roleCode: '',
  menuIds: []
})

// 分配数据权限
const dataScopeVisible = ref(false)
const dataScopeFormRef = ref(null)
const dataScopeForm = reactive({
  roleId: undefined,
  roleName: '',
  roleCode: '',
  dataScope: '1',
  deptIds: []
})

const dataScopeRules = {
  dataScope: [
    { required: true, message: '请选择数据范围', trigger: 'change' }
  ]
}

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const res = await listRole(queryParams)
    const data = res.data || res
    roleList.value = data.rows || data.list || []
    total.value = data.total || 0
  } catch (error) {
    roleList.value = []
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

// 新增
const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增角色'
  dialogVisible.value = true
  nextTick(() => {
    if (menuTreeRef.value) {
      menuTreeRef.value.setCheckedKeys([])
    }
  })
}

// 编辑
const handleEdit = async (row) => {
  resetForm()
  try {
    const res = await getRole(row.id)
    const data = res.data || res
    form.value = {
      id: data.id,
      roleName: data.roleName,
      roleCode: data.roleCode,
      sortOrder: data.sortOrder,
      status: data.status,
      menuIds: data.menuIds || [],
      remark: data.remark
    }
    dialogTitle.value = '编辑角色'
    dialogVisible.value = true
    nextTick(() => {
      if (menuTreeRef.value && data.menuIds) {
        menuTreeRef.value.setCheckedKeys(data.menuIds)
      }
    })
  } catch (error) {
    ElMessage.error('获取角色信息失败')
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(`确认要删除角色"${row.roleName}"吗？`, '系统提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await delRole(row.id)
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
        const menuIds = menuTreeRef.value ? menuTreeRef.value.getCheckedKeys(true) : []
        const submitData = { ...form.value, menuIds: menuIds }
        if (form.value.id) {
          await updateRole(submitData)
          ElMessage.success('修改成功')
        } else {
          await addRole(submitData)
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
    id: undefined,
    roleName: '',
    roleCode: '',
    sortOrder: 0,
    status: 1,
    menuIds: [],
    remark: ''
  }
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 分配菜单权限
const handleAuthMenu = async (row) => {
  authMenuForm.roleId = row.id
  authMenuForm.roleName = row.roleName
  authMenuForm.roleCode = row.roleCode
  authMenuVisible.value = true
  try {
    // 先加载菜单树数据
    await getMenuTreeData()
    // 再获取该角色已勾选的菜单ID
    const res = await getRoleMenuTree(row.id)
    const checkedKeys = res.data || res || []
    nextTick(() => {
      if (authMenuTreeRef.value && checkedKeys.length) {
        authMenuTreeRef.value.setCheckedKeys(checkedKeys)
      }
    })
  } catch (error) {
    console.error('加载菜单权限失败', error)
  }
}

// 提交菜单权限
const submitAuthMenu = () => {
  const menuIds = authMenuTreeRef.value ? authMenuTreeRef.value.getCheckedKeys(true) : []
  updateRoleMenu({
    roleId: authMenuForm.roleId,
    menuIds: menuIds
  }).then(() => {
    ElMessage.success('分配菜单权限成功')
    authMenuVisible.value = false
  }).catch(() => {})
}

// 分配数据权限
const handleAuthDataScope = async (row) => {
  dataScopeForm.roleId = row.id
  dataScopeForm.roleName = row.roleName
  dataScopeForm.roleCode = row.roleCode
  dataScopeForm.dataScope = '1'
  dataScopeForm.deptIds = []
  dataScopeVisible.value = true
  try {
    const res = await getRoleDeptTree(row.id)
    const data = res.data || res
    deptOptions.value = data.depts || data || []
    dataScopeForm.dataScope = data.dataScope || '1'
    nextTick(() => {
      if (deptTreeRef.value && data.checkedKeys) {
        deptTreeRef.value.setCheckedKeys(data.checkedKeys)
      }
    })
  } catch (error) {
    deptOptions.value = []
  }
}

// 提交数据权限
const submitDataScope = () => {
  dataScopeFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const deptIds = dataScopeForm.dataScope === '2' && deptTreeRef.value
          ? deptTreeRef.value.getCheckedKeys(true)
          : []
        await updateRoleDataScope({
          roleId: dataScopeForm.roleId,
          dataScope: dataScopeForm.dataScope,
          deptIds: deptIds
        })
        ElMessage.success('分配数据权限成功')
        dataScopeVisible.value = false
      } catch (error) {
        // 错误已在拦截器中处理
      }
    }
  })
}

// 树操作 - 展开/折叠
const handleCheckedTreeExpand = (value) => {
  const treeRef = authMenuVisible.value ? authMenuTreeRef.value : menuTreeRef.value
  if (!treeRef) return
  const treeData = treeRef.store._getAllNodes()
  treeData.forEach(data => {
    data.expanded = value
  })
}

// 树操作 - 全选/全不选
const handleCheckedTreeNodeAll = (value) => {
  const treeRef = authMenuVisible.value ? authMenuTreeRef.value : menuTreeRef.value
  if (!treeRef) return
  if (value) {
    const allKeys = []
    const getAllKeys = (nodes) => {
      nodes.forEach(node => {
        allKeys.push(node.id)
        if (node.children && node.children.length) {
          getAllKeys(node.children)
        }
      })
    }
    getAllKeys(menuOptions.value)
    treeRef.setCheckedKeys(allKeys)
  } else {
    treeRef.setCheckedKeys([])
  }
}

// 部门树 - 展开/折叠
const handleDeptCheckedTreeExpand = (value) => {
  if (!deptTreeRef.value) return
  const treeData = deptTreeRef.value.store._getAllNodes()
  treeData.forEach(data => {
    data.expanded = value
  })
}

// 部门树 - 全选/全不选
const handleDeptCheckedTreeNodeAll = (value) => {
  if (!deptTreeRef.value) return
  if (value) {
    const allKeys = []
    const getAllKeys = (nodes) => {
      nodes.forEach(node => {
        allKeys.push(node.id)
        if (node.children && node.children.length) {
          getAllKeys(node.children)
        }
      })
    }
    getAllKeys(deptOptions.value)
    deptTreeRef.value.setCheckedKeys(allKeys)
  } else {
    deptTreeRef.value.setCheckedKeys([])
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
  getMenuTreeData()
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

.menu-tree {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  min-height: 200px;
  max-height: 400px;
  overflow-y: auto;
  padding: 10px;
  width: 100%;
}
</style>
