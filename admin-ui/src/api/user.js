import request from '@/utils/request'

// 查询用户列表
export function listUser(query) {
  return request({
    url: '/api/system/user/list',
    method: 'get',
    params: query
  })
}

// 查询用户详细
export function getUser(userId) {
  return request({
    url: '/api/system/user/' + userId,
    method: 'get'
  })
}

// 新增用户
export function addUser(data) {
  return request({
    url: '/api/system/user',
    method: 'post',
    data: data
  })
}

// 修改用户
export function updateUser(data) {
  return request({
    url: '/api/system/user',
    method: 'put',
    data: data
  })
}

// 删除用户
export function delUser(userId) {
  return request({
    url: '/api/system/user/' + userId,
    method: 'delete'
  })
}

// 重置密码
export function resetUserPwd(userId, password) {
  return request({
    url: '/api/system/user/resetPwd',
    method: 'put',
    data: {
      userId: userId,
      password: password
    }
  })
}

// 获取部门树
export function getDeptTree() {
  return request({
    url: '/api/system/dept/treeselect',
    method: 'get'
  })
}
