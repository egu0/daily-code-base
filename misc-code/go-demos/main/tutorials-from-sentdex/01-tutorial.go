package main

import (
	"fmt"
	"math"
	"math/rand"
)

// firstly install 'godoc': go install golang.org/x/tools/cmd/godoc@latest
// then read manual in terminal:
// 1.`go doc fmt.Println`
// 2.`go doc math/rand.Intn`

func mathSqrt() {
	fmt.Println("the square root of 4 is", math.Sqrt(4))
}

func main() {
	mathSqrt()
	fmt.Println("a number from 0-99 is", rand.Intn(100))
}
