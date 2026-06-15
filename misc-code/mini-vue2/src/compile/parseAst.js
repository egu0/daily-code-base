const ncname = `[a-zA-Z_][\\-\\.0-9_a-zA-Z]*` //标签名
const qnameCapture = `((?:${ncname}\\:)?${ncname})` //标签名:标识，比如 span:xx
const startTagOpen = new RegExp(`^<${qnameCapture}`) //开始标签打开，比如 <div
const endTag = new RegExp(`^<\\/${qnameCapture}[^>]*>`) //结束标签，比如 </div>
const attribute = /^\s*([^\s"'<>\/=]+)(?:\s*(=)\s*(?:"([^"]*)"+|'([^']*)'+|([^\s"'=<>`]+)))?/
const startTagClose = /^\s*(\/?)>/ //开始标签闭合，即 >

// 解析 html 为 ast
export function parseHTML(html) {
    // 根元素、用于记录父子关系的栈
    let root, stack = []

    //主逻辑
    while (html) {
        let indexOf = html.indexOf('<')

        if (indexOf === 0) {
            // 开始标签
            let matchedStart = parseStartTag()
            if (matchedStart) {
                start(matchedStart.tagName, matchedStart.attrs)
                continue
            }

            // 结束标签
            let matchedEnd = html.match(endTag)
            if (matchedEnd) {
                advance(matchedEnd[0].length)
                end(matchedEnd[1])
            }

            continue
        }

        // 获取双标签中间的部分，即 <xxx>TARGET</xxx>
        if (indexOf > 0) {
            let innerText = html.substring(0, indexOf)
            advance(innerText.length)
            charts(innerText)
            // 简单的例子
            // <div>XXXXXX</div>
            //      XXXXXX</div>
            //      ######
            //            |
            //         indexOf
            //------------------------------------
            // 更复杂的例子
            // <x>111<y>222</y></x>
            //    111<y>222</y></x>
            //    ###
            //       |
            //    indexOf
        }
    }

    //-------------------------------------------------------------------------------------
    //-------- 一堆辅助函数  ---------------------------------------------------------------
    //-------------------------------------------------------------------------------------

    // 当解析到 <xxx key1=val1 key2=val2 ..> 时，将 { tag: xxx, attrs: [..], ..} 入栈
    function start(tagName, tagAttrs) {
        //console.log(`start tag: ${tagName}, attrs:`, tagAttrs)
        const element = {
            tag: tagName,
            attrs: tagAttrs,
            children: [],
            type: 1,
            parent: null
        }
        if (!root) {
            root = element
        }
        // currentParent = element
        stack.push(element)
    }

    // 当解析到 body<yyy>zzz</yyy></xxx> 时，将 { text: 'body', type: 3 } 添加到 xxx.children 中
    function charts(tagText) {
        //console.log(`tag text: ${JSON.stringify(tagText)}`)
        // tagText = tagText.replace(/^\s+/, '')
        if (tagText && stack.length > 0) {
            stack[stack.length - 1].children.push({
                text: tagText,
                type: 3
            })
        }
    }

    // 当解析到 </xxx> 时，将 xxx 取出，为其设置 parent 属性，并 xxx.parent.children.push(xxx)
    function end(tagName) {
        //console.log(`end tag: ${tagName}`)
        let element = stack.pop()
        if (element && stack.length > 0) {
            let parent = stack[stack.length - 1]
            element.parent = parent
            parent.children.push(element)
        }
    }

    // 解析开始标签
    // 定义在 parseHTML 函数中，好处：可以使用它的成员变量
    function parseStartTag() {
        let astNode = {
            tagName: '',
            attrs: []
        }

        //e.g. ['<div', 'div', index: 0, input: '<div id="app">hello, {{ msg }}</div>', groups: undefined]
        let matchedTagStart = html.match(startTagOpen)
        if (!matchedTagStart) {
            return
        }

        // 设置标签名
        astNode.tagName = matchedTagStart[1]
        // 删除解析出的部分
        advance(matchedTagStart[0].length)

        // 解析属性
        let cur_attr, startTagClosePart
        // 没有匹配到 '>' 并且 匹配到属性
        while (!(startTagClosePart = html.match(startTagClose)) && (cur_attr = html.match(attribute))) {
            // cur_attr 示例:
            //    [' class="box"', 'class', '=', 'box', undefined, undefined, 
            //     index: 0, input: ' class="box">hello, {{ msg }}</div>', groups: undefined]
            astNode.attrs.push({
                name: cur_attr[1],
                value: cur_attr[3] || cur_attr[4] || cur_attr[5]
            })
            advance(cur_attr[0].length)
        }

        // 解析起始标签结尾，即 >
        if (startTagClosePart) {
            advance(startTagClosePart[0].length)
            return astNode
        }
    }

    // 删除前 length 个字符
    function advance(length) {
        html = html.substring(length)
    }

    //-------------------------------------------------------------------------------------

    // 函数返回
    return root
}