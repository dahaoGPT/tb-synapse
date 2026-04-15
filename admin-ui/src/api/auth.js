import request from '@/utils/request'

// 登录
export function login(data) {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data: data
  })
}

// 获取用户信息
export function getInfo() {
  return request({
    url: '/api/auth/info',
    method: 'get'
  })
}

// 退出登录
export function logout() {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
}

// 获取路由菜单
export function getRouters() {
  return request({
    url: '/api/auth/routes',
    method: 'get'
  })
}
