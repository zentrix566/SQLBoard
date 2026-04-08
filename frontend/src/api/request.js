import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken } from './auth'
import { useRouter } from 'vue-router'

const request = axios.create({
  baseURL: '/api',
  timeout: 300000
})

// 请求拦截器：添加 token
request.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    if (error.response && error.response.status === 401) {
      // 未授权，清除 token 跳转到登录页
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('nickname')
      window.location.reload()
    }
    ElMessage.error(error.message)
    return Promise.reject(error)
  }
)

export default request
