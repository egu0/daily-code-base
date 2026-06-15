[express - npm (npmjs.com)](https://www.npmjs.com/package/express)

引入依赖

```json
  "dependencies": {
    "@types/express": "^4.17.21",
    "express": "^4.19.1"
  }
```

构造目录

```
.
├── dist                     编译输出目录
├── package.json
├── src                      源码目录
│   └── index.ts             一个 ts 文件
└── tsconfig.json            ts 配置
```

编译

```
tsc -w
```

下载 `nodemon` 工具，运行编译出来的 `dist/index.js`（监听指定 js 文件变化，如果发生了变化则再次执行）

```
nodemon dist/index.js
```

配置文件 `tsconfig.json`

```json
{
    // 指定要编译的目录
    "include": [
        "src"
    ],
    "compilerOptions": {
        "paths": {
            // This is solely to stop a bug with @types/node as of 12/15/2023
            // https://github.com/DefinitelyTyped/DefinitelyTyped/discussions/67406#discussioncomment-7866621
            "undici-types": [
                "./node_modules/undici-types/index.d.ts"
            ],
        },
        /* Modules */
        "target": "es2016", // 设置生成的 JavaScript 的 JavaScript 语言版本，并包含兼容的库声明。
        "module": "es2015", // 模块化
        "rootDir": "./src", // Where to compile from
        "outDir": "./dist", // Where to compile to
        /* JavaScript Support */
        "allowJs": true, // Allow JavaScript files to be compiled
        "checkJs": true, // Type check JavaScript files and report errors
        /* Emit */
        "sourceMap": true, // Create source map files for emitted JavaScript files (good for debugging)
        "removeComments": true, // Don't emit comments
        "strict": true, //严格模式
        "noImplicitAny": true, // 不包含隐式 any。当没有显示指定类型且 ts 推断出 any 类型时进行错误提示
        "strictNullChecks": true, // 严格空值检查。ts 进行空值检查，如果变量可能为空且被错误使用时进行提示
    },
}
```

**遇到的错误**

```
[11:56:02 PM] File change detected. Starting incremental compilation...

node_modules/@types/node/globals.d.ts:6:76 - error TS2792: Cannot find module 'undici-types'. Did you mean to set the 'moduleResolution' option to 'nodenext', or to add aliases to the 'paths' option?

6 type _Request = typeof globalThis extends { onmessage: any } ? {} : import("undici-types").Request;
                                                                             ~~~~~~~~~~~~~~

node_modules/@types/node/globals.d.ts:7:77 - error TS2792: Cannot find module 'undici-types'. Did you mean to set the 'moduleResolution' option to 'nodenext', or to add aliases to the 'paths' option?

...
```

解决：[[@types/node\] Compiler error: Cannot find module 'undici-types' · DefinitelyTyped/DefinitelyTyped · Discussion #67406 (github.com)](https://github.com/DefinitelyTyped/DefinitelyTyped/discussions/67406#discussioncomment-7866621)

