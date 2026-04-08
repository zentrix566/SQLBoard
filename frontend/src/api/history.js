import request from './request'

export function listHistory(pageNum, pageSize, connectionId) {
  return request({
    url: '/history/list',
    method: 'get',
    params: {
      pageNum,
      pageSize,
      connectionId
    }
  })
}
