package main

import (
	"fyne.io/fyne/v2/app"
	"fyne.io/fyne/v2/container"
	"fyne.io/fyne/v2/widget"
	"gitee.com/egu0/goexamples/v2"
	"gitee.com/egu0/goexamples/v2/mmath"
)

func main() {
	println(goexamples.Greeting("Eugene"))
	println(goexamples.AddTest())
	println(mmath.Add(1,2))

	// fyne
	a := app.New()
	w := a.NewWindow("Hello")

	hello := widget.NewLabel("Hello Fyne!")
	w.SetContent(container.NewVBox(
		hello,
		widget.NewButton("Hi!", func() {
			hello.SetText("Welcome :)")
		}),
	))

	w.ShowAndRun()
}