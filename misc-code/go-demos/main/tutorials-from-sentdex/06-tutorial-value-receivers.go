package main

import "fmt"

const sixteenBitMax float64 = 65535
const kmhMultiple float64 = 1.60934

type car2 struct {
	gasPedal      uint16 // 0 - 65535
	brakePedal    uint16
	steeringWheel int16
	topSpeedKmh   float64
}

// value receiver
func (c car2) kmh() float64 {
	return float64(c.gasPedal) * (c.topSpeedKmh / sixteenBitMax)
}
func (c car2) mph() float64 {
	return float64(c.gasPedal) * (c.topSpeedKmh / sixteenBitMax / kmhMultiple)
}

func main() {
	aCar := car2{
		gasPedal:      41234,
		brakePedal:    19181,
		steeringWheel: 3881,
		topSpeedKmh:   224.0,
	}

	fmt.Println(aCar.gasPedal)
	fmt.Println(aCar.kmh())
	fmt.Println(aCar.mph())
}
