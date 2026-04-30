import request from '@/utils/request'

export function getSkuList(params) {
  return request({
    url: '/sku/page',
    method: 'get',
    params
  })
}

export function getSkuById(id) {
  return request({
    url: `/sku/${id}`,
    method: 'get'
  })
}

export function addSku(data) {
  return request({
    url: '/sku',
    method: 'post',
    data
  })
}

export function updateSku(data) {
  return request({
    url: '/sku',
    method: 'put',
    data
  })
}

export function deleteSku(id) {
  return request({
    url: `/sku/${id}`,
    method: 'delete'
  })
}

export function getSkuListAll() {
  return request({
    url: '/sku/list',
    method: 'get'
  })
}
