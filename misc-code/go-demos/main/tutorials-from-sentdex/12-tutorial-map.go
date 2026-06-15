package main

import "fmt"

func main() {
	var grades map[string]float64     // define a map variable
	grades = make(map[string]float64) // allocate and initialize an object, like keyword `new`
	grades["Timmy"] = 43
	grades["Jess"] = 83
	grades["Sam"] = 91

	fmt.Println(grades)

	// get value by key
	gradeOfTim := grades["Timmy"]
	fmt.Println(gradeOfTim)

	// iterate over grades
	for k, v := range grades {
		fmt.Println(k, ":", v)
	}

	// delete key-value pair by key
	delete(grades, "Timmy")

	fmt.Println(grades)
}
