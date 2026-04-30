<template>
  <div class="page-container">
    <div class="page-header">
      <span class="page-title">入库管理</span>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增入库单
      </el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="inNo" label="入库单号" width="200" />
      <el-table-column prop="inType" label="入库类型" width="120">
        <template #default="{ row }">
          <el-tag :type="row.inType === 'PURCHASE' ? 'primary' : 'warning'">
            {{ row.inType === 'PURCHASE' ? '采购入库' : '退货入库' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="supplierName" label="供应商" width="150" />
      <el-table-column prop="totalQty" label="总数量" width="100" />
      <el-table-column prop="inspectedQty" label="已验货" width="100" />
      <el-table-column prop="shelvedQty" label="已上架" width="100" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link :disabled="row.status !== 0 && row.status !== 1" @click="handleInspect(row)">验货</el-button>
          <el-button type="success" link :disabled="row.status !== 2 && row.status !== 3" @click="handleShelve(row)">上架</el-button>
          <el-button type="danger" link :disabled="row.status === 4" @click="handleCancel(row)">取消</el-button>
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

    <el-dialog v-model="dialogVisible" title="新增入库单" width="800px">
      <el-form :model="form" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入库类型">
              <el-select v-model="form.inType" placeholder="请选择入库类型" style="width: 100%">
                <el-option label="采购入库" value="PURCHASE" />
                <el-option label="退货入库" value="RETURN" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商">
              <el-select v-model="form.supplierId" placeholder="请选择供应商" style="width: 100%">
                <el-option
                  v-for="item in supplierList"
                  :key="item.id"
                  :label="item.supplierName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="入库明细">
          <el-table :data="formDetails" border size="small">
            <el-table-column label="SKU编码" width="150">
              <template #default="{ row, $index }">
                <el-select v-model="row.skuId" placeholder="选择SKU" @change="(id) => handleSkuChange(id, $index)" style="width: 100%">
                  <el-option
                    v-for="item in skuList"
                    :key="item.id"
                    :label="item.skuName"
                    :value="item.id"
                  />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="SKU名称" width="150">
              <template #default="{ row }">
                <span>{{ row.skuName }}</span>
              </template>
            </el-table-column>
            <el-table-column label="计划数量" width="120">
              <template #default="{ row }">
                <el-input-number v-model="row.planQty" :min="1" :max="99999" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template #default="{ $index }">
                <el-button type="danger" link @click="handleRemoveDetail($index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div style="margin-top: 10px">
            <el-button type="primary" link @click="handleAddDetail">
              <el-icon><Plus /></el-icon>
              添加明细
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getStockInList, createStockIn, inspectStockIn, shelveStockIn, cancelStockIn } from '@/api/stockIn'
import { getSupplierListAll } from '@/api/supplier'
import { getSkuListAll } from '@/api/sku'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const supplierList = ref([])
const skuList = ref([])

const statusMap = {
  0: { text: '待验货', type: 'info' },
  1: { text: '验货中', type: 'warning' },
  2: { text: '待上架', type: '' },
  3: { text: '上架中', type: 'primary' },
  4: { text: '已完成', type: 'success' },
  5: { text: '已取消', type: 'danger' }
}

const getStatusText = (status) => statusMap[status]?.text || '未知'
const getStatusType = (status) => statusMap[status]?.type || ''

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  inType: 'PURCHASE',
  supplierId: null
})

const formDetails = ref([])

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await getStockInList(params)
    tableData.value = res.data.records || []
    pagination.total = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const fetchSupplierList = async () => {
  try {
    const res = await getSupplierListAll()
    supplierList.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const fetchSkuList = async () => {
  try {
    const res = await getSkuListAll()
    skuList.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const handleAdd = () => {
  form.inType = 'PURCHASE'
  form.supplierId = null
  formDetails.value = []
  dialogVisible.value = true
}

const handleAddDetail = () => {
  formDetails.value.push({
    skuId: null,
    skuCode: '',
    skuName: '',
    planQty: 1
  })
}

const handleRemoveDetail = (index) => {
  formDetails.value.splice(index, 1)
}

const handleSkuChange = (id, index) => {
  const sku = skuList.value.find(item => item.id === id)
  if (sku) {
    formDetails.value[index].skuCode = sku.skuCode
    formDetails.value[index].skuName = sku.skuName
  }
}

const handleSubmit = async () => {
  if (formDetails.value.length === 0) {
    ElMessage.warning('请添加入库明细')
    return
  }
  
  const supplier = supplierList.value.find(item => item.id === form.supplierId)
  const data = {
    inType: form.inType,
    supplierId: form.supplierId,
    supplierCode: supplier?.supplierCode,
    supplierName: supplier?.supplierName,
    warehouseCode: 'WH001',
    warehouseName: '主仓库'
  }
  
  try {
    await createStockIn(data, formDetails.value)
    ElMessage.success('创建成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) {
    console.error(e)
  }
}

const handleInspect = (row) => {
  ElMessageBox.confirm('确定要进行验货操作吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await inspectStockIn(row.id, [])
      ElMessage.success('验货成功')
      fetchData()
    } catch (e) {
      console.error(e)
    }
  }).catch(() => {})
}

const handleShelve = (row) => {
  ElMessageBox.confirm('确定要进行上架操作吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await shelveStockIn(row.id, [])
      ElMessage.success('上架成功')
      fetchData()
    } catch (e) {
      console.error(e)
    }
  }).catch(() => {})
}

const handleCancel = (row) => {
  ElMessageBox.confirm('确定要取消该入库单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await cancelStockIn(row.id)
      ElMessage.success('取消成功')
      fetchData()
    } catch (e) {
      console.error(e)
    }
  }).catch(() => {})
}

onMounted(() => {
  fetchData()
  fetchSupplierList()
  fetchSkuList()
})
</script>
