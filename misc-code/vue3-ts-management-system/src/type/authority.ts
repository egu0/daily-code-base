import type { RoleInterface } from './role'

export class AuthorityViewInitData {
  //修改中的数据
  target: RoleInterface = {
    id: 0,
    name: '',
    authority: []
  }

  list: AuthorityInterface[] = []
  // tree ref
  treeRef: any
}

export interface AuthorityInterface {
  id: number
  name: string
  list?: AuthorityInterface[]
}
