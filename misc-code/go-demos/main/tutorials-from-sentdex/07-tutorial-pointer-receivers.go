package main

import "fmt"

const sixteenBitMax2 float64 = 65535
const kmhMultiple2 float64 = 1.60934

type car3 struct {
	gasPedal      uint16
	brakePedal    uint16
	steeringWheel int16
	topSpeedKmh   float64
}

// value receiver
func (c car3) kmh() float64 {
	c.topSpeedKmh = 400
	fmt.Println("value receiver kmh(), topSpeedKmh is", c.topSpeedKmh)
	// 👉这里的数据即 c 是原数据的拷贝，修改 c 不会影响原数据
	return float64(c.gasPedal) * (c.topSpeedKmh / sixteenBitMax2)
}
func (c car3) mph() float64 {
	fmt.Println("value receiver mph(), topSpeedKmh is", c.topSpeedKmh)
	return float64(c.gasPedal) * (c.topSpeedKmh / sixteenBitMax2 / kmhMultiple2)
}

// pointer receiver
func (c *car3) newTopSpeed(newSpeed float64) {
	// 👉这里的 c 是原数据，修改它会影响原数据
	c.topSpeedKmh = newSpeed
	fmt.Println("pointer receiver kmh(), after modifying, topSpeedKmh is", c.topSpeedKmh)
}

func main() {
	aCar := car3{
		gasPedal:    41234,
		topSpeedKmh: 224.0,
	}

	fmt.Println(aCar.gasPedal)
	fmt.Println(aCar.kmh())
	fmt.Println(aCar.mph())

	aCar.newTopSpeed(500) // use [pointer receiver] to modify
	fmt.Println(aCar.kmh())
	fmt.Println(aCar.mph())
}

/*
输出及分析
-----------
value receiver kmh(), topSpeedKmh is 400                          修改拷贝数据中的值
251.6762035553521
value receiver mph(), topSpeedKmh is 224                          原数据中的值
87.57544955758087
pointer receiver kmh(), after modifying, topSpeedKmh is 500       通过 pointer receiver 修改原数据中的值
value receiver kmh(), topSpeedKmh is 400                          修改结构数据中的值
251.6762035553521
value receiver mph(), topSpeedKmh is 500                          获得原数据中 [通过 pointer receiver 修改] 后的值
195.4809141910287
*/
