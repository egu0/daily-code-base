package main

import "fmt"

func main() {
	//for i := 0; i < 10; i++ {
	//	fmt.Println(i)
	//}

	//for a := 0; a < 20; {
	//	fmt.Println(a)
	//	a++
	//}

	//x := 0
	//for ; x < 25; x += 6 {
	//	fmt.Println(x)
	//}

	//z := 0
	//for z < 4 {
	//	fmt.Println(z)
	//	z += 2
	//}

	//for {
	//	fmt.Println("Do stuff")
	//}

	// iterate over the array
	for _, str := range []string{"Python", "Go", "Rust"} {
		fmt.Println(str)
	}
}
