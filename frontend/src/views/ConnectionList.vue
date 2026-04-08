<template>
  <el-card class="box-card" style="min-height: calc(100vh - 100px)">
    <el-button type="primary" @click="showAddDialog">添加数据源</el-button>

    <el-table :data="list" border style="margin-top: 20px">
      <el-table-column prop="name" label="名称" width="200" />
      <el-table-column prop="environment" label="环境" width="100">
        <template #default="{ row }">
          <el-tag :type="row.environment === 'production' ? 'danger' : 'primary'">
            {{ row.environment === 'production' ? '生产' : row.environment === 'testing' ? '测试' : '开发' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="dbType" label="类型" width="100" />
      <el-table-column prop="host" label="主机" />
      <el-table-column prop="port" label="端口" width="80" />
      <el-table-column prop="databaseName" label="数据库名" width="150" />
      <el-table-column prop="username" label="用户名" width="150" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="editConnection(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDeleteConnection(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑数据源' : '添加数据源'"
      width="500px"
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="连接名称" required>
          <el-input v-model="form.name" placeholder="给连接起个名字" />
        </el-form-item>
        <el-form-item label="环境">
          <el-select v-model="form.environment" placeholder="选择环境">
            <el-option label="生产" value="production" />
            <el-option label="测试" value="testing" />
            <el-option label="开发" value="development" />
          </el-select>
        </el-form-item>
        <el-form-item label="数据库类型">
          <el-select v-model="form.dbType" placeholder="选择类型">
            <el-option label="MySQL" value="mysql" />
            <el-option label="PostgreSQL" value="postgresql" />
          </el-select>
        </el-form-item>
        <el-form-item label="主机地址">
          <el-input v-model="form.host" placeholder="127.0.0.1" />
        </el-form-item>
        <el-form-item label="端口">
          <el-input v-model.number="form.port" placeholder="3306" />
        </el-form-item>
        <el-form-item label="数据库名">
          <el-input v-model="form.databaseName" placeholder="数据库名称" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="loading">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listConnections, saveConnection, deleteConnection } from '../api/connection'

const list = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const loading = ref(false)

const form = ref({
  name: '',
  environment: 'testing',
  dbType: 'mysql',
  host: 'localhost',
  port: 3306,
  databaseName: '',
  username: '',
  password: ''
})

const loadList = async () => {
  list.value = await listConnections()
}

onMounted(() => {
  loadList()
})

const showAddDialog = () => {
  form.value = {
    name: '',
    environment: 'testing',
    dbType: 'mysql',
    host: 'localhost',
    port: 3306,
    databaseName: '',
    username: '',
    password: ''
  }
  isEdit.value = false
  dialogVisible.value = true
}

const editConnection = (row) => {
  form.value = { ...row }
  isEdit.value = true
  dialogVisible.value = true
}

const submitForm = async () => {
  loading.value = true
  try {
    await saveConnection(form.value)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadList()
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    loading.value = false
  }
}

const handleDeleteConnection = async (row) => {
  await ElMessageBox.confirm(`确认删除数据源 "${row.name}"？`, '确认删除', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning'
  })
  await deleteConnection(row.id)
  ElMessage.success('删除成功')
  loadList()
}
</script>

<style scoped>
</style>
