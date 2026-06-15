let callbacks = []
let setUp = false

function flush() {
    setUp = false
    const copies = callbacks.slice(0)
    callbacks.length = 0
    for (let i = 0; i < copies.length; i++) {
        copies[i]()
    }
}

export function nextTick(cb) {
    callbacks.push(cb)
    if (!setUp) {
        timerFunc()
        setUp = true
    }
}

//-----------------------------------------------------
//确定使用哪种异步方式
let timerFunc
if (Promise) {
    timerFunc = () => {
        Promise.resolve().then(flush)
    }
} else if (MutationObserver) { //h5
    let observer = MutationObserver(flush)
    let textNode = document.createTextNode(1)
    observer.observe(textNode, { CharacterData: true })
    timerFunc = () => {
        textNode.textContent = 2
    }
} else if (setImmediate) {
    timerFunc = () => {
        setImmediate(flush)
    }
}
