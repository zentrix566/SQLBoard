<template>
  <div>
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>操作历史</span>
        </div>
      </template>

      <el-form :inline="true" :model="filter" class="demo-form-inline">
        <el-form-item label="数据源ID">
          <el-input v-model="filter.connectionId" placeholder="请输入数据源ID" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="list" border style="margin-top: 10px" max-height="600">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="operator" label="操作人" width="100" />
        <el-table-column prop="operationType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getOperationTypeTag(row.operationType)">
              {{ getOperationTypeName(row.operationType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="connectionId" label="连接ID" width="80" />
        <el-table-column prop="sqlContent" label="SQL" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 'success' ? 'success' : 'danger'">
              {{ row.status === 'success' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="affectedRows" label="行数" width="80" />
        <el-table-column prop="executionTime" label="耗时(ms)" width="80" />
        <el-table-column prop="desensitized" label="脱敏" width="80">
          <template #default="{ row }">
            <el-tag :type="row.desensitized ? 'warning' : 'info'">
              {{ row.desensitized ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" width="180">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column prop="errorMessage" label="错误信息" min-width="150" show-overflow-tooltip />
      </el-table>

      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listHistory } from '../api/history'

const filter = ref({
  connectionId: ''
})

const list = ref([])
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const loadData = async () => {
  const res = await listHistory(
    pagination.value.currentPage,
    pagination.value.pageSize,
    filter.value.connectionId ? parseInt(filter.value.connectionId) : null
  )
  list.value = res.records
  pagination.value.total = res.total
}

const resetFilter = () => {
  filter.value.connectionId = ''
  pagination.value.currentPage = 1
  loadData()
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

const getOperationTypeName = (type) => {
  const map = {
    query: '查询',
    write: '写入',
    import: '导入',
    export: '导出',
    stats: '统计',
    other: '其他'
  }
  return map[type] || type
}

const getOperationTypeTag = (type) => {
  const map = {
    query: 'info',
    write: 'warning',
    import: 'primary',
    export: 'success',
    stats: 'danger',
    other: 'info'
  }
  return map[type] || 'info'
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.box-card {
  margin-top: 0;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
