const defaultTagRE = /\{\{((?:.|\r?\n)+?)\}\}/g // {{ ... }}

/**
 * 解析标签节点
 * @param {*} tagNode 标签节点
 * @returns render 字符串
 */
export function parseTagNode(tagNode) {
    let children = genChildren(tagNode.children)
    let render = `_c(${JSON.stringify(tagNode.tag)}`
        + `, ${tagNode.attrs.length > 0 ? genProps(tagNode.attrs) : 'null'}`
        + `${children.length > 0 ? ', ' + children : ''})`
    return render
}

// 根据子节点数组（children）处理子节点
function genChildren(children) {
    return children.map(child => parseNode(child)).join(', ')
}

// 解析节点
function parseNode(node) {
    if (node.type === 1) {// astNode.type === 1, 元素
        return parseTagNode(node)
    } else if (node.type === 3) {// astNode.type === 3, 文本
        let text = node.text
        defaultTagRE.lastIndex = 0

        //不包含插值表达式时
        if (!defaultTagRE.test(text)) {
            return `_v(${JSON.stringify(node.text)})`
        }

        //解析包含插值表达式的文本
        let lastIndex = defaultTagRE.lastIndex = 0
        let tokens = [], match
        while (match = defaultTagRE.exec(text)) {
            // lastIndex
            //  |
            // 'xxx{{aaa}}zzz'
            //     |
            //  index 

            let index = match.index
            if (index > lastIndex) {
                tokens.push(JSON.stringify(text.slice(lastIndex, index)))
            }
            tokens.push(`_s(${match[1].trim()})`)

            //          lastIndex
            //            |
            // 'xxx{{aaa}}zzz'
            //     |
            //  index   

            lastIndex = index + match[0].length
        }
        if (lastIndex < text.length) {
            tokens.push(JSON.stringify(text.slice(lastIndex)))
        }

        return `_v(${tokens.join(' + ')})`
    }
}

// 根据属性数组（ast 语法树节点的 attrs 属性）拼接字符串，比如
// html:  <div id="app" class="box" style="color: red; margin-top: 30px;">
// attrs: [{"name":"id","value":"app"},{"name":"class","value":"box"},{"name":"style","value":"color: red; margin-top: 30px;"}]
// 结果:  {id: "app", class: "box", style: {"color":"red","margin-top":"30px"}}
function genProps(attrs) {
    if (attrs.length === 0) {
        return '{}'
    }

    let result = ''
    for (let i = 0; i < attrs.length; i++) {
        let attr = attrs[i]
        if (attr.name === 'style') {
            let allStyleAttrsObj = {}
            attr.value.split(';')
                .filter(item => item.includes(':'))
                .forEach(item => {
                    let [key, value] = item.split(':')
                    allStyleAttrsObj[key.trim()] = value && value.trim()
                })
            attr.value = allStyleAttrsObj
        }
        result += `${attr.name}: ${JSON.stringify(attr.value)}, `
    }
    return `{${result.slice(0, -2)}}`
}

