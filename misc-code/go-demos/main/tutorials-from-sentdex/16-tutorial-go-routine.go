package main

import (
	"fmt"
	"time"
)

func say(sentence string, times int) {
	for i := 0; i < times; i++ {
		fmt.Println(sentence)
		time.Sleep(time.Millisecond * 100)
	}
}

func main() {
	// A "go" statement starts the execution of a function call as
	// an independent concurrent thread of control,
	// or goroutine, within the same address space.
	go say("Hey", 6) // 将此方法交给 goroutine 执行
	say("There", 3)  // 如果这个方法也通过 goroutine 调用，那么主程序执行完后会结束 goroutine 的执行

	// 可简单理解：go 关键词创建并使用一个新的子线程调用指定的方法，主程序运行结束后会终止子线程的运行
	// 产生的问题：如何等到 goroutine 执行结束后再终止主程序
}
