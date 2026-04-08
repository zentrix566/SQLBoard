<template>
  <div>
    <el-card class="box-card" style="min-height: calc(100vh - 100px)">
      <template #header>
        <div class="card-header">
          <span>SQL 执行</span>
        </div>
      </template>

      <el-form :model="form" label-width="120px">
        <el-form-item label="数据源">
          <el-select v-model="form.connectionId" placeholder="请选择数据源" style="width: 100%">
            <el-option
              v-for="conn in connections"
              :key="conn.id"
              :label="`${conn.name} (${conn.environment})`"
              :value="conn.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="SQL语句">
          <el-input
            v-model="form.sql"
            type="textarea"
            :rows="10"
            placeholder="输入SQL语句..."
          />
        </el-form-item>

        <el-form-item label="需要脱敏">
          <el-switch v-model="form.needDesensitization" />
        </el-form-item>

        <template v-if="form.needDesensitization">
          <el-form-item label="脱敏配置">
            <div v-for="(item, index) in desensitizationConfig" :key="index" class="desensitize-row">
              <el-input v-model="item.column" placeholder="列名" style="width: 200px; margin-right: 10px" />
              <el-select v-model="item.type" placeholder="脱敏类型" style="width: 150px; margin-right: 10px">
                <el-option label="不脱敏" value="NONE" />
                <el-option label="姓名" value="CHINESE_NAME" />
                <el-option label="身份证" value="ID_CARD" />
                <el-option label="手机号" value="PHONE" />
                <el-option label="邮箱" value="EMAIL" />
                <el-option label="银行卡" value="BANK_CARD" />
                <el-option label="地址" value="ADDRESS" />
                <el-option label="完全隐藏" value="FULL" />
              </el-select>
              <el-button type="danger" @click="removeDesensitizeConfig(index)">删除</el-button>
            </div>
            <el-button type="primary" @click="addDesensitizeConfig">添加配置</el-button>
          </el-form-item>
        </template>

        <el-form-item>
          <el-button type="primary" @click="executeQuery" :loading="loading">查询</el-button>
          <el-button type="warning" @click="executeUpdate" :loading="loading">执行更新</el-button>
          <el-button type="success" @click="exportData" :loading="loading">导出</el-button>
          <el-button @click="saveAsOperation">保存为常用操作</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-if="result && result.success" class="box-card" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>执行结果 ({{result.affectedRows}} 行, {{result.executionTime}}ms)</span>
        </div>
      </template>

      <el-table
        v-if="result.data && result.data.length > 0"
        :data="result.data"
        border
        max-height="500"
      >
        <el-table-column
          v-for="column in result.columns"
          :key="column"
          :prop="column"
          :label="column"
        />
      </el-table>

      <div v-else-if="result.affectedRows !== undefined" class="result-message">
        {{ result.message }}
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { executeSql, exportSql } from '../api/sql'
import { listConnections } from '../api/connection'
import { saveOperation } from '../api/operation'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const connections = ref([])
const result = ref(null)

const form = ref({
  connectionId: '',
  sql: '',
  needDesensitization: false
})

const desensitizationConfig = ref([
  { column: '', type: 'NONE' }
])

onMounted(async () => {
  const res = await listConnections()
  connections.value = res

  // 从路由参数获取（从常用操作点击"使用"进来）
  if (route.query.connectionId) {
    form.value.connectionId = parseInt(route.query.connectionId)
  } else if (connections.value.length > 0) {
    // 默认选中第一个数据源
    form.value.connectionId = connections.value[0].id
  }

  if (route.query.sql) {
    form.value.sql = route.query.sql
  }
})

const addDesensitizeConfig = () => {
  desensitizationConfig.value.push({ column: '', type: 'NONE' })
}

const removeDesensitizeConfig = (index) => {
  desensitizationConfig.value.splice(index, 1)
}

const executeQuery = async () => {
  await execute('query')
}

