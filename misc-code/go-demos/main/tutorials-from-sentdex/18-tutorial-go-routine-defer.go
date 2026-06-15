package main

import (
	"fmt"
	"sync"
	"time"
)

var wg2 sync.WaitGroup

func say3(sentence string, times int) {
	defer wg2.Done() // 使用 defer 推迟执行，执行时机：调用者函数执行完 或相应的 goroutine 处于 panic 状态

	for i := 0; i < times; i++ {
		fmt.Println(sentence)
		time.Sleep(time.Millisecond * 100)
	}
}

func main() {
	wg2.Add(1)
	go say3("Hey", 6)
	wg2.Add(1)
	go say3("There", 3)
	wg2.Wait()

	wg2.Add(1)
	go say3("Hi", 3)
	wg2.Wait()
}
