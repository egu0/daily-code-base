import Watcher from "./observe/watcher"
import { patch } from "./vnode/patch"

export function mountComponent(vm) {

    callHook(vm, 'beforeMounted')

    // vm._render() : 将 render 函数转为虚拟 dom
    // vm._update() : 将虚拟 dom 转为真实 dom 并替换原 dom
    let updateComponent = () => {
        vm._update(vm._render())
    }

    new Watcher(vm, updateComponent, () => {
        callHook(vm, 'updated')
    }, true)

    callHook(vm, 'mounted')
}

export function lifecycleMixin(Vue) {
    Vue.prototype._update = function (newVNode) {
        let vm = this

        let lastUsedVNode = vm._vnode
        if (!lastUsedVNode) {
            //第一次，直接替换掉真实 dom
            vm.$el = patch(vm.$el, newVNode)
            vm._vnode = newVNode
        } else {
            //第二三四五...次，使用原虚拟 dom、新的虚拟 dom 和旧真实 dom (lastUsedVNode.el)，
            //根据 diff 算法计算新的真实 dom
            patch(lastUsedVNode, newVNode)
        }
    }
}

/**
 * 调用生命周期钩子
 * @param {*} vm 
 * @param {*} hookName 
 */
export function callHook(vm, hookName) {
    let hooks = vm.$options[hookName]
    if (hooks) {
        hooks.forEach(hookFn => {
            hookFn.call(vm)
        })
    }
}