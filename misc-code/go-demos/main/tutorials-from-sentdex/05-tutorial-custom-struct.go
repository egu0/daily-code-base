package main

import "fmt"

type car struct {
	gasPedal      uint16 // 0 - 65535
	brakePedal    uint16
	steeringWheel int16
	topSpeedKmh   float64
}

func main() {
	aCar := car{
		gasPedal:      41234,
		brakePedal:    19181,
		steeringWheel: 3881,
		topSpeedKmh:   224.0,
	}

	bCar := car{41234, 19181,
		3881, 224.0}

	fmt.Println(aCar)
	fmt.Println(bCar.gasPedal)
}
