<template>
  <div class="page-container">
    <div class="page-header">
      <span class="page-title">出库管理</span>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增出库单
      </el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="outNo" label="出库单号" width="200" />
      <el-table-column prop="outType" label="出库类型" width="120">
        <template #default="{ row }">
          <el-tag :type="row.outType === 'SALE' ? 'primary' : 'warning'">
            {{ row.outType === 'SALE' ? '销售出库' : '退货出库' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="customerName" label="客户名称" width="150" />
      <el-table-column prop="customerPhone" label="客户电话" width="130" />
      <el-table-column prop="totalQty" label="总数量" width="100" />
      <el-table-column prop="pickedQty" label="已拣货" width="100" />
      <el-table-column prop="checkedQty" label="已复核" width="100" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="300" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link :disabled="row.status !== 0 && row.status !== 1" @click="handlePick(row)">拣货</el-button>
          <el-button type="success" link :disabled="row.status !== 2 && row.status !== 3" @click="handleCheck(row)">复核</el-button>
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

    <el-dialog v-model="dialogVisible" title="新增出库单" width="800px">
      <el-form :model="form" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出库类型">
              <el-select v-model="form.outType" placeholder="请选择出库类型" style="width: 100%">
                <el-option label="销售出库" value="SALE" />
                <el-option label="退货出库" value="RETURN_TO_SUPPLIER" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户名称">
              <el-input v-model="form.customerName" placeholder="请输入客户名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客户电话">
              <el-input v-model="form.customerPhone" placeholder="请输入客户电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户地址">
              <el-input v-model="form.customerAddress" placeholder="请输入客户地址" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="出库明细">
          <el-table :data="formDetails" border size="small">
            <el-table-column label="SKU选择" width="200">
              <template #default="{ row, $index }">
                <el-select v-model="row.skuId" placeholder="选择SKU" @change="(id) => handleSkuChange(id, $index)" style="width: 100%">
                  <el-option
                    v-for="item in skuList"
                    :key="item.id"
                    :label="item.skuName + ' (' + item.skuCode + ')'"
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
import { getStockOutList, createStockOut, pickStockOut, checkStockOut, cancelStockOut } from '@/api/stockOut'
import { getSkuListAll } from '@/api/sku'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const skuList = ref([])

const statusMap = {
  0: { text: '待拣货', type: 'info' },
  1: { text: '拣货中', type: 'warning' },
  2: { text: '待复核', type: '' },
  3: { text: '复核中', type: 'primary' },
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
  outType: 'SALE',
  customerName: '',
  customerPhone: '',
  customerAddress: ''
})

const formDetails = ref([])

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await getStockOutList(params)
    if (res && res.code === 200 && res.data) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    } else {
      tableData.value = []
      pagination.total = 0
    }
  } catch (e) {
    console.error('获取出库单列表失败:', e)
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
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
  form.outType = 'SALE'
  form.customerName = ''
  form.customerPhone = ''
  form.customerAddress = ''
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
    ElMessage.warning('请添加出库明细')
    return
  }
  
  const data = {
    outType: form.outType,
    customerName: form.customerName,
    customerPhone: form.customerPhone,
    customerAddress: form.customerAddress,
    warehouseCode: 'WH001',
    warehouseName: '主仓库'
  }
  
  try {
    const res = await createStockOut(data, formDetails.value)
    if (res && res.code === 200) {
      ElMessage.success('创建成功')
      dialogVisible.value = false
      fetchData()
    } else {
      ElMessage.error(res?.message || '创建失败')
    }
  } catch (e) {
    ElMessage.error(e.message || '创建失败')
  }
}

const handlePick = (row) => {
  ElMessageBox.confirm('确定要进行拣货操作吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await pickStockOut(row.id, [])
      if (res && res.code === 200) {
        ElMessage.success('拣货成功')
        fetchData()
      } else {
        ElMessage.error(res?.message || '拣货失败')
      }
    } catch (e) {
      ElMessage.error(e.message || '拣货失败')
    }
  }).catch(() => {})
}

const handleCheck = (row) => {
  ElMessageBox.confirm('确定要进行复核操作吗？复核通过后将自动扣减库存。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await checkStockOut(row.id, [])
      if (res && res.code === 200) {
        ElMessage.success('复核成功，库存已扣减')
        fetchData()
      } else {
        ElMessage.error(res?.message || '复核失败')
      }
    } catch (e) {
      ElMessage.error(e.message || '复核失败')
    }
  }).catch(() => {})
}

const handleCancel = (row) => {
  ElMessageBox.confirm('确定要取消该出库单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await cancelStockOut(row.id)
      if (res && res.code === 200) {
        ElMessage.success('取消成功')
        fetchData()
      } else {
        ElMessage.error(res?.message || '取消失败')
      }
    } catch (e) {
      ElMessage.error(e.message || '取消失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  fetchData()
  fetchSkuList()
})
</script>
