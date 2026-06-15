<template>
  <div class="login-box">
    <el-form
      ref="ruleFormRef"
      :model="ruleForm"
      status-icon
      :rules="rules"
      label-width="auto"
      class="login-form"
    >
      <h2>后台管理系统</h2>
      <el-form-item label="用户名" prop="username">
        <el-input v-model="ruleForm.username" autocomplete="off" />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="ruleForm.password" type="password" autocomplete="off" />
      </el-form-item>
      <el-form-item>
        <el-button class="login-button" type="primary" @click="submitForm(ruleFormRef)">
          登录
        </el-button>
        <el-button class="login-button" @click="resetForm(ruleFormRef)">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, toRefs, ref } from 'vue'
import { LoginData } from '../type/login'
import type { FormInstance } from 'element-plus'
import { login } from '../request/api'
import { useRouter } from 'vue-router'

export default defineComponent({
  setup() {
    let formData = reactive(new LoginData())
    const rules = {
      username: [
        {
          required: true,
          message: '请输入账号',
          trigger: 'blur'
        },
        {
          min: 3,
          max: 5,
          message: '账号的长度在 3 到 10 之间',
          trigger: 'blur'
        }
      ],
      password: [
        {
          required: true,
          message: '请输入密码',
          trigger: 'blur'
        },
        {
          min: 8,
          max: 40,
          message: '密码的长度在 8 到 40 之间',
          trigger: 'blur'
        }
      ]
    }
    const ruleFormRef = ref<FormInstance>()
    const router = useRouter()
    const submitForm = (formEl: FormInstance | undefined) => {
      if (!formEl) return
      formEl.validate((valid) => {
        if (!valid) {
          return
        }
        login(formData.ruleForm).then((res) => {
          localStorage.setItem('token', res.data.token)
          router.push('/')
        })
      })
    }
    const resetForm = (formEl: FormInstance | undefined) => {
      if (!formEl) return
      formEl.resetFields()
    }
    return { ...toRefs(formData), rules, ruleFormRef, submitForm, resetForm }
  }
})
</script>

<style lang="scss" scoped>
.login-box {
  text-align: center;
  width: 100%;
  height: 100%;
  background: url(../assets/bg.jpg) no-repeat;
  position: relative;
  .login-form {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 600px;
    padding: 30px;
    border-radius: 20px;
    background: #fff;
  }
  .login-button {
    width: 49%;
  }
  h2 {
    font-weight: 500;
    margin-bottom: 25px;
  }
}
</style>
