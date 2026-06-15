import Dep from "./observe/dep"
import { observer } from "./observe/index"
import Watcher from "./observe/watcher"
import { nextTick } from "./utils/nextTick"

export function initState(vm) {
    let options = vm.$options

    if (options.data) {
        initData(vm)
    }
    if (options.props) {
        initProps(vm)
    }
    if (options.watch) {
        initWatch(vm)
    }
    if (options.computed) {
        initComputed(vm)
    }
    if (options.methods) {
        initMethods(vm)
    }
}

function initProps(vm) { }
function initMethods(vm) { }

function initComputed(vm) {
    //vm._computedWatchers 用来存放全局 computedWatcher
    let computedWatchers = vm._computedWatchers = {}
    let computed = vm.$options.computed

    for (let key in computed) {
        let fnOrObj = computed[key]

        //为每个 computed 属性创建一个对应的 Watcher 实例（lazy=true 作为标识）
        let getMethod = (typeof fnOrObj === 'function') ? fnOrObj : fnOrObj.get
        computedWatchers[key] = new Watcher(vm, getMethod, () => { }, { lazy: true })

        //代理 computed 属性对应的 get 方法
        defineComputed(vm, key, fnOrObj)
    }
}

function defineComputed(vm, key, fnOrObj) {
    //https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/defineProperty#description
    let attributes = {
        enumerable: true,
        configurable: true,
        get: () => { },
        set: () => { }
    }

    if (typeof fnOrObj === 'function') {
        attributes.get = createComputedGetter(key)
    } else {
        attributes.get = createComputedGetter(key)
        attributes.set = fnOrObj.set
    }

    Object.defineProperty(vm, key, attributes)
}

function createComputedGetter(key) {
    return function () {
        let watcher = this._computedWatchers[key]
        if (watcher.dirty) {
            watcher.evaluate()
        }
        if (Dep.target) {
            watcher.depend()
        }
        return watcher.value
    }
}

function initWatch(vm) {
    let watch = vm.$options.watch
    for (let key in watch) {
        let handler = watch[key]
        if (Array.isArray(handler)) {
            handler.forEach(item => {
                createWatcher(vm, key, item)
            })
        } else {
            // handler 可能是对象、字符串、函数类型
            createWatcher(vm, key, handler)
        }
    }
}

export function createWatcher(vm, exprOrFn, handler, options) {
    if (typeof handler === 'object') {
        options = handler // handler 中包含配置项和 handler 函数
        handler = handler.handler
    } else if (typeof handler === 'string') {
        handler = vm[handler]
    }

    return vm.$watch(exprOrFn, handler, options)
}

function initData(vm) {
    let data = vm.$options.data
    let methods = vm.$options.methods

    // 获取 data 对象
    // data 可能是函数，也可能是对象
    // 如果 data 是函数，那么需要为它绑定 this 到 vm
    data = typeof data === 'function' ? data.call(vm) : data
    // 将 data 对象放入 vm 中
    vm._data = data
    vm._methods = methods

    // 将 _data 中的属性代理到 vm 实例
    for (let key in data) {
        proxy(vm, "_data", key)
    }
    // 方法代理
    if (methods) {
        for (let key in methods) {
            proxy(vm, '_methods', key)
        }
    }

    // 劫持 data
    observer(data)
}

function proxy(vm, source, key) {
    Object.defineProperty(vm, key, {
        get() {
            return vm[source][key]
        },
        set(newVal) {
            vm[source][key] = newVal
        }
    })
}

export function stateMixin(Vue) {
    Vue.prototype.$nextTick = function (cb) {
        nextTick(cb)
    }

    Vue.prototype.$watch = function (exprOrFn, handler, options) {
        new Watcher(this, exprOrFn, handler, { ...options, user: true })

        //立即执行
        if (options && options.immediate) {
            handler.call(this)
        }
    }
}