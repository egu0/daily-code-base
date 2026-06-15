import { mergeOptions } from "../utils/index"

export function initGlobalApi(Vue) {
    Vue.options = {} //静态类型
    Vue.Mixin = function (mixin) {//静态方法。其中的 this 指 Vue 类
        // Vue.options: { data: { .. }, created: [ f1(), f2() ..], watch: [ f3(), f4() ..], .. }
        Vue.options = mergeOptions(Vue.options, mixin)
    }

    Vue.options.components = {}

    //参考：https://v2.cn.vuejs.org/v2/api/#Vue-component
    Vue.component = function (id, componentDefinition) {
        componentDefinition.name = componentDefinition.name || id

        let extendedComponentDef = this.extend(componentDefinition)
        this.options.components[id] = extendedComponentDef
    }

    //参考：https://v2.cn.vuejs.org/v2/api/#Vue-extend
    Vue.extend = function (componentDefinition) {
        const Son = function vueComponent(options) {
            this._init(options)
        }

        // 原型链：
        //   Son实例 ==proto==> Object.create(Vue.prototype) ==proto==> Vue.prototype
        //   Son.prototype ===/

        //继承 Vue 类型：根据 Vue 原型创建对象，并将它作为 Son 原型
        Son.prototype = Object.create(Vue.prototype)
        //修改 Son 构造器
        Son.prototype.constructor = Son
        //将 Vue.options 和 组件的定义信息 合并到 Son.options
        Son.options = mergeOptions(Vue.options, componentDefinition)

        return Son
    }
}