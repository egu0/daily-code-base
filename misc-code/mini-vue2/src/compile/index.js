import { parseTagNode } from "./generate"
import { parseHTML } from "./parseAst"

export function compileToFunction(el) {
    // console.log('html:', el)

    // 1.将 html 解析为 ast 语法树
    let ast = parseHTML(el)
    // console.log('ast:', ast)

    // 2.将 ast 语法树解析为 render 函数字符串
    let render_string = parseTagNode(ast)
    // console.log('render string:', render_string)

    // 3.将 render 函数字符串转为 render 函数
    let render = new Function(`with(this) { return ${render_string} }`)
    // console.log('render:', render)

    return render
}