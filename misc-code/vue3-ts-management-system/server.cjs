/* eslint-disable no-undef */
const http = require('http')
const crypto = require('crypto')
const url = require('url')

/*
安装工具：npm install nodemon -g
执行命令：nodemon server.cjs
*/

/**
 * 商品列表
 */
var goodsList = [
  {
    userId: 1,
    id: 1,
    title: '旺仔牛奶',
    detail:
      '旺仔牛奶相信大家都喝过，小时候常常能在电视上看到它的广告，各种广告台词也深入人心，一看到它的IP就总容易想起那句再看，再看我就把你喝掉的广告语。记得小时候买一瓶喝都能高兴上好几天，它的奶香味浓郁，好喝到让人上瘾。其实旺仔牛奶不是真正的牛奶，而是一种复原乳，还被称为再制奶，它是用浓缩乳或者乳粉加入一定比例的水重新做成的一款乳液'
  },
  {
    userId: 1,
    id: 2,
    title: '旺旺碎冰冰',
    detail:
      '这款旺旺碎冰冰实在太棒了太好吃了,旺旺是大品牌质量有保障,品质信得过,孩子非常喜欢吃,各种口味都有,酸甜可口,让人爱不释口,回味无穷液'
  },
  {
    userId: 1,
    id: 3,
    title: '喜之郎',
    detail:
      '喜之郎品牌，创立于1992年，属于广东喜之郎集团有限公司，旗下产品包括喜之郎果冻、美好时光海苔、优乐美奶茶、开心时间饼干等。喜之郎系列产品被评为“中国名牌产品”液'
  },
  {
    userId: 1,
    id: 4,
    title: '德芙',
    detail:
      '德芙巧克力似乎早已成为人们传递情感、享受美好瞬间的首选佳品。面对太多的选择,消费者关心的不仅仅是一盒糖果,而是产品的品质如何、口感如何、味道如何,价格是优惠等液'
  },
  {
    userId: 1,
    id: 5,
    title: '卫龙辣条',
    detail:
      '一直以来,卫龙美味坚守“让世界人人爱上中国味”的使命,不懈努力实现“传统美食娱乐化、休闲化、便捷化、亲民化、数智化,乐活123年的生态平台”的美好愿景,以传统美味为载体'
  },
  {
    userId: 1,
    id: 6,
    title: '伊利纯牛奶',
    detail:
      '伊利纯牛奶是伊利旗下很受欢迎的一款奶制品，含有很丰富的蛋白质、钙元素以及其它微量元素，给儿童、青少年、中老年人以及孕妇补充身体所需要的能量，并促进身体健康'
  },
  {
    userId: 1,
    id: 7,
    title: '旺仔牛奶',
    detail:
      '旺仔牛奶相信大家都喝过，小时候常常能在电视上看到它的广告，各种广告台词也深入人心，一看到它的IP就总容易想起那句再看，再看我就把你喝掉的广告语。记得小时候买一瓶喝都能高兴上好几天，它的奶香味浓郁，好喝到让人上瘾。其实旺仔牛奶不是真正的牛奶，而是一种复原乳，还被称为再制奶，它是用浓缩乳或者乳粉加入一定比例的水重新做成的一款乳液'
  },
  {
    userId: 1,
    id: 8,
    title: '旺旺碎冰冰',
    detail:
      '这款旺旺碎冰冰实在太棒了太好吃了,旺旺是大品牌质量有保障,品质信得过,孩子非常喜欢吃,各种口味都有,酸甜可口,让人爱不释口,回味无穷液'
  },
  {
    userId: 1,
    id: 9,
    title: '喜之郎',
    detail:
      '喜之郎品牌，创立于1992年，属于广东喜之郎集团有限公司，旗下产品包括喜之郎果冻、美好时光海苔、优乐美奶茶、开心时间饼干等。喜之郎系列产品被评为“中国名牌产品”液'
  },
  {
    userId: 1,
    id: 10,
    title: '德芙',
    detail:
      '德芙巧克力似乎早已成为人们传递情感、享受美好瞬间的首选佳品。面对太多的选择,消费者关心的不仅仅是一盒糖果,而是产品的品质如何、口感如何、味道如何,价格是优惠等液'
  },
  {
    userId: 1,
    id: 11,
    title: '伊利纯牛奶',
    detail:
      '伊利纯牛奶是伊利旗下很受欢迎的一款奶制品，含有很丰富的蛋白质、钙元素以及其它微量元素，给儿童、青少年、中老年人以及孕妇补充身体所需要的能量，并促进身体健康'
  },
  {
    userId: 1,
    id: 12,
    title: '卫龙辣条',
    detail:
      '一直以来,卫龙美味坚守“让世界人人爱上中国味”的使命,不懈努力实现“传统美食娱乐化、休闲化、便捷化、亲民化、数智化,乐活123年的生态平台”的美好愿景,以传统美味为载体'
  },
  {
    userId: 1,
    id: 13,
    title: '旺旺碎冰冰',
    detail:
      '这款旺旺碎冰冰实在太棒了太好吃了,旺旺是大品牌质量有保障,品质信得过,孩子非常喜欢吃,各种口味都有,酸甜可口,让人爱不释口,回味无穷液'
  },
  {
    userId: 1,
    id: 14,
    title: '卫龙辣条',
    detail:
      '一直以来,卫龙美味坚守“让世界人人爱上中国味”的使命,不懈努力实现“传统美食娱乐化、休闲化、便捷化、亲民化、数智化,乐活123年的生态平台”的美好愿景,以传统美味为载体'
  },
  {
    userId: 1,
    id: 15,
    title: '德芙',
    detail:
      '德芙巧克力似乎早已成为人们传递情感、享受美好瞬间的首选佳品。面对太多的选择,消费者关心的不仅仅是一盒糖果,而是产品的品质如何、口感如何、味道如何,价格是优惠等液'
  },
  {
    userId: 1,
    id: 16,
    title: '伊利纯牛奶',
    detail:
      '伊利纯牛奶是伊利旗下很受欢迎的一款奶制品，含有很丰富的蛋白质、钙元素以及其它微量元素，给儿童、青少年、中老年人以及孕妇补充身体所需要的能量，并促进身体健康'
  },
  {
    userId: 1,
    id: 17,
    title: '旺仔牛奶',
    detail:
      '旺仔牛奶相信大家都喝过，小时候常常能在电视上看到它的广告，各种广告台词也深入人心，一看到它的IP就总容易想起那句再看，再看我就把你喝掉的广告语。记得小时候买一瓶喝都能高兴上好几天，它的奶香味浓郁，好喝到让人上瘾。其实旺仔牛奶不是真正的牛奶，而是一种复原乳，还被称为再制奶，它是用浓缩乳或者乳粉加入一定比例的水重新做成的一款乳液'
  },
  {
    userId: 1,
    id: 18,
    title: '卫龙辣条',
    detail:
      '一直以来,卫龙美味坚守“让世界人人爱上中国味”的使命,不懈努力实现“传统美食娱乐化、休闲化、便捷化、亲民化、数智化,乐活123年的生态平台”的美好愿景,以传统美味为载体'
  },
  {
    userId: 1,
    id: 19,
    title: '喜之郎',
    detail:
      '喜之郎品牌，创立于1992年，属于广东喜之郎集团有限公司，旗下产品包括喜之郎果冻、美好时光海苔、优乐美奶茶、开心时间饼干等。喜之郎系列产品被评为“中国名牌产品”液'
  },
  {
    userId: 1,
    id: 20,
    title: '德芙',
    detail:
      '德芙巧克力似乎早已成为人们传递情感、享受美好瞬间的首选佳品。面对太多的选择,消费者关心的不仅仅是一盒糖果,而是产品的品质如何、口感如何、味道如何,价格是优惠等液'
  },
  {
    userId: 1,
    id: 21,
    title: '伊利纯牛奶',
    detail:
      '伊利纯牛奶是伊利旗下很受欢迎的一款奶制品，含有很丰富的蛋白质、钙元素以及其它微量元素，给儿童、青少年、中老年人以及孕妇补充身体所需要的能量，并促进身体健康'
  },
  {
    userId: 1,
    id: 22,
    title: '旺仔牛奶',
    detail:
      '旺仔牛奶相信大家都喝过，小时候常常能在电视上看到它的广告，各种广告台词也深入人心，一看到它的IP就总容易想起那句再看，再看我就把你喝掉的广告语。记得小时候买一瓶喝都能高兴上好几天，它的奶香味浓郁，好喝到让人上瘾。其实旺仔牛奶不是真正的牛奶，而是一种复原乳，还被称为再制奶，它是用浓缩乳或者乳粉加入一定比例的水重新做成的一款乳液'
  },
  {
    userId: 1,
    id: 23,
    title: '旺旺碎冰冰',
    detail:
      '这款旺旺碎冰冰实在太棒了太好吃了,旺旺是大品牌质量有保障,品质信得过,孩子非常喜欢吃,各种口味都有,酸甜可口,让人爱不释口,回味无穷液'
  },
  {
    userId: 1,
    id: 24,
    title: '喜之郎',
    detail:
      '喜之郎品牌，创立于1992年，属于广东喜之郎集团有限公司，旗下产品包括喜之郎果冻、美好时光海苔、优乐美奶茶、开心时间饼干等。喜之郎系列产品被评为“中国名牌产品”液'
  },
  {
    userId: 1,
    id: 25,
    title: '德芙',
    detail:
      '德芙巧克力似乎早已成为人们传递情感、享受美好瞬间的首选佳品。面对太多的选择,消费者关心的不仅仅是一盒糖果,而是产品的品质如何、口感如何、味道如何,价格是优惠等液'
  },
  {
    userId: 1,
    id: 26,
    title: '伊利纯牛奶',
    detail:
      '伊利纯牛奶是伊利旗下很受欢迎的一款奶制品，含有很丰富的蛋白质、钙元素以及其它微量元素，给儿童、青少年、中老年人以及孕妇补充身体所需要的能量，并促进身体健康'
  },
  {
    userId: 1,
    id: 27,
    title: '旺仔牛奶',
    detail:
      '旺仔牛奶相信大家都喝过，小时候常常能在电视上看到它的广告，各种广告台词也深入人心，一看到它的IP就总容易想起那句再看，再看我就把你喝掉的广告语。记得小时候买一瓶喝都能高兴上好几天，它的奶香味浓郁，好喝到让人上瘾。其实旺仔牛奶不是真正的牛奶，而是一种复原乳，还被称为再制奶，它是用浓缩乳或者乳粉加入一定比例的水重新做成的一款乳液'
  },
  {
    userId: 1,
    id: 28,
    title: '旺旺碎冰冰',
    detail:
      '这款旺旺碎冰冰实在太棒了太好吃了,旺旺是大品牌质量有保障,品质信得过,孩子非常喜欢吃,各种口味都有,酸甜可口,让人爱不释口,回味无穷液'
  },
  {
    userId: 1,
    id: 29,
    title: '喜之郎',
    detail:
      '喜之郎品牌，创立于1992年，属于广东喜之郎集团有限公司，旗下产品包括喜之郎果冻、美好时光海苔、优乐美奶茶、开心时间饼干等。喜之郎系列产品被评为“中国名牌产品”液'
  },
  {
    userId: 1,
    id: 30,
    title: '德芙',
    detail:
      '德芙巧克力似乎早已成为人们传递情感、享受美好瞬间的首选佳品。面对太多的选择,消费者关心的不仅仅是一盒糖果,而是产品的品质如何、口感如何、味道如何,价格是优惠等液'
  },
  {
    userId: 1,
    id: 31,
    title: '伊利纯牛奶',
    detail:
      '伊利纯牛奶是伊利旗下很受欢迎的一款奶制品，含有很丰富的蛋白质、钙元素以及其它微量元素，给儿童、青少年、中老年人以及孕妇补充身体所需要的能量，并促进身体健康'
  },
  {
    userId: 1,
    id: 32,
    title: '卫龙辣条',
    detail:
      '一直以来,卫龙美味坚守“让世界人人爱上中国味”的使命,不懈努力实现“传统美食娱乐化、休闲化、便捷化、亲民化、数智化,乐活123年的生态平台”的美好愿景,以传统美味为载体'
  },
  {
    userId: 1,
    id: 33,
    title: '卫龙辣条',
    detail:
      '一直以来,卫龙美味坚守“让世界人人爱上中国味”的使命,不懈努力实现“传统美食娱乐化、休闲化、便捷化、亲民化、数智化,乐活123年的生态平台”的美好愿景,以传统美味为载体'
  },
  {
    userId: 1,
    id: 34,
    title: '卫龙辣条',
    detail:
      '一直以来,卫龙美味坚守“让世界人人爱上中国味”的使命,不懈努力实现“传统美食娱乐化、休闲化、便捷化、亲民化、数智化,乐活123年的生态平台”的美好愿景,以传统美味为载体'
  }
]

