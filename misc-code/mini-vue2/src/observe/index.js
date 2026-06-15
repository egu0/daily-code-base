import { arrayProxyProto } from "./arr"
import Dep from "./dep"

//观测数据
export function observer(data) {
    // 判断
    if (typeof data != 'object' || data == null) {
        return data
    }

    return new Observer(data)
}

class Observer {
    constructor(data) {
        //为数据添加 __ob__ 属性
        Object.defineProperty(data, "__ob__", {
            enumerable: false,
            value: this
        })

        //为数据（对象或数组）添加 Dep 实例
        this.dep = new Dep()

        if (Array.isArray(data)) {
            data.__proto__ = arrayProxyProto
            this.walk_array(data)
        } else {
            this.walk(data)
        }
    }

    //观测 data 对象中的每个元素
    walk(data) {
        let keys = Object.keys(data)
        for (let i = 0; i < keys.length; i++) {
            let key = keys[i]
            let val = data[key]
            defineReactive(data, key, val)
        }
    }

    //观测 data 对象中的每个元素
    walk_array(data) {
        for (let i = 0; i < data.length; i++) {
            observer(data[i])
        }
    }
}

function defineReactive(data, key, val) {

    // val 可能是对象，需要为其添加观测
    // observer(x)：当 x 为对象或数组时，该函数返回一个 Observer 实例 childDep
    //              如果 x 为数组，那么 childDep.dep 表示该数组关联的的 watcher
    let childDep = observer(val)

    // 为 key 添加 dep
    let keyDep = new Dep()

    // 为 val 添加观测
    Object.defineProperty(data, key, {
        get() {
            if (Dep.target) {
                keyDep.depend()

                if (childDep && childDep.dep) {
                    childDep.dep.depend()
                }
            }
            //console.log('get:', key);
            return val
        },

        set(newVal) {
            //console.log('set:', key, ', new value:', newVal);
            if (val === newVal) {
                return
            } else {
                // 为通过 = 修改的新对象设置观测
                observer(newVal)
                val = newVal
                keyDep.notify()
            }
        }
    })
}