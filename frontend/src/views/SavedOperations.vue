<template>
  <el-card class="box-card" style="min-height: calc(100vh - 100px)">
    <el-form :inline="true" :model="filter">
      <el-form-item label="数据源">
        <el-select v-model="filter.connectionId" placeholder="全部" clearable @change="loadList" style="width: 200px">
          <el-option
            v-for="conn in connections"
            :key="conn.id"
            :label="conn.name"
            :value="conn.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="操作类型">
        <el-select v-model="filter.operationType" placeholder="全部" clearable @change="loadList" style="width: 150px">
          <el-option label="查询" value="query" />
          <el-option label="更新" value="update" />
          <el-option label="删除" value="delete" />
          <el-option label="统计" value="stats" />
        </el-select>
      </el-form-item>
    </el-form>

    <el-table :data="list" border style="margin-top: 10px">
      <el-table-column prop="name" label="操作名称" width="200" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="operationType" label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="row.operationType === 'query' ? 'primary' : 'warning'">
            {{ row.operationType === 'query' ? '查询' : row.operationType === 'update' ? '更新' : row.operationType }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="180">
        <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="useOperation(row)">使用</el-button>
          <el-button type="danger" link @click="handleDeleteOperation(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listConnections } from '../api/connection'
import { listOperations, deleteOperation } from '../api/operation'

const router = useRouter()
const connections = ref([])
const list = ref([])
const filter = ref({
  connectionId: '',
  operationType: ''
})

onMounted(async () => {
  connections.value = await listConnections()
  // 默认选中第一个数据源
  if (connections.value.length > 0) {
    filter.value.connectionId = connections.value[0].id
  }
  loadList()
})

const loadList = async () => {
  if (!filter.value.connectionId) {
    list.value = []
    return
  }
  list.value = await listOperations(filter.value.connectionId)
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

const useOperation = (row) => {
  router.push({
    path: '/sql',
    query: {
      connectionId: row.connectionId,
      sql: row.sqlContent
    }
  })
}

const handleDeleteOperation = async (row) => {
  await ElMessageBox.confirm(`确认删除操作 "${row.name}"？`, '确认删除', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning'
  })
  await deleteOperation(row.id)
  ElMessage.success('删除成功')
  loadList()
}
</script>

<style scoped>
</style>
