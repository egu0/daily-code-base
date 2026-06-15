<template>
  <div>
    <el-collapse v-model="activeNames">
      <el-collapse-item title="参考" name="1">
        <div>
          <ul>
            <li>
              1. 视频教程：
              <el-link
                type="primary"
                target="_blank"
                href="https://www.bilibili.com/video/BV1nr4y1G73d"
                >一行行代码带你敲，Vue3+TS 电商后台系统项目实战（2022最新版）</el-link
              >
            </li>
            <li>
              2. 俺的代码：<el-link
                type="primary"
                target="_blank"
                href="https://gitee.com/egu0/vue3-ts-demo"
                >gitee 仓库</el-link
              >
            </li>
          </ul>
        </div>
      </el-collapse-item>
      <el-collapse-item title="技术栈" name="2">
        <ul>
          <li>1. TypeScript</li>
          <li>2. Vue3</li>
          <li>3. Element-Plus</li>
          <li>4. Axios</li>
          <li>5. 等</li>
        </ul>
      </el-collapse-item>
      <el-collapse-item title="俺代码中与教程不同的部分" name="3">
        <ul>
          <li>1. 把数据处理逻辑放在后台，比如分页查找商品、修改用户信息</li>
        </ul>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue'

export default defineComponent({
  setup() {
    const activeNames = ref(['1'])
    return { activeNames }
  }
})
</script>

<style lang="scss" scoped></style>
