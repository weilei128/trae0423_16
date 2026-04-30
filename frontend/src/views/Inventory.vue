<template>
  <div class="page-container">
    <div class="page-header">
      <span class="page-title">库存管理</span>
      <div style="display: flex; gap: 10px">
        <el-button type="primary" @click="showStockDialog = true">
          <el-icon><Plus /></el-icon>
          增加库存
        </el-button>
        <el-button type="danger" @click="showDeductDialog = true">
          <el-icon><Minus /></el-icon>
          扣减库存
        </el-button>
      </div>
    </div>

    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="SKU编码">
        <el-input v-model="searchForm.skuCode" placeholder="请输入SKU编码" clearable />
      </el-form-item>
      <el-form-item label="SKU名称">
        <el-input v-model="searchForm.skuName" placeholder="请输入SKU名称" clearable />
      </el-form-item>
      <el-form-item label="货位编码">
        <el-input v-model="searchForm.locationCode" placeholder="请输入货位编码" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="skuCode" label="SKU编码" width="120" />
      <el-table-column prop="skuName" label="SKU名称" width="180" />
      <el-table-column prop="locationCode" label="货位编码" width="120" />
      <el-table-column prop="batchNo" label="批次号" width="150" />
      <el-table-column prop="stock" label="总库存" width="100">
        <template #default="{ row }">
          <el-text :type="row.stock < 10 ? 'danger' : 'primary'" type="primary">
            {{ row.stock }}
          </el-text>
        </template>
      </el-table-column>
      <el-table-column prop="lockedStock" label="锁定库存" width="100" />
      <el-table-column prop="availableStock" label="可用库存" width="100">
        <template #default="{ row }">
          <el-text :type="row.availableStock < 10 ? 'danger' : 'success'">
            {{ row.availableStock }}
          </el-text>
        </template>
      </el-table-column>
      <el-table-column prop="expiryDate" label="有效期至" width="120" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleLockStock(row)">锁定</el-button>
          <el-button type="warning" link @click="handleUnlockStock(row)">解锁</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pagination.pageNum"
      v-model:page-size="pagination.pageSize"
      :page-sizes="[10, 20, 50, 100]"
      :total="pagination.total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="fetchData"
      @current-change="fetchData"
      style="margin-top: 20px; justify-content: flex-end"
    />

    <el-dialog v-model="showStockDialog" title="增加库存" width="500px">
      <el-form :model="stockForm" label-width="100px">
        <el-form-item label="SKU ID">
          <el-input v-model="stockForm.skuId" type="number" placeholder="请输入SKU ID" />
        </el-form-item>
        <el-form-item label="货位 ID">
          <el-input v-model="stockForm.locationId" type="number" placeholder="请输入货位 ID" />
        </el-form-item>
        <el-form-item label="批次号">
          <el-input v-model="stockForm.batchNo" placeholder="请输入批次号" />
        </el-form-item>
        <el-form-item label="数量">
          <el-input v-model="stockForm.quantity" type="number" placeholder="请输入数量" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showStockDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAddStock">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showDeductDialog" title="扣减库存" width="500px">
      <el-form :model="deductForm" label-width="100px">
        <el-form-item label="SKU ID">
          <el-input v-model="deductForm.skuId" type="number" placeholder="请输入SKU ID" />
        </el-form-item>
        <el-form-item label="货位 ID">
          <el-input v-model="deductForm.locationId" type="number" placeholder="请输入货位 ID" />
        </el-form-item>
        <el-form-item label="批次号">
          <el-input v-model="deductForm.batchNo" placeholder="请输入批次号" />
        </el-form-item>
        <el-form-item label="数量">
          <el-input v-model="deductForm.quantity" type="number" placeholder="请输入数量" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDeductDialog = false">取消</el-button>
        <el-button type="primary" @click="handleDeductStock">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Minus } from '@element-plus/icons-vue'
import { getInventoryList, addStock, deductStock, lockStock, unlockStock } from '@/api/inventory'

const loading = ref(false)
const tableData = ref([])
const showStockDialog = ref(false)
const showDeductDialog = ref(false)

const searchForm = reactive({
  skuCode: '',
  skuName: '',
  locationCode: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const stockForm = reactive({
  skuId: null,
  locationId: null,
  batchNo: '',
  quantity: null
})

const deductForm = reactive({
  skuId: null,
  locationId: null,
  batchNo: '',
  quantity: null
})

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const res = await getInventoryList(params)
    tableData.value = res.data.records || []
    pagination.total = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

const handleReset = () => {
  searchForm.skuCode = ''
  searchForm.skuName = ''
  searchForm.locationCode = ''
  handleSearch()
}

const handleAddStock = async () => {
  try {
    await addStock(stockForm)
    ElMessage.success('增加库存成功')
    showStockDialog.value = false
    fetchData()
  } catch (e) {
    console.error(e)
  }
}

const handleDeductStock = async () => {
  try {
    await deductStock(deductForm)
    ElMessage.success('扣减库存成功')
    showDeductDialog.value = false
    fetchData()
  } catch (e) {
    console.error(e)
  }
}

const handleLockStock = (row) => {
  ElMessageBox.prompt('请输入锁定数量', '锁定库存', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /^\d+$/,
    inputErrorMessage: '请输入有效的数字'
  }).then(async ({ value }) => {
    try {
      await lockStock({
        skuId: row.skuId,
        locationId: row.locationId,
        batchNo: row.batchNo,
        quantity: parseInt(value)
      })
      ElMessage.success('锁定成功')
      fetchData()
    } catch (e) {
      console.error(e)
    }
  }).catch(() => {})
}

const handleUnlockStock = (row) => {
  ElMessageBox.prompt('请输入解锁数量', '解锁库存', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /^\d+$/,
    inputErrorMessage: '请输入有效的数字'
  }).then(async ({ value }) => {
    try {
      await unlockStock({
        skuId: row.skuId,
        locationId: row.locationId,
        batchNo: row.batchNo,
        quantity: parseInt(value)
      })
      ElMessage.success('解锁成功')
      fetchData()
    } catch (e) {
      console.error(e)
    }
  }).catch(() => {})
}

onMounted(() => {
  fetchData()
})
</script>
