let collection = {}
collection.data = function (oldData, newData) {
    return newData
}
// collection.computed = function () { }
// collection.watch = function () { }
// collection.methods = function () { }

collection.components = function (oldData, newData) {
    const obj = Object.create(oldData)
    if (newData) {
        for (let key in newData) {
            obj[key] = newData[key]
        }
    }
    return obj
}

//生命周期钩子
export const HOOKS = [
    "beforeCreate",
    "created",
    "beforeMount",
    "mounted",
    "beforeUpdate",
    "updated",
    "beforeDestory",
    "destroyed"
]

//批量为声明周期钩子添加处理函数
//示例：collection.created = addToList(list, item) { .. }
HOOKS.forEach(hookName => {
    collection[hookName] = addToList
})

function addToList(list, item) {
    if (item) {
        if (list) {
            return list.concat(item)
        } else {
            return [item]
        }
    } else {
        return list
    }
}

/**
 * 合并选项： mixin 和 Vue.options
 * @param {*} oldOptions 已存在的选项，来自 Vue.options 这个静态变量
 * @param {*} mixinOptions 混入选项，来自 Vue.Mixin({ .. }) 这个静态函数的参数
 * @returns 合并后的结果
 */
export function mergeOptions(oldOptions, mixinOptions) {
    //合并后的选项
    const options = {}

    //遍历 Vue.optins 中的所有 key
    Object.keys(oldOptions).forEach(key => {
        mergeField(key)
    })
    //遍历 mixin 中的所有 key
    Object.keys(mixinOptions).forEach(key => {
        mergeField(key)
    })

    return options

    /**
     * 伪代码：options[key] = combine(Vue.options[key], mixin[key])
     */
    function mergeField(key) {
        if (collection[key]) {
            options[key] = collection[key](oldOptions[key], mixinOptions[key])
        } else {
            // 其他 key 直接覆盖
            options[key] = mixinOptions[key] || oldOptions[key]
        }
    }
}