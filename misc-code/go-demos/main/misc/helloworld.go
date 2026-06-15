package main

import "fmt"

func basicDataTypes() {
	var b bool = true
	var s string = "hello"
	var i int = 42 // int32
	var f float64 = 3.14
	var by byte = 'a'
	var r rune = '世'
	var c complex128 = 1 + 2i

	fmt.Println(b)
	fmt.Println(s)
	fmt.Println(i)
	fmt.Println(f)
	fmt.Println(by)
	fmt.Println(r)
	fmt.Println(c)
}

func arrayDemo() {
	var c [5]int
	fmt.Println(c)

	fmt.Println("-----")
	b := [5]int{1, 2, 3, 4, 5}
	fmt.Println(b)

	fmt.Println("-----")
	var a [5]int
	a[0] = 1
	a[1] = 2
	a[2] = 3
	a[3] = 4
	a[4] = 5
	fmt.Println(a)

	fmt.Println("-----")
	s := make([]int, 0, 5) // 自动类型推断
	s = append(s, 1)
	s = append(s, 2)
	s = append(s, 3)
	s = append(s, 4)
	s = append(s, 5)
	s = append(s, 6)
	fmt.Println(s)

	fmt.Println("-----")
	q := []int{1, 2, 3}
	q = append(q, 4)
	q = append(q, 5)
	fmt.Println(q)
	fmt.Println(q[2:4])

	fmt.Println("-----")
	for i, v := range s {
		fmt.Printf("Index: %d, Value: %d\n", i, v)
	}

	fmt.Println("-----")
	for i := 0; i < len(s); i++ {
		fmt.Println(s[i])
	}
	fmt.Println("-----")
}

func main() {
	//field()
	//var sum = add(100, 200)
	//var aaa = "hello"
	//fmt.Printf("%T\n", aaa)
	//basicDataTypes()
	//arrayDemo()
	fmt.Printf("Hello world!")
}
