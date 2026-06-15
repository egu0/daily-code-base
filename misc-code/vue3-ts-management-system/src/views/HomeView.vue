<script lang="ts">
import { defineComponent } from 'vue'
import { useRouter } from 'vue-router'

export default defineComponent({
  setup() {
    const router = useRouter()
    var routes = router.getRoutes().filter((r) => r.meta.showInAside)
    const currentRoutePath = router.currentRoute.value.path
    // const currentRoutePath = useRoute().path // 也可以获取当前路径
    const handleLogout = () => {
      ElMessageBox.confirm('确定退出登录吗', '注意', {
        confirmButtonText: '确定',
        cancelButtonText: '点错了',
        type: 'warning'
      })
        .then(() => {
          localStorage.removeItem('token')
          ElMessage({
            type: 'success',
            message: `退出成功`
          })
          setTimeout(() => {
            router.push('/login')
          }, 600)
        })
        .catch(() => {})
    }
    return { routes, currentRoutePath, handleLogout }
  }
})
</script>

<template>
  <div>
    <el-container>
      <el-header>
        <el-row :gutter="20">
          <el-col class="logo-box" :span="4"><img class="logo" src="../assets/logo.svg" /></el-col>
          <el-col :span="16"><h2>后台管理系统</h2></el-col>
          <el-col :span="4" class="logout-box"
            ><el-link href="javascript:;" class="logout" @click="handleLogout"
              >退出登录</el-link
            ></el-col
          >
        </el-row>
      </el-header>
      <el-container>
        <el-aside>
          <el-menu
            :default-active="currentRoutePath"
            text-color="#fff"
            active-text-color="#ffd04b"
            background-color="#535b61"
            router
          >
            <!-- router 属性：开启路由模式 -->
            <el-menu-item :index="route.path" v-for="route in routes" :key="route.path">
              <span>{{ route.meta.title }}</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style lang="scss" scoped>
.el-header {
  height: 80px;
  background-color: #666;
  color: #fff;
  .logo-box {
    position: relative;
    .logo {
      top: 15px;
      left: 15px;
      height: 50px;
      position: absolute;
    }
  }
  .logout-box {
    text-align: right;
    .logout {
      color: #fff;
      margin-right: 10px;
      line-height: 80px;
    }
  }
  h2 {
    text-align: center;
    line-height: 80px;
  }
}
.el-aside {
  width: 240px;
  .el-menu {
    height: calc(100vh - 80px);
  }
}
</style>
