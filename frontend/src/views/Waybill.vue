<template>
  <div class="page-container">
    <div class="page-header">
      <span class="page-title">配送管理</span>
      <el-button type="primary">
        <el-icon><Plus /></el-icon>
        新增运单
      </el-button>
    </div>
    <el-empty description="配送管理功能开发中...">
      <template #description>
        <span>包含运单生成、承运商配置、配送状态追踪等功能</span>
      </template>
    </el-empty>
  </div>
</template>

<script setup>
import { Plus } from '@element-plus/icons-vue'
</script>
