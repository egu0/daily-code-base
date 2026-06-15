let id = 0

class Dep {
    constructor() {
        this.subs = []
        this.subIds = new Set()
        this.id = id++
    }

    depend() {
        // 双向记忆
        let watcher = Dep.target
        watcher.addDep(this)
        this.addSub(watcher)
    }

    addSub(watcher) {
        if (watcher && !this.subIds.has(watcher.id)) {
            this.subIds.add(watcher.id)
            this.subs.push(watcher)
        }
    }

    // 属性更新时通知所有 watcher
    notify() {
        this.subs.forEach(watcher => {
            watcher.update()
        })
    }
}

export default Dep

//---------------------------------

Dep.target = null
let stack = []
export function pushTarget(watcher) {
    Dep.target = watcher
    stack.push(watcher)
}
export function popTarget() {
    Dep.target = null
    stack.pop()
    if (stack.length > 0) {
        Dep.target = stack[stack.length - 1]
    }
}