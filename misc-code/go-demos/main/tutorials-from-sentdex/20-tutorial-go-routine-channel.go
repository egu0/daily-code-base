package main

import "fmt"

func foo(chanVal chan int, num int) {
	chanVal <- num * 5 // write channel
}

func main() {
	fooVal := make(chan int) // create an int type channel

	go foo(fooVal, 5)
	go foo(fooVal, 3)

	//n1 := <-fooVal
	//n2 := <-fooVal

	n1, n2 := <-fooVal, <-fooVal // read channel

	fmt.Println(n1, n2)
}