const executeUpdate = async () => {
  const sql = form.value.sql.trim().toLowerCase()

  // 检测危险操作
  const dangerousKeywords = [
    'drop', 'truncate', 'alter', 'rename',
    'delete.*from', 'update.*set'
  ]
  const isDangerous = dangerousKeywords.some(keyword => {
    const regex = new RegExp('\\b' + keyword + '\\b', 'i')
    return regex.test(sql)
  })

  let message = '确认执行该更新操作？此操作会修改数据库数据'
  let title = '确认执行'
  let type = 'warning'

  // 对于高危操作，增加更强警告
  if (/(drop|truncate)/i.test(sql)) {
    message = '\n⚠️  **检测到高危操作 DROP/TRUNCATE** ⚠️\n\n此操作会删除整张表/整个数据库的数据，且无法恢复！\n\n确认要继续执行吗？'
    title = '!!! 高危操作确认 !!!'
    type = 'error'
  } else if (/delete from/i.test(sql) && !sql.includes('where')) {
    message = '\n⚠️  **检测到全表DELETE** ⚠️\n\n你正在删除整张表的所有数据，且无法恢复！\n\n确认要继续执行吗？'
    title = '!!! 高危操作确认 !!!'
    type = 'error'
  }

  await ElMessageBox.confirm(message, title, {
    confirmButtonText: '确定执行',
    cancelButtonText: '取消',
    type: type
  })
  await execute('update')
}

const execute = async () => {
  if (!form.value.connectionId) {
    ElMessage.warning('请选择数据源')
    return
  }
  if (!form.value.sql.trim()) {
    ElMessage.warning('请输入SQL语句')
    return
  }

  loading.value = true
  result.value = null

  try {
    // Convert array config to object format for backend
    let configObj = null
    if (form.value.needDesensitization && desensitizationConfig.value.length > 0) {
      configObj = {}
      desensitizationConfig.value.forEach(item => {
        if (item.column && item.column.trim()) {
          configObj[item.column] = item.type
        }
      })
    }
    const params = {
      ...form.value,
      desensitizationConfig: configObj
    }
    const res = await executeSql(params)
    result.value = res
    if (res.success) {
      ElMessage.success(res.message)
    } else {
      ElMessage.error(res.message)
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('执行失败')
  } finally {
    loading.value = false
  }
}

const exportData = async () => {
  if (!form.value.connectionId) {
    ElMessage.warning('请选择数据源')
    return
  }
  if (!form.value.sql.trim()) {
    ElMessage.warning('请输入SQL语句')
    return
  }

  loading.value = true
  try {
    // Convert array config to object format for backend
    let configObj = null
    if (form.value.needDesensitization && desensitizationConfig.value.length > 0) {
      configObj = {}
      desensitizationConfig.value.forEach(item => {
        if (item.column && item.column.trim()) {
          configObj[item.column] = item.type
        }
      })
    }
    const params = {
      ...form.value,
      desensitizationConfig: configObj,
      format: 'excel',
      fileName: 'export'
    }
    const blob = await exportSql(params)

    // 创建下载链接
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'export.xlsx'
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    window.URL.revokeObjectURL(url)

    ElMessage.success('导出成功')
  } catch (e) {
    console.error(e)
    ElMessage.error('导出失败')
  } finally {
    loading.value = false
  }
}

const saveAsOperation = async () => {
  if (!form.value.connectionId || !form.value.sql) {
    ElMessage.warning('请先选择数据源并输入SQL')
    return
  }

  const { value } = await ElMessageBox.prompt('请输入操作名称', '保存为常用操作', {
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  })

  if (value) {
    const sqlType = form.value.sql.trim().toLowerCase().startsWith('select') ? 'query' : 'update'
    // Convert array config to object format for backend
    let configObj = null
    if (form.value.needDesensitization && desensitizationConfig.value.length > 0) {
      configObj = {}
      desensitizationConfig.value.forEach(item => {
        if (item.column && item.column.trim()) {
          configObj[item.column] = item.type
        }
      })
    }
    const data = {
      name: value,
      connectionId: form.value.connectionId,
      sqlContent: form.value.sql,
      operationType: sqlType,
      desensitizationConfig: form.value.needDesensitization ? JSON.stringify(configObj) : null
    }
    await saveOperation(data)
    ElMessage.success('保存成功')
    router.push('/saved-operations')
  }
}
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
.desensitize-row {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
.result-message {
  font-size: 16px;
  color: #606266;
  padding: 20px 0;
}
</style>
