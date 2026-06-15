<template>
  <div class="role-main">
    <div>
      <el-form>
        <el-form-item>
          <el-button type="primary" @click="handleAddRole">新增</el-button>
        </el-form-item>
      </el-form>
    </div>
    <el-table :data="list" style="width: 100%" :border="true">
      <el-table-column align="center" prop="id" label="ID" width="50"></el-table-column>
      <el-table-column align="center" prop="name" label="角色名" width="150"></el-table-column>
      <el-table-column prop="authority" label="操作">
        <template #default="scope">
          <el-button type="primary" link @click="handleEditRole(scope.row)">修改</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, toRefs, onMounted } from 'vue'
import { addRole, getRoleList } from '@/request/api'
import { RoleViewInitData } from '@/type/role'
import type { RoleInterface } from '@/type/user'
import { useRouter } from 'vue-router'

export default defineComponent({
  setup() {
    const data = reactive(new RoleViewInitData())
    const router = useRouter()

    onMounted(() => {
      fetchRoleList()
    })

    const fetchRoleList = () => {
      getRoleList().then((res) => {
        data.list = res.data.result
      })
    }

    const handleAddRole = () => {
      // VSCode 提示报错，但编译通过，请忽略之
      ElMessageBox.prompt('请输入角色名', '添加新角色', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      })
        .then(({ value }) => {
          if (!value || typeof value !== 'string' || value.trim().length < 1) {
            ElMessage({
              type: 'warning',
              message: `请输入合法的角色名`
            })
            return
          }
          value = value.trim()
          addRole(value).then((res) => {
            ElMessage({
              type: `${res.data.result}`,
              message: res.data.result === 'success' ? `添加新角色成功：${value}` : res.data.msg
            })
            fetchRoleList()
          })
        })
        .catch(() => {})
    }

    const handleEditRole = (role: RoleInterface) => {
      router.push({
        path: 'authority',
        query: {
          id: role.id
        }
      })
    }

    return { ...toRefs(data), handleAddRole, handleEditRole }
  }
})
</script>

<style lang="scss" scoped>
.role-main {
  max-height: calc(100vh - 80px - 40px);
}
</style>
import { ElMessageBox, ElMessage } from 'element-plus'import { ElMessageBox, ElMessage } from
'element-plus'import { ElMessageBox, ElMessage } from 'element-plus'import { ElMessageBox, ElMessage
} from 'element-plus'
