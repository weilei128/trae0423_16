import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

request.interceptors.request.use(
  (config) => {
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response) => {
    const res = response.data
    return res
  },
  (error) => {
    let message = '网络错误'
    if (error.response) {
      message = `服务器错误: ${error.response.status}`
    } else if (error.message) {
      message = error.message
    }
    return Promise.reject(new Error(message))
  }
)

export default request
