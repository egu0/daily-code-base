export interface UserInterface {
  id: number
  nickName: string
  userName: string
  roles: UserRoleInterface[]
}

export interface UserRoleInterface {
  id: number
  name: string
}

export interface SelectUserDataInterface {
  roleId: number
  nickName: string
}

export class UserViewInitData {
  // 搜索表单
  selectData: SelectUserDataInterface = {
    roleId: 0,
    nickName: ''
  }
  // 查询到的用户信息
  list: UserInterface[] = []
  // 展示在搜索区的角色列表
  roleList: RoleInterface[] = []
  // 展示编辑对话框
  showEditDialog: boolean = false
  // 正在编辑的数据
  editingData: EditingUserInterface = {
    id: 0,
    nickName: '',
    userName: '',
    roles: []
  }
}

export interface EditingUserInterface {
  id: number
  nickName: string
  userName: string
  roles: number[]
}

export interface RoleInterface {
  id: number
  name: string
  authority: number[]
}
