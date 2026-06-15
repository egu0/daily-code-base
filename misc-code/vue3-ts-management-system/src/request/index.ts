import axios from 'axios'

const service = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

service.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (!token) {
    config.headers.token = token
  }
  return config
})

service.interceptors.response.use((res) => {
  if (res.data.code != 200) {
    return Promise.reject(res.data)
  } else {
    return res
  }
})

export default service
