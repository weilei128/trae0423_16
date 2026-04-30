import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/sku'
  },
  {
    path: '/sku',
    name: 'Sku',
    component: () => import('@/views/Sku.vue'),
    meta: { title: '货品档案' }
  },
  {
    path: '/stock-in',
    name: 'StockIn',
    component: () => import('@/views/StockIn.vue'),
    meta: { title: '入库管理' }
  },
  {
    path: '/stock-out',
    name: 'StockOut',
    component: () => import('@/views/StockOut.vue'),
    meta: { title: '出库管理' }
  },
  {
    path: '/inventory',
    name: 'Inventory',
    component: () => import('@/views/Inventory.vue'),
    meta: { title: '库存管理' }
  },
  {
    path: '/location',
    name: 'Location',
    component: () => import('@/views/Location.vue'),
    meta: { title: '货位管理' }
  },
  {
    path: '/waybill',
    name: 'Waybill',
    component: () => import('@/views/Waybill.vue'),
    meta: { title: '配送管理' }
  },
  {
    path: '/return',
    name: 'Return',
    component: () => import('@/views/Return.vue'),
    meta: { title: '退货管理' }
  },
  {
    path: '/report',
    name: 'Report',
    component: () => import('@/views/Report.vue'),
    meta: { title: '报表中心' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 仓储物流配送管理系统` : '仓储物流配送管理系统'
  next()
})

export default router
