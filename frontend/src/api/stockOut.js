import request from '@/utils/request'

export function getStockOutList(params) {
  return request({
    url: '/stock-out/page',
    method: 'get',
    params
  })
}

export function getStockOutById(id) {
  return request({
    url: `/stock-out/${id}`,
    method: 'get'
  })
}

export function getStockOutDetails(id) {
  return request({
    url: `/stock-out/${id}/details`,
    method: 'get'
  })
}

export function createStockOut(data, details) {
  return request({
    url: '/stock-out',
    method: 'post',
    data: {
      ...data,
      details: details
    }
  })
}

export function updateStockOut(data) {
  return request({
    url: '/stock-out',
    method: 'put',
    data
  })
}

export function pickStockOut(id, details) {
  return request({
    url: `/stock-out/pick/${id}`,
    method: 'post',
    data: details
  })
}

export function checkStockOut(id, details) {
  return request({
    url: `/stock-out/check/${id}`,
    method: 'post',
    data: details
  })
}

export function cancelStockOut(id) {
  return request({
    url: `/stock-out/cancel/${id}`,
    method: 'post'
  })
}
