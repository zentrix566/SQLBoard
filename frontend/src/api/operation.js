import request from './request'

export function listOperations(connectionId) {
  return request.get(`/operation/list/${connectionId}`)
}

export function getOperation(id) {
  return request.get(`/operation/${id}`)
}

export function saveOperation(data) {
  return request.post('/operation/save', data)
}

export function deleteOperation(id) {
  return request.delete(`/operation/${id}`)
}
