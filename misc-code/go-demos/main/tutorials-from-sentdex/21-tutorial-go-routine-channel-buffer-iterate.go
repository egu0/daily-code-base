package main

import (
	"fmt"
	"sync"
)

var wg5 sync.WaitGroup

func foo2(chanVal chan int, num int) {
	defer wg5.Done()
	chanVal <- num * 5 // send data into channel
}

func main() {
	fooVal := make(chan int, 10) // buffered 10 int variables

	for i := 0; i < 10; i++ {
		wg5.Add(1)
		go foo2(fooVal, i)
	}
	wg5.Wait()

	// Close a channel only when it is essential
	//  to inform the receiving goroutines that all data has been transmitted.
	// We can easily close a channel in golang by using the close() function.
	// This built-in function sets a flag indicating that no additional data will be sent to this channel.
	close(fooVal) // close channel

	for i := range fooVal { // iterate over channel
		fmt.Println(i)
	}
}
