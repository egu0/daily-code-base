import type { SelectGoodsDataInterface } from '@/type/goods'
import service from '.'
import type { EditingUserInterface, RoleInterface, SelectUserDataInterface } from '@/type/user'

interface LoginData {
  username: string
  password: string
}

/**
 * 登录
 * @param data
 * @returns
 */
export function login(data: LoginData) {
  return service({
    url: '/login',
    method: 'post',
    data
  })
}

/**
 * 获取商品信息
 * @returns
 */
export function getGoodsList(selectData: SelectGoodsDataInterface) {
  return service({
    url:
      `/getGoodsList?page=${selectData.page}&pageSize=${selectData.pageSize}` +
      `&title=${selectData.title}&detail=${selectData.detail}`,
    method: 'get'
  })
}

/**
 * 获取用户列表
 * @returns
 */
export function getUserList(selectData: SelectUserDataInterface) {
  if (selectData && selectData.roleId === 0) {
    //如果没有选择角色则忽略
    return service({
      url: `/getUserList?nickName=${selectData.nickName}`,
      method: 'get'
    })
  } else {
    return service({
      url: `/getUserList?nickName=${selectData.nickName}&roleId=${selectData.roleId}`,
      method: 'get'
    })
  }
}

/**
 * 更新用户信息
 * @param data
 * @returns
 */
export function updateUser(data: EditingUserInterface) {
  return service({
    url: '/updateUser',
    method: 'post',
    data
  })
}

/**
 * 获取角色列表
 * @returns
 */
export function getRoleList() {
  return service({
    url: '/getRoleList',
    method: 'get'
  })
}

/**
 * 添加新角色
 * @param roleName
 * @returns
 */
export function addRole(roleName: string) {
  return service({
    url: '/addRole',
    method: 'post',
    data: {
      name: roleName
    }
  })
}

/**
 * （根据角色 id ）获取权限列表
 */
export function getAuthorityList(roleId: string) {
  if (roleId && roleId.length > 0) {
    return service({
      url: `/getAuthorityList?id=${roleId}`,
      method: 'get'
    })
  } else {
    return service({
      url: `/getAuthorityList`,
      method: 'get'
    })
  }
}

/**
 * 修改角色
 */
export function updateRole(data: RoleInterface) {
  return service({
    url: '/updateRole',
    method: 'post',
    data
  })
}
