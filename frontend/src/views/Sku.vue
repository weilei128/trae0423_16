<template>
  <div class="page-container">
    <div class="page-header">
      <span class="page-title">货品档案管理</span>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增
      </el-button>
    </div>

    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="SKU编码">
        <el-input v-model="searchForm.skuCode" placeholder="请输入SKU编码" clearable />
      </el-form-item>
      <el-form-item label="SKU名称">
        <el-input v-model="searchForm.skuName" placeholder="请输入SKU名称" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="skuCode" label="SKU编码" width="150" />
      <el-table-column prop="skuName" label="SKU名称" width="200" />
      <el-table-column prop="category" label="分类" width="120" />
      <el-table-column prop="unit" label="单位" width="80" />
      <el-table-column prop="specs" label="规格" width="200" show-overflow-tooltip />
      <el-table-column prop="supplierName" label="供应商" width="150" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-show="pagination.total > 0"
      v-model:current-page="pagination.pageNum"
      v-model:page-size="pagination.pageSize"
      :page-sizes="[10, 20, 50, 100]"
      :total="pagination.total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="fetchData"
      @current-change="fetchData"
      style="margin-top: 20px; justify-content: flex-end"
    />

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="SKU编码" prop="skuCode">
              <el-input v-model="form.skuCode" placeholder="请输入SKU编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="SKU名称" prop="skuName">
              <el-input v-model="form.skuName" placeholder="请输入SKU名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="分类">
              <el-input v-model="form.category" placeholder="请输入分类" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位">
              <el-input v-model="form.unit" placeholder="请输入单位" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="规格">
          <el-input v-model="form.specs" type="textarea" :rows="2" placeholder="请输入规格描述" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="供应商">
              <el-select v-model="form.supplierId" placeholder="请选择供应商" style="width: 100%" clearable>
                <el-option
                  v-for="item in supplierList"
                  :key="item.id"
                  :label="item.supplierName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio :label="1">启用</el-radio>
                <el-radio :label="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
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
import { getSkuList, getSkuById, addSku, updateSku, deleteSku } from '@/api/sku'
import { getSupplierListAll } from '@/api/supplier'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增SKU')
const formRef = ref(null)
const supplierList = ref([])

const searchForm = reactive({
  skuCode: '',
  skuName: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null,
  skuCode: '',
  skuName: '',
  category: '',
  unit: '',
  specs: '',
  supplierId: null,
  supplierCode: '',
  supplierName: '',
  status: 1
})

const rules = {
  skuCode: [{ required: true, message: '请输入SKU编码', trigger: 'blur' }],
  skuName: [{ required: true, message: '请输入SKU名称', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const res = await getSkuList(params)
    if (res && res.code === 200 && res.data) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    } else {
      tableData.value = []
      pagination.total = 0
    }
  } catch (e) {
    console.error('获取SKU列表失败:', e)
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const fetchSupplierList = async () => {
  try {
    const res = await getSupplierListAll()
    if (res && res.code === 200 && res.data) {
      supplierList.value = res.data || []
    } else {
      supplierList.value = []
    }
  } catch (e) {
    console.error('获取供应商列表失败:', e)
    supplierList.value = []
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

const handleReset = () => {
  searchForm.skuCode = ''
  searchForm.skuName = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增SKU'
  form.id = null
  form.skuCode = ''
  form.skuName = ''
  form.category = ''
  form.unit = ''
  form.specs = ''
  form.supplierId = null
  form.status = 1
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑SKU'
  try {
    const res = await getSkuById(row.id)
    if (res && res.code === 200 && res.data) {
      Object.assign(form, res.data)
    }
    dialogVisible.value = true
  } catch (e) {
    ElMessage.error('获取数据失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该SKU吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteSku(row.id)
      if (res && res.code === 200) {
        ElMessage.success('删除成功')
        fetchData()
      } else {
        ElMessage.error(res?.message || '删除失败')
      }
    } catch (e) {
      ElMessage.error(e.message || '删除失败')
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (form.id) {
          res = await updateSku(form)
        } else {
          res = await addSku(form)
        }
        if (res && res.code === 200) {
          ElMessage.success(form.id ? '编辑成功' : '新增成功')
          dialogVisible.value = false
          fetchData()
        } else {
          ElMessage.error(res?.message || '操作失败')
        }
      } catch (e) {
        ElMessage.error(e.message || '操作失败')
      }
    }
  })
}

onMounted(() => {
  fetchData()
  fetchSupplierList()
})
</script>