/**
 * 用户列表
 */
var userList = [
  {
    id: 1,
    nickName: '小明',
    userName: '小明',
    roles: [
      {
        id: 1,
        name: '管理员'
      },
      {
        id: 2,
        name: '普通用户'
      }
    ]
  },
  {
    id: 2,
    nickName: '红红',
    userName: '红红',
    roles: [
      {
        id: 1,
        name: '管理员'
      }
    ]
  },
  {
    id: 3,
    nickName: '绿绿',
    userName: '绿绿',
    roles: [
      {
        id: 2,
        name: '普通用户'
      }
    ]
  }
]

/**
 * 角色列表
 */
var roleList = [
  {
    name: '管理员',
    id: 1,
    authority: [2, 5, 7, 9, 10, 12, 13, 14, 17, 18]
  },
  {
    name: '普通用户',
    id: 2,
    authority: [4, 7, 9, 10, 12, 13]
  }
]

/**
 * 权限列表
 */
var authorityList = [
  {
    id: 1,
    name: '订单列表',
    list: [
      {
        id: 2,
        name: '订单详情'
      },
      {
        id: 3,
        name: '查看',
        list: [
          {
            id: 5,
            name: '审核'
          }
        ]
      },
      {
        id: 4,
        name: '删除'
      }
    ]
  },
  {
    id: 6,
    name: '商品列表',
    list: [
      {
        id: 7,
        name: '商品详情'
      },
      {
        id: 8,
        name: '查看',
        list: [
          {
            id: 9,
            name: '审核'
          }
        ]
      },
      {
        id: 10,
        name: '删除'
      }
    ]
  },
  {
    id: 11,
    name: '用户列表',
    list: [
      {
        id: 12,
        name: '用户详情'
      },
      {
        id: 13,
        name: '查看'
      },
      {
        id: 14,
        name: '删除'
      }
    ]
  },
  {
    id: 15,
    name: '角色',
    list: [
      {
        id: 16,
        name: '角色详情'
      },
      {
        id: 17,
        name: '查看'
      },
      {
        id: 18,
        name: '删除'
      }
    ]
  }
]

