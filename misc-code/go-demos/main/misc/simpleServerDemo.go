package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
)

type Message struct {
	Text string `json:"text"`
}

func main() {
	http.HandleFunc("/",
		func(responseWriter http.ResponseWriter, request *http.Request) {
			reqUrl := request.RequestURI
			reqMethod := request.Method
			clientHost := request.Host
			reqProto := request.Proto
			fmt.Printf("@ -> %8s [%20s] -> %s %s \n", reqProto, clientHost, reqMethod, reqUrl)

			// Create a new message
			message := Message{Text: "Hello, world!"}

			// Encode the message as JSON
			jsonBytes, err := json.Marshal(message)
			if err != nil {
				http.Error(responseWriter, err.Error(), http.StatusInternalServerError)
				return
			}

			// Set the Content-Type header to application/json
			responseWriter.Header().Set("Content-Type", "application/json")

			// Write the JSON response
			writeNo, err := responseWriter.Write(jsonBytes)
			if err != nil {
				return
			}
			fmt.Printf("@ <- written %d bytes into response\n", writeNo)
		})

	println("Server started at port 8081.")
	log.Fatal(http.ListenAndServe(":8081", nil))
}
