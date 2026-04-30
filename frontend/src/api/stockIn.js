import request from '@/utils/request'

export function getStockInList(params) {
  return request({
    url: '/stock-in/page',
    method: 'get',
    params
  })
}

export function getStockInById(id) {
  return request({
    url: `/stock-in/${id}`,
    method: 'get'
  })
}

export function getStockInDetails(id) {
  return request({
    url: `/stock-in/${id}/details`,
    method: 'get'
  })
}

export function createStockIn(data, details) {
  return request({
    url: '/stock-in',
    method: 'post',
    data: {
      stockIn: data,
      details: details
    }
  })
}

export function updateStockIn(data) {
  return request({
    url: '/stock-in',
    method: 'put',
    data
  })
}

export function inspectStockIn(id, details) {
  return request({
    url: `/stock-in/inspect/${id}`,
    method: 'post',
    data: details
  })
}

export function shelveStockIn(id, details) {
  return request({
    url: `/stock-in/shelve/${id}`,
    method: 'post',
    data: details
  })
}

export function cancelStockIn(id) {
  return request({
    url: `/stock-in/cancel/${id}`,
    method: 'post'
  })
}