/**
 * 处理 [post /api/login] 请求
 */
const handleLogin = (req, res) => {
  if (req.method !== 'POST') {
    res.end(JSON.stringify({}))
    return
  }
  let data = ''
  req.on('data', (chunk) => {
    data += chunk
  })
  req.on('end', () => {
    console.log('>    data: ', data)
    let payload = JSON.parse(data)
    console.log('> payload: ', payload)
    if (payload['username'] === 'admin' && payload['password'] === '12345678') {
      let responseData = {
        code: 200,
        token: crypto.randomBytes(16).toString('hex'),
        msg: '操作成功'
      }
      res.end(JSON.stringify(responseData))
    } else {
      let responseData = {
        code: 400,
        msg: '操作失败'
      }
      res.end(JSON.stringify(responseData))
    }
  })
}

/**
 * 处理 [get /api/getGoodsList ] 请求
 */
const handleGetGoodsList = (req, res) => {
  let queryData = url.parse(req.url, true).query
  let page = queryData.page || 1
  if (page <= 0) {
    page = 1
  }
  let pageSize = queryData.pageSize || 10
  if (pageSize <= 0) {
    pageSize = 10
  }
  let title = queryData.title || ''
  let detail = queryData.detail || ''
  targetList = goodsList.filter(
    (item) => item.title.includes(title) && item.detail.includes(detail)
  )
  res.end(
    JSON.stringify({
      code: 200,
      msg: '请求成功',
      result: targetList.slice((page - 1) * pageSize, page * pageSize),
      total: targetList.length
    })
  )
}

