<template>
  <el-container class="app-container">
    <el-header class="app-header">
      <div class="logo">
        <el-icon><Box /></el-icon>
        <span>仓储物流配送管理系统</span>
      </div>
      <div class="header-right">
        <el-text>欢迎使用</el-text>
      </div>
    </el-header>
    <el-container>
      <el-aside width="220px" class="app-aside">
        <el-menu
          :default-active="activeMenu"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
        >
          <el-menu-item index="/sku">
            <el-icon><Goods /></el-icon>
            <span>货品档案</span>
          </el-menu-item>
          <el-menu-item index="/stock-in">
            <el-icon><Download /></el-icon>
            <span>入库管理</span>
          </el-menu-item>
          <el-menu-item index="/stock-out">
            <el-icon><Upload /></el-icon>
            <span>出库管理</span>
          </el-menu-item>
          <el-menu-item index="/inventory">
            <el-icon><Coin /></el-icon>
            <span>库存管理</span>
          </el-menu-item>
          <el-menu-item index="/location">
            <el-icon><Grid /></el-icon>
            <span>货位管理</span>
          </el-menu-item>
          <el-menu-item index="/waybill">
            <el-icon><Van /></el-icon>
            <span>配送管理</span>
          </el-menu-item>
          <el-menu-item index="/return">
            <el-icon><RefreshLeft /></el-icon>
            <span>退货管理</span>
          </el-menu-item>
          <el-menu-item index="/report">
            <el-icon><DataAnalysis /></el-icon>
            <span>报表中心</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="app-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { 
  Box, Goods, Download, Upload, Coin, Grid, 
  Van, RefreshLeft, DataAnalysis 
} from '@element-plus/icons-vue'

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  height: 100%;
  width: 100%;
}

.app-container {
  height: 100%;
}

.app-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  color: white;
}

.app-header .logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: bold;
}

.app-header .header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.app-aside {
  background-color: #304156;
}

.app-main {
  background-color: #f5f7fa;
  padding: 20px;
}

.page-container {
  background: white;
  border-radius: 8px;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.search-form {
  margin-bottom: 20px;
}
</style>
