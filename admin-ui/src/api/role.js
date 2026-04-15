import request from '@/utils/request'

// 查询角色列表
export function listRole(query) {
  return request({
    url: '/api/system/role/list',
    method: 'get',
    params: query
  })
}

// 查询角色详细
export function getRole(roleId) {
  return request({
    url: '/api/system/role/' + roleId,
    method: 'get'
  })
}

// 新增角色
export function addRole(data) {
  return request({
    url: '/api/system/role',
    method: 'post',
    data: data
  })
}

// 修改角色
export function updateRole(data) {
  return request({
    url: '/api/system/role',
    method: 'put',
    data: data
  })
}

// 删除角色
export function delRole(roleId) {
  return request({
    url: '/api/system/role/' + roleId,
    method: 'delete'
  })
}

// 查询角色已授权菜单ID列表
export function getRoleMenuTree(roleId) {
  return request({
    url: '/api/system/role/menuIds/' + roleId,
    method: 'get'
  })
}

// 保存角色菜单权限
export function updateRoleMenu(data) {
  return request({
    url: '/api/system/role/assignMenus',
    method: 'put',
    data: data
  })
}

// 查询角色已授权部门ID列表
export function getRoleDeptTree(roleId) {
  return request({
    url: '/api/system/role/deptIds/' + roleId,
    method: 'get'
  })
}

// 保存角色数据权限
export function updateRoleDataScope(data) {
  return request({
    url: '/api/system/role/assignDepts',
    method: 'put',
    data: data
  })
}