/**
 * 处理 [get /api/getRoleList ] 请求
 */
const handleGetRoleList = (req, res) => {
  res.end(
    JSON.stringify({
      code: 200,
      msg: '请求成功',
      result: roleList
    })
  )
}

/**
 * 处理 [get /api/getUserList ] 请求
 */
const handleGetUserList = (req, res) => {
  let queryData = url.parse(req.url, true).query
  let roleId = queryData.roleId
  let nickName = queryData.nickName
  let targetList = userList.filter((user) => {
    if (roleId) {
      roleId = Number.parseInt(roleId)
      return user.nickName.includes(nickName) && user.roles.map((role) => role.id).includes(roleId)
    } else {
      return user.nickName.includes(nickName)
    }
  })
  res.end(
    JSON.stringify({
      code: 200,
      msg: '请求成功',
      result: targetList
    })
  )
}

/**
 * 处理 [post /api/updateUser ] 请求
 */
const handleUpdateUser = (req, res) => {
  if (req.method !== 'POST') {
    res.end(JSON.stringify({}))
    return
  }
  let data = ''
  req.on('data', (chunk) => {
    data += chunk
  })
  req.on('end', () => {
    console.log('>    data: ', data)
    let payload = JSON.parse(data)
    console.log('> payload: ', payload)

    let $roles = payload['roles'] || []
    let roles = roleList.filter((role) => $roles.includes(role.id))
    let $id = payload['id'] || 0
    let $nickName = payload['nickName'] || ''
    let $userName = payload['userName'] || ''
    let selectedUserList = userList.filter((user) => $id === user.id)
    if (selectedUserList.length === 1) {
      let targetUser = selectedUserList[0]
      targetUser['id'] = $id
      targetUser['nickName'] = $nickName
      targetUser['userName'] = $userName
      targetUser['roles'] = roles
    }
    res.end(
      JSON.stringify({
        code: 200,
        msg: '操作成功'
      })
    )
  })
}

