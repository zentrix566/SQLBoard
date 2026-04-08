import request from './request'

export function executeSql(data) {
  return request.post('/sql/execute', data)
}

export function exportSql(data) {
  return request.post('/sql/export', data, {
    responseType: 'blob'
  })
}
