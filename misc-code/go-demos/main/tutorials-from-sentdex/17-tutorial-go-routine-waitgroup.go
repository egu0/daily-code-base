package main

import (
	"fmt"
	"sync"
	"time"
)

var wg sync.WaitGroup

func say2(sentence string, times int) {
	for i := 0; i < times; i++ {
		fmt.Println(sentence)
		time.Sleep(time.Millisecond * 100)
	}
	wg.Done() // Done decrements the WaitGroup counter by one.
}

func main() {
	wg.Add(1) // Add adds delta to the WaitGroup counter.
	go say2("Hey", 6)
	wg.Add(1)
	go say2("There", 3)

	wg.Wait() // Wait blocks until the WaitGroup counter is zero.

	wg.Add(1)
	go say2("Hi", 3)
	wg.Wait()

	// 总结：使用 计数器 表示主线程以外的其他线程的执行情况，在计数器到达 0 之前，主线程需要等待
	// 类似于 juc 中的 CountDownLatch 用法
}
