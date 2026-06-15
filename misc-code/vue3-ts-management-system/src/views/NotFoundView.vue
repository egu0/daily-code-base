<template>
  <div class="box">
    <h2>Not Found</h2>
    <el-link type="primary" @click="goback" class="go-back">返回首页</el-link>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import { useRouter } from 'vue-router'

export default defineComponent({
  setup() {
    const router = useRouter()
    const goback = () => {
      router.push('/')
    }
    return { goback }
  }
})
</script>

<style lang="scss" scoped>
.box {
  padding: 15px;
  .go-back {
    margin-top: 10px;
  }
}
</style>
