import { compileToFunction } from "./compile/index"
import { initState } from "./initState"
import { callHook, mountComponent } from "./lifecycle"
import { mergeOptions } from "./utils/index"

export function initMixin(Vue) {
    Vue.prototype._init = function (options) {
        let vm = this // 原型链中的 this 是 Vue 实例
        vm.$options = mergeOptions(vm.constructor.options, options)

        callHook(vm, 'beforeCreated')

        //初始化 data 数据
        initState(vm)

        callHook(vm, 'created')

        //模板编译
        if (vm.$options.el) {
            vm.$mount(vm.$options.el)
        }
        //  else {
        //     throw new Error("'el' is not defined")
        // }
    }

    Vue.prototype.$mount = function (el) {
        let vm = this
        let options = vm.$options
        if (!options.render) {
            let template = options.template
            if (el) {
                el = document.querySelector(el)
                vm.$el = el
                template = el.outerHTML
            }
            let render = compileToFunction(template)
            vm.$options.render = render
        }
        mountComponent(vm)
    }
}
