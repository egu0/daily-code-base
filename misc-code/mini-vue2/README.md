# Vue2 from scratch

[【2023前端合集】手写Vue2框架教程，58集精讲源码解析！](https://www.bilibili.com/video/BV1Jv4y1q7nE)

node 版本: v16

## 1.项目搭建

```sh
npm init -y

npm install @babel/preset-env @babel/core rollup rollup-plugin-babel rollup-plugin-serve -D
```

`src/index.js`

```js
function Vue() {}

export default Vue;
```

`rollup.config.js`

```js
import babel from "rollup-plugin-babel";
import serve from "rollup-plugin-serve";

export default {
  input: "./src/index.js",
  output: {
    file: "dist/vue.js",
    format: "umd",
    name: "Vue",
    sourcemap: true,
  },
  plugins: [
    babel({
      exclude: "node_modules/**",
    }),
    serve({
      port: 3000,
      contentBase: "",
      openPage: "/index.html",
    }),
  ],
};
```

`.babelrc`

```json
{
  "presets": ["@babel/preset-env"]
}
```

`index.html`

```html
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Document</title>
  </head>

  <body>
    <div id="app">hello</div>
  </body>

  <script src="dist/vue.js"></script>
</html>
```

在 `package.json` 中配置 rollup 命令

```json
  "scripts": {
    "dev": "rollup -c rollup.config.js -w"
  },
```

执行命令

```bash
npm run dev
```

## 2.处理 data 属性

1. 模块化。[checkpoint](https://gitee.com/egu0/mini-vue2/commit/5e7c82f8f4cf790c567518b75ed226531df57b8d)

2. 使用 `Object.defineProperty` 函数为对象中的第一层数据添加观测。比如 `{x: 1, y: 2}`。[checkpoint](https://gitee.com/egu0/mini-vue2/commit/6ca5928ea989089de5f7ea9ab534bfa845063add)
3. 为嵌套对象中的数据添加观测。比如 `{a: 1, b: {c: 2, d: 3}}`。[checkpoint](https://gitee.com/egu0/mini-vue2/commit/15847655fa667fc31535f1adcafa2f3f96bd48b5)
4. 为通过 `=` 添加的新对象设置观测。比如 `vm._data.b = {e: 4}`。[checkpoint](https://gitee.com/egu0/mini-vue2/commit/957c8bd3de74180204d6d34120e0949ae370cc5c)
5. 通过代理模式（利用原型链）劫持数组的方法。比如为 `vm._data.scores` 数组的 push 方法添加自定义逻辑。效果：可在控制台看到 `vm._data.scores` 数组原型的方法为所定义的目标方法。[checkpoint](https://gitee.com/egu0/mini-vue2/commit/35be0b1a8e4b8aa1ae77fb3f19f6d7d88be4d7ab)
6. 为数组中的对象中的元素添加观测。比如`{arr: [ { z: 6 } ]}`。效果：`z` 被添加了观测。[checkpoint](https://gitee.com/egu0/mini-vue2/commit/7f4648f2edee78805379634e0f75d1aad444cd2d)
7. 为通过 `push,unshift,splice` 方法添加的元素添加观测。[checkpoint](https://gitee.com/egu0/mini-vue2/commit/34cc985515cb4ad5a369742423e2ea03b628d24c)
8. 将 `_data` 中的属性代理到 vm 实例。[checkpoint](https://gitee.com/egu0/mini-vue2/commit/7cf7a457d8ddebe03878615cbceaa3b2ce7c732d)

## 3.将 thml 模板解析为 ast

### 1.获取 html

初次渲染逻辑：

1. 初始化数据
2. 编译模板
3. render 虚拟节点
4. 转为真实 DOM 并放入页面中

编译模板的流程：（[source](https://v2.vuejs.org/v2/guide/instance.html#Lifecycle-Diagram)）

![pic](https://s2.loli.net/2024/04/29/HaNqQBP8UMLbztl.png)

简述：

1. 必须包含 `el` 属性（要么显式指定，要么从 `$mount` 方法参数处获取）；
2. 如果指定了 `template` 属性，那么优先编译 `template` 属性指定的模板；如果未指定 `template` 属性，那么编译 `el` 元素

### 2.了解 AST

AST：抽象语法树，[Wikipedia](https://en.wikipedia.org/wiki/Abstract_syntax_tree)

示例：将一段 html 转为对应的 ast 对象

```html
<div id="app">
  hello <span>jetson</span> <br />
  <p>how are you</p>
</div>
```

结果对象

```json
{
  "tag": "div",
  "attrs": {
    "id": "app"
  },
  "children": [
    {
      "tag": null,
      "text": "hello"
    },
    {
      "tag": "span",
      "text": "jetson"
    },
    {
      "tag": "br"
    },
    {
      "tag": "p",
      "children": [
        {
          "tag": null,
          "text": "how are you"
        }
      ]
    }
  ]
}
```

### 3.解析 html

将 html 解析为 AST 对象

缺点：不能解析单标签元素，比如 img，link

## 4.将 ast 转换为 render 函数

### 1.render 函数字符串

将 ast 变为 render 函数字符串

示例

1、准备 html

```vue
<div
  id="app"
  class="box"
  style="color: red; margin-top: 30px;"
> hello, {{ msg}}, {{greeting }}
    <h1>are you good? </h1>
</div>
```

2、将 html 转为 ast。省略

3、将 ast 转为 render 函数字符串

```js
_c(
  "div",
  { id: "app", class: "box", style: { color: "red", "margin-top": "30px" } },
  _v("hello, " + _s(msg) + ", " + _s(greeting) + "\n        "),
  _c("h1", null, _v("are you good? ")),
);
```

- `_c(tagName, attrs, ...args)` 支持多个参数
- `_v(text)` 只接收一个参数
- `_s(varName)` 只接收一个参数

注意：`4.2` 提交时 `_v` 逻辑有点问题（`src/compile/generate.js`），应该为

```js
        if (lastIndex < text.length) {
            tokens.push(JSON.stringify(text.slice(lastIndex)))
        }

        return `_v(${tokens.join(' + ')})`// + 而不是 , 因为 _v 只接受一个参数
    }
}
```

修正：`charts` 函数无需对 tagText 进行处理

```js
function charts(tagText) {
  // tagText = tagText.replace(/^\s+/, '')
  if (tagText && stack.length > 0) {
    stack[stack.length - 1].children.push({
      text: tagText,
      type: 3,
    });
  }
}
```

### 2.render 函数

将 render 函数字符串转为 render 函数

```js
export function compileToFunction(el) {
  console.log("html:", el);

  // 1.将 html 解析为 ast 语法树
  let ast = parseHTML(el);
  console.log("ast:", ast);

  // 2.将 ast 语法树解析为 render 函数字符串
  let render_string = parseTagNode(ast);
  console.log("render string:", render_string);

  // 3.将 render 函数字符串转为 render 函数
  let render = new Function(`with(this) { return ${render_string} }`);
  console.log("render:", render);
}
```

## 5.转为真实 DOM

### 1.虚拟 DOM

将 render 函数转为虚拟 DOM

### 2.真实 DOM

将虚拟 DOM 转为真实 DOM，并替换原 DOM

效果：基本的模板渲染

## 总结渲染流程

1. **数据初始化**
2. **将 html 模板解析为 ast 对象**
3. **将 ast 对象转换为 render 函数**：先将 ast 对象转换为 render 函数字符串，再将 render 字符串转换为 render 函数）
4. **根据 render 函数获取虚拟 dom，将虚拟 dom 转换为真实 dom 并替换原 dom**

## 6.生命周期

### 1.合并选项到 `vm.$options`

具体的，合并的内容是：

1. 通过 `Vue.Mixin()` 添加的选项
2. 通过 `new Vue({..})` 创建实例时添加的选项

### 2.调用生命周期钩子

## 7.watch

### 1.Watcher类

[source](https://www.bilibili.com/video/BV1Jv4y1q7nE?p=27)

### 2.Dep 类

dep 实例与 data 中的属性一一对应

watcher 实例与属性的关系为 `N:1`，表示某个属性被用了几次就有几个对应的 watcher 实例

### 3.收集对象的依赖

收集对象的依赖、对象的自动更新

### 4.dep和watcher多对多

注意：额外做了 `dep.subs` 的去重

### 5.收集数组的依赖

收集数组的依赖、数组的自动更新

## 8.nextTick

问题：数据更新多次，同时也进行了多次重新渲染，造成性能消耗

希望的结果：只更新一次

### 1.用队列处理watcher

内容：进行属性的 set 操作，与属性关联的 dep 执行了 dep.notify 方法，进而调用了 watcher.update 方法；在 watcher.js 中，将 `watcher` 去重后放到一个队列中，同时进行防抖处理，利用 setTimeout 将 `vm._update(vm._render(el))` 推迟执行

`observe/watcher.js` 部分代码

```js
//...

let watcherQueue = [];
let hittedWatchers = {}; // 键为 watcher 的 id，值为布尔表示是否存在
let setUp = false;

function watcherEnqueue(watcher) {
  let id = watcher.id;

  //去重。通过 hittedWatchers 集合记录已经加入的 watcher
  if (hittedWatchers[id] == null) {
    watcherQueue.push(watcher);
    hittedWatchers[id] = true;

    //防抖。setUp 表示是否已设置异步处理钩子
    if (!setUp) {
      setTimeout(() => {
        watcherQueue.forEach((watcher) => {
          watcher.run();
        });
        // 状态重置
        watcherQueue = [];
        hittedWatchers = {};
        setUp = false;
      }, 0);
    }
    setUp = true;
  }
}
```

### 2.实现 nextTick

[nextTick](https://stackoverflow.com/a/47636157/23681037)

### 3.实现 updated 钩子

## 9.watch

watch 使用的四种方式

```html
<body>
  <div id="app">{{name}}</div>
  <script src="/node_modules/vue/dist/vue.js"></script>
  <script>
    let vm = new Vue({
      el: "#app",
      data: {
        name: "eugene",
      },
      methods: {
        name_watch_fn: (newVal, oldVal) => {
          console.log(oldVal, "->", newVal);
        },
      },
      watch: {
        //1.
        name(newVal, oldVal) {
          console.log(oldVal, "->", newVal);
        },

        //2.
        // 'name': [
        //     (newVal, oldVal) => {
        //         console.log('1', oldVal, '->', newVal)
        //     },
        //     (newVal, oldVal) => {
        //         console.log('2', oldVal, '->', newVal)
        //     }
        // ]

        //3.
        // 'name': {
        //     handler(newVal, oldVal) {
        //         console.log(oldVal, '->', newVal)
        //     }
        // }

        //4.
        // 'name': 'name_watch_fn'
      },
    });

    setTimeout(() => {
      vm.name = "egu0";
    }, 500);
  </script>
</body>
```

### 9.1实现watch功能

初始化 watch：创建 watcher 实例，监听数据的变化

## 10.diff 算法

`patch(oldVNode, newVNode)` 方法

- 作用：将虚拟 dom 转为真实 dom，并用得到的真实 dom 替换原真实 dom

- 调用：第一次调用时，`oldVNode` 是真实 dom，`newVNode` 是虚拟 dom；之后再调用时，两个参数都是虚拟 dom
- 优化（`diff 算法`）：因为虚拟 dom 和真实 dom 具有映射关系，所以当虚拟 dom 变化时，可以只比较它变化的内容从而只更新受影响的部分真实 dom。相比于每次虚拟 dom 变化都重新从头创建真实 dom，利用 diff 算法提高了资源利用率和程序性能

面试题：`key` 的选择，[参考](https://zhuanlan.zhihu.com/p/124019708)

## 11.computed

### 1.实现 computed 代理

用 defineProperty 为 comptuted 属性实现代理功能

### 2.实现缓存机制

为每个计算属性创建一个对应的 Watcher 实例，实现缓存机制

### 3.实现 computed 属性的同步更新

## 总结：Watcher 四用

- 用于初次渲染
- watch 功能
- nextTick 功能
- computed 功能

## 12.自定义组件

### 1.component 和 extend 两个静态方法

`Vue.component` 依赖于 `Vue.extend`

`Vue.extend` 方法：创建 Vue 子类

### 2.创建自定义组件的虚拟 dom

...
