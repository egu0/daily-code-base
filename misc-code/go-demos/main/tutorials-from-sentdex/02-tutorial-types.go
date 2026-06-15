package main

import "fmt"

func add(x, y float64) float64 {
	return x + y
}

func multiple(x string, y string) (string, string) {
	return x, y
}

func main() {
	//var n1 float64 = 3.2
	//var n2 float64 = 3.3

	//var n1, n2 float64 = 1.1, 1.2

	n1, n2 := 1.3, 1.4 // auto type
	fmt.Println(add(n1, n2))

	fmt.Println(multiple("Hey", "there"))
}