/**
 * 处理 [post /api/addRole ] 请求
 */
const handleAddRole = (req, res) => {
  if (req.method !== 'POST') {
    res.end(JSON.stringify({}))
    return
  }
  let data = ''
  req.on('data', (chunk) => {
    data += chunk
  })
  req.on('end', () => {
    console.log('>    data: ', data)
    let payload = JSON.parse(data)
    console.log('> payload: ', payload)

    let roleName = payload['name'] || ''
    if (typeof roleName === 'string' && roleName.trim().length > 0) {
      roleName = roleName.trim()
      let exist = roleList.filter((role) => role.name === roleName).length > 0
      if (!exist) {
        let maxRoleId = 0
        roleList.forEach((role) => {
          if (role.id > maxRoleId) {
            maxRoleId = role.id
          }
        })
        roleList.push({
          id: maxRoleId + 1,
          name: roleName,
          authority: []
        })
        res.end(
          JSON.stringify({
            code: 200,
            result: 'success',
            msg: '操作成功'
          })
        )
        return
      }
    }

    res.end(
      JSON.stringify({
        code: 200,
        result: 'error',
        msg: '输入不合法或角色名已经存在'
      })
    )
  })
}

/**
 * 获取权限列表
 */
const handleGetAuthorityList = (req, res) => {
  let queryData = url.parse(req.url, true).query

  let roleId = queryData.id || ''
  if (roleId) {
    let id = Number.parseInt(roleId)
    let selectedList = roleList.filter((role) => role.id === id)
    if (selectedList.length === 1) {
      res.end(
        JSON.stringify({
          code: 200,
          msg: '请求成功',
          result: selectedList[0].authority,
          name: selectedList[0].name
        })
      )
      return
    }
  }

  res.end(
    JSON.stringify({
      code: 200,
      msg: '请求成功',
      result: authorityList
    })
  )
}

