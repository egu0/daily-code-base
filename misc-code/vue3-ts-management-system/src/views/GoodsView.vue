<template>
  <div class="goods-main">
    <div>
      <el-form :inline="true" :model="selectData">
        <el-form-item label="商品名称">
          <el-input v-model="selectData.title" clearable />
        </el-form-item>
        <el-form-item label="商品详情">
          <el-input v-model="selectData.detail" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">搜索</el-button>
        </el-form-item>
      </el-form>
    </div>
    <el-table :data="list" style="width: 100%" :border="true">
      <el-table-column align="center" prop="id" label="ID" width="50"></el-table-column>
      <el-table-column align="center" prop="title" label="名称" width="120"></el-table-column>
      <el-table-column prop="detail" label="详情"></el-table-column>
    </el-table>
    <el-pagination
      layout="prev, pager, next"
      :page-size="selectData.pageSize"
      :total="selectData.count"
      @current-change="handlePageChange"
      @size-change="handleSizeChange"
    ></el-pagination>
  </div>
</template>

<script lang="ts">
import { getGoodsList } from '@/request/api'
import { InitData } from '@/type/goods'
import { defineComponent, onMounted, reactive, toRefs } from 'vue'

export default defineComponent({
  setup() {
    let data = reactive(new InitData())

    function reloadData() {
      getGoodsList(data.selectData).then((res) => {
        data.list = res.data.result
        data.selectData.count = res.data.total
      })
    }
    onMounted(() => {
      reloadData()
    })

    function handlePageChange(page: number) {
      data.selectData.page = page
      reloadData()
    }
    function handleSizeChange(pageSize: number) {
      data.selectData.pageSize = pageSize
      reloadData()
    }

    function handleSubmit() {
      reloadData()
    }

    return { ...toRefs(data), handleSubmit, handlePageChange, handleSizeChange }
  }
})
</script>

<style lang="scss" scoped>
.goods-main {
  max-height: calc(100vh - 80px - 40px);
}
</style>
