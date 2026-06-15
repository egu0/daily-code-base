export function renderMixin(Vue) {
    Vue.prototype._c = function () {//标签
        return createElement(this, ...arguments)
    }
    Vue.prototype._v = function () {//文本
        return createText(...arguments)
    }
    Vue.prototype._s = function (val) {//变量
        if (!val) {
            return ""
        }
        return (typeof val === 'object') ? JSON.stringify(val) : val
    }

    /**
     * 根据 render 函数生成虚拟 dom
     * @param {*} el 
     */
    Vue.prototype._render = function () {
        let vm = this
        let render = vm.$options.render
        let vnode = render.call(this)
        return vnode
    }
}

function createElement(vm, tagName, attrs = {}, ...children) {
    attrs = attrs || {}
    if (isReserved(tagName)) {
        return createVNode(vm, tagName, attrs, attrs ? attrs.key : undefined, children, undefined, undefined)
    } else {
        const Ctor = vm.$options['components'][tagName]
        return createComponent(vm, tagName, attrs, children, Ctor)
    }
}

function createComponent(vm, tagName, attrs, children, Ctor) {
    if (typeof Ctor == 'object') {
        Ctor = vm.constructor.extend(Ctor)
    }

    attrs.hook = {
        init(vnode) {
            let instance = new vnode.compOptions.Ctor({})
            vnode.componentInstance = instance
            instance.$mount()
        }
    }
    return createVNode('vm', 'vue-comp_' + tagName, attrs, undefined, undefined, undefined, { Ctor, children })
}

//https://zhuanlan.zhihu.com/p/89214182
let TagList = [
    'html', 'head', 'title', 'base', 'link', 'meta', 'style', 'script', 'noscript',
    'template', 'body', 'section', 'nav', 'article', 'aside', 'h1', 'h2', 'h3', 'h4',
    'h5', 'h6', 'header', 'footer', 'p', 'hr', 'pre', 'blockquote', 'ol', 'ul', 'li',
    'dl', 'dt', 'dd', 'figure', 'figcaption', 'div', 'a', 'em', 'strong', 'small', 's',
    'cite', 'q', 'dfn', 'abbr', 'data', 'form', 'fieldset', 'legend', 'label', 'input',
    'button', 'select', 'datalist', 'optgroup', 'option', 'textarea', 'keygen', 'output',
    'progress', 'meter', 'details', 'summary', 'menuitem', 'menu', 'img', 'iframe',
    'embed', 'object', 'param', 'video', 'audio', 'source', 'track', 'canvas', 'map',
    'area', 'svg', 'math', 'table', 'caption', 'colgroup', 'col', 'tbody', 'thead', 'tfoot',
    'tr', 'td', 'th', 'time', 'code', 'var', 'samp', 'hbd', 'sub', 'i', 'b', 'u', 'mark',
    'ruby', 'rt', 'rp', 'bdi', 'bdo', 'span', 'br', 'wbr', 'ins', 'del',
]

function isReserved(tagName) {
    return TagList.includes(tagName)
}

function createVNode(vm, tagName, data, key, children, text, compOptions) {
    return {
        vm,
        tag: tagName,
        data,
        key,
        children,
        text,
        compOptions
    }
}

function createText(text) {
    return createVNode(undefined, undefined, undefined, undefined, undefined, text, undefined)
}