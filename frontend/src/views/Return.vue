<template>
  <div class="page-container">
    <div class="page-header">
      <span class="page-title">退货管理</span>
      <el-button type="primary">
        <el-icon><Plus /></el-icon>
        新增退货单
      </el-button>
    </div>
    <el-empty description="退货管理功能开发中...">
      <template #description>
        <span>包含退货入库、质检结果、异常上报等功能</span>
      </template>
    </el-empty>
  </div>
</template>

<script setup>
import { Plus } from '@element-plus/icons-vue'
</script>
