export interface LoginFormInterface {
  username: string
  password: string
}

export class LoginData {
  ruleForm: LoginFormInterface = {
    username: '',
    password: ''
  }
}
