<template>
  <div class="page-container">
    <div class="page-header">
      <span class="page-title">货位管理</span>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增
      </el-button>
    </div>

    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="货位编码">
        <el-input v-model="searchForm.locationCode" placeholder="请输入货位编码" clearable />
      </el-form-item>
      <el-form-item label="货位名称">
        <el-input v-model="searchForm.locationName" placeholder="请输入货位名称" clearable />
      </el-form-item>
      <el-form-item label="区域">
        <el-input v-model="searchForm.area" placeholder="请输入区域" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
          <el-option label="禁用" :value="0" />
          <el-option label="启用" :value="1" />
          <el-option label="占用" :value="2" />
          <el-option label="空闲" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="locationCode" label="货位编码" width="120" />
      <el-table-column prop="locationName" label="货位名称" width="150" />
      <el-table-column prop="warehouseName" label="仓库" width="120" />
      <el-table-column prop="area" label="区域" width="80" />
      <el-table-column prop="row" label="排" width="60" />
      <el-table-column prop="column" label="列" width="60" />
      <el-table-column prop="layer" label="层" width="60" />
      <el-table-column prop="maxCapacity" label="最大容量" width="100" />
      <el-table-column prop="usedCapacity" label="已用容量" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleViewCache(row)">查看缓存</el-button>
          <el-button type="warning" link @click="handleClearCache(row)">清除缓存</el-button>
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="货位编码">
              <el-input v-model="form.locationCode" placeholder="请输入货位编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="货位名称">
              <el-input v-model="form.locationName" placeholder="请输入货位名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="仓库编码">
              <el-input v-model="form.warehouseCode" placeholder="请输入仓库编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="仓库名称">
              <el-input v-model="form.warehouseName" placeholder="请输入仓库名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="区域">
              <el-input v-model="form.area" placeholder="区域" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="排">
              <el-input v-model="form.row" placeholder="排" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="列">
              <el-input v-model="form.column" placeholder="列" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="层">
              <el-input v-model="form.layer" placeholder="层" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="最大容量">
              <el-input v-model="form.maxCapacity" type="number" placeholder="请输入最大容量" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="禁用" :value="0" />
                <el-option label="启用" :value="1" />
                <el-option label="占用" :value="2" />
                <el-option label="空闲" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="cacheDialogVisible" title="货位缓存信息" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="货位编码">{{ cacheData.locationCode }}</el-descriptions-item>
        <el-descriptions-item label="货位名称">{{ cacheData.locationName }}</el-descriptions-item>
        <el-descriptions-item label="仓库">{{ cacheData.warehouseName }}</el-descriptions-item>
        <el-descriptions-item label="区域-排-列-层">
          {{ cacheData.area }}-{{ cacheData.row }}-{{ cacheData.column }}-{{ cacheData.layer }}
        </el-descriptions-item>
        <el-descriptions-item label="容量">
          已用: {{ cacheData.usedCapacity }} / 最大: {{ cacheData.maxCapacity }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">{{ getStatusText(cacheData.status) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getLocationList, getLocationById, getLocationByCode, addLocation, updateLocation, deleteLocation, clearCache } from '@/api/location'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const cacheDialogVisible = ref(false)
const dialogTitle = ref('新增货位')
const cacheData = ref({})

const statusMap = {
  0: { text: '禁用', type: 'danger' },
  1: { text: '启用', type: 'success' },
  2: { text: '占用', type: 'warning' },
  3: { text: '空闲', type: 'info' }
}

const getStatusText = (status) => statusMap[status]?.text || '未知'
const getStatusType = (status) => statusMap[status]?.type || ''

const searchForm = reactive({
  locationCode: '',
  locationName: '',
  area: '',
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null,
  locationCode: '',
  locationName: '',
  warehouseCode: '',
  warehouseName: '',
  area: '',
  row: '',
  column: '',
  layer: '',
  maxCapacity: 0,
  usedCapacity: 0,
  status: 1
})

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const res = await getLocationList(params)
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
  searchForm.locationCode = ''
  searchForm.locationName = ''
  searchForm.area = ''
  searchForm.status = null
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增货位'
  form.id = null
  form.locationCode = ''
  form.locationName = ''
  form.warehouseCode = ''
  form.warehouseName = ''
  form.area = ''
  form.row = ''
  form.column = ''
  form.layer = ''
  form.maxCapacity = 0
  form.usedCapacity = 0
  form.status = 1
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑货位'
  try {
    const res = await getLocationById(row.id)
    Object.assign(form, res.data)
    dialogVisible.value = true
  } catch (e) {
    ElMessage.error('获取数据失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该货位吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteLocation(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (e) {
      console.error(e)
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  try {
    if (form.id) {
      await updateLocation(form)
      ElMessage.success('编辑成功')
    } else {
      await addLocation(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (e) {
    console.error(e)
  }
}

const handleViewCache = async (row) => {
  try {
    const res = await getLocationByCode(row.locationCode)
    cacheData.value = res.data || {}
    cacheDialogVisible.value = true
  } catch (e) {
    ElMessage.error('获取缓存数据失败')
  }
}

const handleClearCache = (row) => {
  ElMessageBox.confirm('确定要清除该货位的缓存吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await clearCache(row.locationCode)
      ElMessage.success('缓存已清除')
    } catch (e) {
      console.error(e)
    }
  }).catch(() => {})
}

onMounted(() => {
  fetchData()
})
</script>
