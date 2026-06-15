<template>
  <div class="authority-main">
    <div>
      <el-form>
        <el-form-item label="角色名">
          <el-input v-model="target.name" clearable />
        </el-form-item>
        <el-tree
          ref="treeRef"
          style="max-width: 600px"
          :data="list"
          :props="defaultProps"
          show-checkbox
          node-key="id"
          :default-checked-keys="target.authority"
          :check-strickly="true"
        />
        <el-form-item>
          <el-button type="primary" @click="handleUpdate">修改</el-button>
          <el-button @click="handleGoback">返回角色页</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts">
import { getAuthorityList, updateRole as updateRoleAuthority } from '@/request/api'
import { AuthorityViewInitData } from '@/type/authority'
import { defineComponent, onMounted, reactive, toRefs } from 'vue'
import { useRoute, useRouter } from 'vue-router'

export default defineComponent({
  setup() {
    const defaultProps = {
      children: 'list',
      label: 'name'
    }
    const route = useRoute()
    const router = useRouter()
    const data = reactive(new AuthorityViewInitData())
    onMounted(() => {
      let roleId = route.query.id
      if (!roleId) {
        router.push('/role')
      }
      data.target.id = Number.parseInt(roleId as string)
      fetchAllData()
    })

    const fetchAllData = () => {
      let roleId = data.target.id + ''
      //获取当前角色的权限
      getAuthorityList(roleId as string).then((res) => {
        data.target.authority = res.data.result
        data.target.name = res.data.name
      })
      //获取所有权限信息
      getAuthorityList('').then((res) => {
        data.list = res.data.result
      })
    }

    const handleUpdate = () => {
      data.target.authority = data.treeRef.getCheckedKeys().sort((a: number, b: number) => {
        return a - b
      })
      updateRoleAuthority(data.target).then(() => {
        fetchAllData()
        ElMessage({
          type: 'success',
          message: `操作成功`
        })
      })
    }

    const handleGoback = () => {
      router.go(-1)
    }

    return { ...toRefs(data), defaultProps, handleUpdate, handleGoback }
  }
})
</script>

<style lang="scss" scoped>
.authority-main {
  max-height: calc(100vh - 80px - 40px);
  .el-form {
    width: 400px;
    .el-form-item {
      margin-top: 18px;
    }
  }
}
</style>
