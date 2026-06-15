package main

import (
	"fmt"
	"io/ioutil"
	"net/http"
)

func main() {
	resp, _ := http.Get("https://www.freebuf.com/feed")
	bytes, _ := ioutil.ReadAll(resp.Body)
	bodyString := string(bytes)
	fmt.Println(bodyString)
	_ = resp.Body.Close() // ignore the return value
}
