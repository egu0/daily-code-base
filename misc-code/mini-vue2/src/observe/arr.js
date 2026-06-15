
//获取 Array 的原型对象：是所有 Array 对象的模板，
// 主要包含了很多无状态的处理函数，比如 push, pop
let oldArrayProto = Array.prototype

//创建代理对象，它的原型是 oldArrayProto，即 arrayProxyProto.__proto__ = oldArrayProto
export let arrayProxyProto = Object.create(oldArrayProto)

//代理模式
//原型链：要处理arr对象  ==proto==>  arrayProxyProto对象  ==proto==>  Array.prototype
//解释：arrayProxyProxy 是一个代理对象，需要为它“重写目标方法”以添加观测逻辑

//需要劫持的方法
let targetMethods = [
    "push",
    "pop",
    "unshift",
    "shift",
    "splice"
]

targetMethods.forEach(methodName => {
    //“重写目标方法”
    arrayProxyProto[methodName] = function (...args) {
        //调用“原方法”
        let result = oldArrayProto[methodName].apply(this, args)

        //为【通过 "methodName" 方法添加的数据】添加观测
        let inserted_arr
        switch (methodName) {
            case "push":
            case "unshift":
                inserted_arr = args
                break
            case "splice":
                //splice(start: number, deleteCount: number, ...items: any[])
                inserted_arr = args.splice(2)
                break
        }
        let observer = this.__ob__
        observer.walk_array(inserted_arr)
        observer.dep.notify()//重新渲染

        return result
    }
})

