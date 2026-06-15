package main

import (
	"encoding/xml"
	"fmt"
	"html/template"
	"io/ioutil"
	"net/http"
)

func indexHandler3(w http.ResponseWriter, r *http.Request) {
	fmt.Fprint(w, `<h1>Hey there</h1>`)
}

type ZhiHuDailyNews struct {
	Titles       []string `xml:"channel>item>title"`
	Links        []string `xml:"channel>item>link"`
	Descriptions []string `xml:"channel>item>description"`
}

type ZhiHuNewsInfo struct {
	Link        string
	Description string
}

type NewsAggPage3 struct {
	Title    string
	NewsInfo map[string]ZhiHuNewsInfo
}

func newsAggHandler3(w http.ResponseWriter, r *http.Request) {
	resp, _ := http.Get("https://www.zhihu.com/rss")
	bytes, _ := ioutil.ReadAll(resp.Body)
	_ = resp.Body.Close()

	var zhiHuDailyNews ZhiHuDailyNews
	xml.Unmarshal(bytes, &zhiHuDailyNews)

	zhiHuNewsInfoMap := make(map[string]ZhiHuNewsInfo)
	for idx, title := range zhiHuDailyNews.Titles {
		desLength := fmt.Sprintf("description length is %d", len(zhiHuDailyNews.Descriptions[idx]))
		zhiHuNewsInfoMap[title] = ZhiHuNewsInfo{
			Link:        zhiHuDailyNews.Links[idx],
			Description: desLength,
		}
	}

	p := NewsAggPage3{Title: "Amazing News Aggregator", NewsInfo: zhiHuNewsInfoMap}
	t, _ := template.ParseFiles("newsaggtemplate.html")
	t.Execute(w, p)
}

func main() {
	http.HandleFunc("/", indexHandler3)
	http.HandleFunc("/agg/", newsAggHandler3)
	http.ListenAndServe(":8000", nil)
}
