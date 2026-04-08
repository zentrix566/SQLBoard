import request from './request'

export function listConnections() {
  return request.get('/connection/list')
}

export function getConnection(id) {
  return request.get(`/connection/${id}`)
}

export function saveConnection(data) {
  return request.post('/connection/save', data)
}

export function deleteConnection(id) {
  return request.delete(`/connection/${id}`)
}
