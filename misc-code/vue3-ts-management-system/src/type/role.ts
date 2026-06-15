export interface RoleInterface {
  id: number
  name: string
  authority: number[]
}

export class RoleViewInitData {
  list: RoleInterface[] = []
}
