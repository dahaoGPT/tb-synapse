import request from '@/utils/request'

// 查询部门列表
export function listDept(query) {
  return request({
    url: '/api/system/dept/list',
    method: 'get',
    params: query
  })
}

// 查询部门详细
export function getDept(deptId) {
  return request({
    url: '/api/system/dept/' + deptId,
    method: 'get'
  })
}

// 新增部门
export function addDept(data) {
  return request({
    url: '/api/system/dept',
    method: 'post',
    data: data
  })
}

// 修改部门
export function updateDept(data) {
  return request({
    url: '/api/system/dept',
    method: 'put',
    data: data
  })
}

// 删除部门
export function delDept(deptId) {
  return request({
    url: '/api/system/dept/' + deptId,
    method: 'delete'
  })
}

// 获取部门下拉树列表
export function getDeptTreeSelect() {
  return request({
    url: '/api/system/dept/treeselect',
    method: 'get'
  })
}
