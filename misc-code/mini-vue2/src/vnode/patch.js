/**
 * 根据虚拟 dom 生成 dom 并替换原 dom
 * @param {*} oldNode 原 dom
 * @param {*} vnode 虚拟 dom
 */
export function patch(oldNode, vnode) {
    if (!oldNode) {
        return createElm(vnode)
    }

    //首次渲染时，oldNode 是真实的 DOM 元素，拥有属性 nodeType
    if (oldNode.nodeType === 1) {
        debugger
        // 根据虚拟 dom 生成 dom
        let newNode = createElm(vnode)

        // 将生成的 dom 替换掉旧 dom
        let parentEle = oldNode.parentNode
        parentEle.insertBefore(newNode, oldNode.nextsibling)
        parentEle.removeChild(oldNode)

        return newNode
    } else {
        //此时 oldNode 时旧的虚拟 dom
        let oldVNode = oldNode, newVNode = vnode

        //更新标签类型
        //   <div>{{name}}</div>
        //   <p>{{name}}</p>
        if (oldVNode.tag !== newVNode.tag) {
            let oldNode = oldVNode.el
            let newNode = createElm(newVNode)
            oldNode.parentNode.replaceChild(newNode, oldNode)
            return
        }

        //更新文本
        //   <div>{{name}}</div>
        //   <div>{{code}}</div>
        if (!oldVNode.tag && (oldVNode.text !== newVNode.text)) {
            oldVNode.el.textContent = newVNode.text
            return
        }

        //更新属性
        //   <div id='apple'  key='jack' style='color: red; background-color: pink;'>{{name}}</div>
        //   <div hash='1234' key='zack' style='font-weight: 800; background-color: grey;'>{{name}}</div>
        //1.使用旧 dom
        newVNode.el = oldVNode.el
        //2.更新 newVnode.el 的属性
        updateElAttr(newVNode, oldVNode.data)

        //更新子元素
        let newChildren = newVNode.children || []
        let oldChildren = oldVNode.children || []
        if (newChildren.length > 0 && oldChildren.length > 0) {
            updateElChildren(newVNode.el, newChildren, oldChildren)
        } else if (newChildren.length > 0 && oldChildren.length === 0) {
            newChildren.forEach(child => newVNode.el.appendChild(createElm(child)))
        } else if (newChildren.length === 0 && oldChildren.length > 0) {
            newVNode.el.innerHTML = ''
        } else {
            //都没有子元素，则不用更新
        }

    }
}

function updateElChildren(el, newChildren, oldChildren) {
    //双指针
    let newLeftIdx = 0, newRightIdx = newChildren.length - 1
    let oldLeftIdx = 0, oldRightIdx = oldChildren.length - 1

    function isSameVNode(vnode1, vnode2) {
        return vnode1.tag === vnode2.tag && vnode1.key === vnode2.key
    }

    // 获取 oldChildren 的 key -> index 的映射表
    let oldChildrenMapping = {}
    oldChildren.forEach((child, idx) => {
        if (child.key) {
            oldChildrenMapping[child.key] = idx
        }
    })

    while (newLeftIdx <= newRightIdx && oldLeftIdx <= oldRightIdx) {
        if (isSameVNode(oldChildren[oldLeftIdx], newChildren[newLeftIdx])) {
            // 旧头 = 新头
            patch(oldChildren[oldLeftIdx++], newChildren[newLeftIdx++])
        } else if (isSameVNode(oldChildren[oldLeftIdx], newChildren[newRightIdx])) {
            // 旧头 = 新尾
            patch(oldChildren[oldLeftIdx++], newChildren[newRightIdx--])
        } else if (isSameVNode(oldChildren[oldRightIdx], newChildren[newRightIdx])) {
            // 旧尾 = 新尾
            patch(oldChildren[oldRightIdx--], newChildren[newRightIdx--])
        } else if (isSameVNode(oldChildren[oldRightIdx], newChildren[newLeftIdx])) {
            // 旧尾 = 新头
            patch(oldChildren[oldRightIdx--], newChildren[newLeftIdx++])
        } else {
            // 查表
            let newVDom = newChildren[newLeftIdx]
            let sameKeyIdx = oldChildrenMapping[newVDom.key]
            if (sameKeyIdx === undefined) { // newVDom.key 不存在于 oldChildren 中
                el.insertBefore(createElm(newVDom), oldChildren[oldLeftIdx].el)
            } else {
                let targetVNode = oldChildren[sameKeyIdx]
                oldChildren[sameKeyIdx] = null // 防止数组塌陷

                el.insertBefore(targetVNode.el, oldChildren[oldLeftIdx].el)
                patch(targetVNode, newChildren[newLeftIdx])
            }
            newLeftIdx++
        }
    }

    if (newLeftIdx <= newRightIdx) {
        for (let i = newLeftIdx; i <= newRightIdx; i++) {
            el.appendChild(createElm(newChildren[i]))
        }
    }

    // 删除掉 oldChildren 中没有用到的
    if (oldLeftIdx <= oldRightIdx) {
        for (let i = oldLeftIdx; i <= oldRightIdx; i++) {
            if (oldChildren[i]) {
                el.removeChild(oldChildren[i].el)
            }
        }
    }
}

function updateElAttr(vnode, oldProps = {}) {
    oldProps = oldProps || {}
    let newProps = vnode.data || {}
    let el = vnode.el

    //删除属性：oldProps 中有但 newProps 中没有、两者都有但值不同
    for (let key in oldProps) {
        if (!newProps[key] || newProps[key] != oldProps[key]) {
            el.removeAttribute(key)
        }
    }

    //如果 oldProps.style 和 newProps.style 不同，那么将 el.style 清空
    let newStyle = newProps.style || {}
    let oldStyle = oldProps.style || {}
    for (let key in oldStyle) {
        if (newStyle.length === 0 || !newStyle[key]
            || newStyle[key] != oldStyle[key]) {
            el.removeAttribute('style')
            break
        }
    }

    //将 newProps 中有的属性添加到 el
    for (let key in newProps) {
        if (key === 'style') {
            for (let styleName in newProps.style) {
                el.style[styleName] = newProps.style[styleName]
            }
        } else if (key === 'class') {
            el.className = newProps.class
        } else {
            el.setAttribute(key, newProps[key])
        }
    }
}

export function createElm(vnode) {
    let { vm, tag, data, key, children, text } = vnode

    if (typeof tag === 'string') {
        if (createComponent(vnode)) {
            //创建组件的真实 dom
            return vnode.componentInstance.$el
        } else {
            //标签
            vnode.el = document.createElement(tag)
            //为 vnode.el 添加属性
            updateElAttr(vnode)
            //为 vnode.el 添加子元素
            children && children.forEach(child => {
                vnode.el.appendChild(createElm(child))
            })
        }
    } else {
        //文本
        vnode.el = document.createTextNode(text)
    }

    return vnode.el
}

function createComponent(vnode) {
    if (vnode.data && vnode.data.hook && vnode.data.hook.init) {
        //调用 init 方法创建子组件实例
        vnode.data.hook.init(vnode)
    }
    return vnode.componentInstance ? true : false
}