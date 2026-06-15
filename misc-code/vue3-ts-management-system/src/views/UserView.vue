<template>
  <div class="user-main">
    <div>
      <el-form :inline="true" :model="selectData">
        <el-form-item label="昵称">
          <el-input v-model="selectData.nickName" clearable />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="selectData.roleId" size="large" style="width: 240px">
            <!-- 搜索时，如果 roleId=0 则去掉这个条件 -->
            <el-option label="请选择" :value="0" />
            <el-option
              v-for="item in roleList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">搜索</el-button>
        </el-form-item>
      </el-form>
    </div>
    <el-table :data="list" style="width: 100%" :border="true">
      <el-table-column align="center" prop="id" label="ID" width="50"></el-table-column>
      <el-table-column align="center" prop="nickName" label="昵称" width="150"></el-table-column>
      <el-table-column prop="role" label="角色">
        <template #default="scope">
          <div class="roles-container">
            <el-tag v-for="(item, index) in scope.row.roles" :key="index">{{ item.name }}</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="role" label="操作" width="300">
        <template #default="scope">
          <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
  <el-dialog v-model="showEditDialog" title="修改用户信息" width="500">
    <el-form :model="editingData">
      <el-form-item label="昵称">
        <el-input v-model="editingData.nickName" autocomplete="off" />
      </el-form-item>
      <el-form-item label="角色">
        <el-select multiple v-model="editingData.roles">
          <el-option v-for="item in roleList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancelUpdate">取消</el-button>
        <el-button type="primary" @click="handleSubmitUpdate"> 更新</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts">
import { getRoleList, getUserList, updateUser } from '@/request/api'
import { UserViewInitData } from '@/type/user'
import { defineComponent, onMounted, reactive, toRefs } from 'vue'

export default defineComponent({
  setup() {
    const data = reactive(new UserViewInitData())

    const fetchRoleList = () => {
      getRoleList().then((res) => {
        data.roleList = res.data.result
      })
    }
    const fetchUserList = () => {
      getUserList(data.selectData).then((res) => {
        data.list = res.data.result
      })
    }
    onMounted(() => {
      fetchUserList()
      fetchRoleList()
    })

    const handleSubmit = () => {
      fetchUserList()
    }

    const handleEdit = (row: any) => {
      data.editingData = {
        id: row.id,
        nickName: row.nickName,
        userName: row.userName,
        roles: row.roles.map((role: any) => role.id)
      }
      data.showEditDialog = true
    }

    const handleCancelUpdate = () => {
      cleanEditingData()
      data.showEditDialog = false
    }
    const handleSubmitUpdate = () => {
      updateUser(data.editingData).then(() => {
        fetchUserList()
        cleanEditingData()
        data.showEditDialog = false
      })
    }
    const cleanEditingData = () => {
      data.editingData = {
        id: 0,
        nickName: '',
        userName: '',
        roles: []
      }
    }

    return { ...toRefs(data), handleSubmit, handleEdit, handleCancelUpdate, handleSubmitUpdate }
  }
})
</script>

<style lang="scss" scoped>
.user-main {
  max-height: calc(100vh - 80px - 40px);
  .roles-container {
    .el-tag {
      margin-right: 5px;
    }
  }
}
</style>
