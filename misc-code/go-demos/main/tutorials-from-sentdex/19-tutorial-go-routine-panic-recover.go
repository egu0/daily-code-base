package main

import (
	"fmt"
	"sync"
	"time"
)

var wg4 sync.WaitGroup

func cleanUp() {
	defer wg4.Done()

	// Executing a call to recover inside a deferred function (but not any function called by it)
	// stops the panicking sequence by restoring normal execution and retrieves the error value passed
	// to the call of panic.
	r := recover()
	if r != nil {
		fmt.Println("Recover is cleanup:", r)
	}
}

func say4(sentence string, times int) {
	defer cleanUp() // 调用时机：surrounding function returns or the corresponding goroutine is panicking

	if times < 3 {
		times = 3
	}

	for i := 0; i < times; i++ {
		if i == 2 {
			panic("oh dear, i is 2") // 手动 panic，理解为抛出异常？
		}
		fmt.Println(sentence)
		time.Sleep(time.Millisecond * 100)
	}
}

func main() {
	wg4.Add(1)
	go say4("Hey", 6)
	wg4.Add(1)
	go say4("There", 6)
	wg4.Wait()

	wg4.Add(1)
	go say4("Hi", 3)
	wg4.Wait()
}
