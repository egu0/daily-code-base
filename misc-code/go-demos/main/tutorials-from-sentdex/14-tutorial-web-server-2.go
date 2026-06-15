package main

import (
	"fmt"
	"html/template"
	"net/http"
)

func indexHandler2(w http.ResponseWriter, r *http.Request) {
	fmt.Fprint(w, `<h1>Hey there</h1>`)
}

type NewsAggPage2 struct {
	Title string
	News  string
}

func newsAggHandler2(w http.ResponseWriter, r *http.Request) {
	p := NewsAggPage2{Title: "Amazing News Aggregator", News: "some news"}
	t, _ := template.ParseFiles("basictemplating.html")
	t.Execute(w, p)
}

func main() {
	http.HandleFunc("/", indexHandler2)
	http.HandleFunc("/agg/", newsAggHandler2)
	http.ListenAndServe(":8000", nil)
}
