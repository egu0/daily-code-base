package main

import (
	"fmt"
	"net/http"
)

func indexHandler(w http.ResponseWriter, r *http.Request) {
	//fmt.Fprintf(w, "<h1>Hey there</h1>")
	//fmt.Fprintf(w, "<p>Whoa, Go is neat!</p>")
	//fmt.Fprintf(w, "<p>...and simple!</p>")

	fmt.Fprint(w, `<h1>Hey there</h1>
<p>Whoa, Go is neat!</p>
<p>...and simple!</p>
`)
}

func main() {
	http.HandleFunc("/", indexHandler)
	http.ListenAndServe(":8000", nil)
}