//权限 id 列表，对应的这些权限包含子权限，为了更好的页面展示，这些权限应该被过滤掉
let excludedAuthority = [1, 3, 6, 8, 11, 15]
function process(givenAuthorityList) {
  let res = []
  givenAuthorityList.forEach((o) => {
    if (!excludedAuthority.includes(o) && !res.includes(o)) {
      res.push(o)
    }
  })
  return res
}

/**
 * 修改角色信息
 */
const handleUpdateRole = (req, res) => {
  if (req.method !== 'POST') {
    res.end(JSON.stringify({}))
    return
  }
  let data = ''
  req.on('data', (chunk) => {
    data += chunk
  })
  req.on('end', () => {
    console.log('>    data: ', data)
    let payload = JSON.parse(data)
    console.log('> payload: ', payload)

    let roleId = payload.id || 0
    let selectedList = roleList.filter((role) => role.id === roleId)
    if (selectedList.length === 1) {
      let role = selectedList[0]

      let roleName = payload.name || ''
      if (typeof roleName === 'string' && roleName.trim().length > 0) {
        //TODO 考虑同名
        role.name = roleName.trim()
      }
      role.authority = process(payload.authority)
    }

    res.end(
      JSON.stringify({
        code: 200,
        msg: '操作成功'
      })
    )
  })
}

function solveCorsProblem(res) {
  res.writeHead(200, {
    'Content-Type': 'application/json',
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': '*',
    'Access-Control-Allow-Headers': 'Content-Type'
  })
}

const server = http.createServer((req, res) => {
  console.log('>     uri: ', req.url)
  console.log('>  method: ', req.method)
  if (req.url.startsWith('/api/login')) {
    solveCorsProblem(res)
    handleLogin(req, res)
  } else if (req.url.startsWith('/api/getGoodsList') && req.method === 'GET') {
    solveCorsProblem(res)
    handleGetGoodsList(req, res)
  } else if (req.url.startsWith('/api/getRoleList') && req.method === 'GET') {
    solveCorsProblem(res)
    handleGetRoleList(req, res)
  } else if (req.url.startsWith('/api/getUserList') && req.method === 'GET') {
    solveCorsProblem(res)
    handleGetUserList(req, res)
  } else if (req.url.startsWith('/api/updateUser')) {
    solveCorsProblem(res)
    handleUpdateUser(req, res)
  } else if (req.url.startsWith('/api/addRole')) {
    solveCorsProblem(res)
    handleAddRole(req, res)
  } else if (req.url.startsWith('/api/getAuthorityList') && req.method === 'GET') {
    solveCorsProblem(res)
    handleGetAuthorityList(req, res)
  } else if (req.url.startsWith('/api/updateRole')) {
    solveCorsProblem(res)
    handleUpdateRole(req, res)
  } else {
    res.writeHead(404, { 'Content-Type': 'text/plain' })
    res.end('Not Found')
  }
})

server.listen(8080, () => {
  console.log('Server running at http://localhost:8080/')
})
