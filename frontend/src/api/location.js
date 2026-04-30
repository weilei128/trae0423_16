import request from '@/utils/request'

export function getLocationList(params) {
  return request({
    url: '/location/page',
    method: 'get',
    params
  })
}

export function getLocationById(id) {
  return request({
    url: `/location/${id}`,
    method: 'get'
  })
}

export function getLocationByCode(locationCode) {
  return request({
    url: `/location/code/${locationCode}`,
    method: 'get'
  })
}

export function addLocation(data) {
  return request({
    url: '/location',
    method: 'post',
    data
  })
}

export function updateLocation(data) {
  return request({
    url: '/location',
    method: 'put',
    data
  })
}

export function deleteLocation(id) {
  return request({
    url: `/location/${id}`,
    method: 'delete'
  })
}

export function clearCache(locationCode) {
  return request({
    url: `/location/clearCache/${locationCode}`,
    method: 'post'
  })
}
