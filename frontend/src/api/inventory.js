import request from '@/utils/request'

export function getInventoryList(params) {
  return request({
    url: '/inventory/page',
    method: 'get',
    params
  })
}

export function getInventoryById(id) {
  return request({
    url: `/inventory/${id}`,
    method: 'get'
  })
}

export function addInventory(data) {
  return request({
    url: '/inventory',
    method: 'post',
    data
  })
}

export function updateInventory(data) {
  return request({
    url: '/inventory',
    method: 'put',
    data
  })
}

export function deleteInventory(id) {
  return request({
    url: `/inventory/${id}`,
    method: 'delete'
  })
}

export function deductStock(data) {
  return request({
    url: '/inventory/deduct',
    method: 'post',
    params: data
  })
}

export function addStock(data) {
  return request({
    url: '/inventory/add',
    method: 'post',
    params: data
  })
}

export function lockStock(data) {
  return request({
    url: '/inventory/lock',
    method: 'post',
    params: data
  })
}

export function unlockStock(data) {
  return request({
    url: '/inventory/unlock',
    method: 'post',
    params: data
  })
}
