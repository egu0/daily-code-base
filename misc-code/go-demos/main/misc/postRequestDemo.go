package main

import (
	"bytes"
	"fmt"
	"io/ioutil"
	"net/http"
)

func main() {
	// Create a new HTTP request with the POST method and URL
	req, err := http.NewRequest("POST",
		// "http://localhost:8081/",
		"https://jsonplaceholder.typicode.com/posts",
		bytes.NewBuffer([]byte(`{"key": "VVV"}`)),
	)
	if err != nil {
		// Handle error
		fmt.Println(err)
		return
	}

	// Set the Content-Type header to application/json
	req.Header.Set("Content-Type", "application/json")

	// Send the HTTP request and get the response
	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		// Handle error
		fmt.Println(err)
		return
	}
	defer resp.Body.Close()

	// Print the response status code and body
	fmt.Println("Response Status:", resp.Status)
	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		// Handle error
		fmt.Println(err)
		return
	}
	fmt.Println("Response Body:", string(body))
}
