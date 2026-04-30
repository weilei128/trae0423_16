import request from '@/utils/request'

export function getSupplierList(params) {
  return request({
    url: '/supplier/page',
    method: 'get',
    params
  })
}

export function getSupplierById(id) {
  return request({
    url: `/supplier/${id}`,
    method: 'get'
  })
}

export function addSupplier(data) {
  return request({
    url: '/supplier',
    method: 'post',
    data
  })
}

export function updateSupplier(data) {
  return request({
    url: '/supplier',
    method: 'put',
    data
  })
}

export function deleteSupplier(id) {
  return request({
    url: `/supplier/${id}`,
    method: 'delete'
  })
}

export function getSupplierListAll() {
  return request({
    url: '/supplier/list',
    method: 'get'
  })
}
