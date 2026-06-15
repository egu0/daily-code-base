import { nextTick } from "../utils/nextTick"
import { popTarget, pushTarget } from "./dep"

let id = 0

class Watcher {
    constructor(vm, updateComponentFn, callback, options) {
        this.vm = vm
        this.exprOrFn = updateComponentFn
        this.cb = callback
        this.options = options
        this.user = !!options.user

        //computed
        this.lazy = !!options.lazy // lazy=true 表示本实例为 computedWatcher
        this.dirty = this.lazy

        this.id = id++
        this.deps = []
        this.depIds = new Set()

        if (typeof updateComponentFn === 'function') {
            this.getter = updateComponentFn
        } else {
            this.getter = function () {
                // exprOrFn 是一个字符串，可能包含多级结构，比如 'stu.detail.status'
                let pathArr = this.exprOrFn.split('.')
                let curObj = this.vm
                for (let i = 0; i < pathArr.length; i++) {
                    let curKey = pathArr[i]
                    curObj = curObj[curKey]
                }
                return curObj
            }
        }

        // 初次渲染时，将结果保存到 this.value
        this.value = this.lazy ? void 0 : this.get()
    }

    // 首次渲染
    get() {
        pushTarget(this)

        // 渲染，执行 vm._render() 中的 _s(变量名) 时，
        //   会通过 [msg.变量名] 方式调用变量的 get() 方法
        let value = this.getter.call(this.vm)

        popTarget()

        return value
    }

    run() {
        let newVal = this.get()
        let oldVal = this.value
        this.value = newVal
        if (this.user) {
            this.cb.call(this.vm, newVal, oldVal)
        }
    }

    // 更新
    update() {
        if (this.lazy) {
            this.dirty = true
        } else {
            watcherEnqueue(this)
        }
    }

    evaluate() {
        this.value = this.get()
        this.dirty = false
    }

    addDep(dep) {
        if (dep && !this.depIds.has(dep.id)) {
            this.deps.push(dep)
            this.depIds.add(dep.id)
        }
    }

    depend() {
        let idx = this.deps.length
        while (idx--) {
            this.deps[idx].depend()
        }
    }
}

let watcherQueue = []
let hittedWatchers = {} // 键为 watcher 的 id，值为布尔表示是否存在
let setUp = false

function watcherEnqueue(watcher) {
    let id = watcher.id

    //去重。通过 hittedWatchers 集合记录已经加入的 watcher
    if (hittedWatchers[id] == null) {
        watcherQueue.push(watcher)
        hittedWatchers[id] = true

        //防抖。setUp 表示是否已设置异步处理钩子
        if (!setUp) {
            nextTick(flushWatcher)
        }
        setUp = true
    }
}

function flushWatcher() {
    watcherQueue.forEach(watcher => {
        watcher.run()

        //不是用户定义的 watcher 时，执行 callback
        if (!watcher.user) {
            watcher.cb()
        }
    })
    // 状态重置
    watcherQueue = []
    hittedWatchers = {}
    setUp = false
}

export default Watcher