package main

import "fmt"

func main() {
	var x int = 15
	b := &x         // memory address
	fmt.Println(b)  // 0x1400009c018
	fmt.Println(*b) // 15

	*b = 5
	fmt.Println(x) // 5

	*b = *b * *b
	fmt.Println(x)  // 25
	fmt.Println(*b) // 25
}
