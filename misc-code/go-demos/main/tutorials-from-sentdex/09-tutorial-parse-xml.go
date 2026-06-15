package main

import (
	"encoding/xml"
	"fmt"
)

var washingtonPostXML = []byte(`<sitemapindex>
	<sitemap>
	  <loc>https://washingtonpost.com/news-politics-sitemap.xml</loc>
	</sitemap>
	<sitemap>
	  <loc>https://washingtonpost.com/news-blogs-politics-sitemap.xml</loc>
	</sitemap>
	<sitemap>
	  <loc>https://washingtonpost.com/news-blogs-local-sitemap.xml</loc>
	</sitemap>
	<sitemap>
	  <loc>https://washingtonpost.com/news-sports-sitemap.xml</loc>
	</sitemap>
	<sitemap>
	  <loc>https://washingtonpost.com/news-blogs-sports-sitemap.xml</loc>
	</sitemap>
	<sitemap>
	  <loc>https://washingtonpost.com/news-blogs-national-sitemap.xml</loc>
	</sitemap>
	<sitemap>
	  <loc>https://washingtonpost.com/news-world-sitemap.xml</loc>
	</sitemap>
	<sitemap>
	  <loc>https://washingtonpost.com/news-blogs-world-sitemap.xml</loc>
	</sitemap>
	<sitemap>
	  <loc>https://washingtonpost.com/news-blogs-technology-sitemap.xml</loc>
	</sitemap>
	<sitemap>
	  <loc>https://washingtonpost.com/news-blogs-lifestyle-sitemap.xml</loc>
	</sitemap>
</sitemapindex>`)

type SitemapIndex struct {
	Locations []Location `xml:"sitemap"`
}

type Location struct {
	Loc string `xml:"loc"`
}

// convert Location to string
func (l Location) String() string {
	//return "a"
	return fmt.Sprint(l.Loc)
}

func main() {
	bytes := washingtonPostXML // or by [ bytes, _ := ioutil.ReadAll(resp.Body) ]
	var s SitemapIndex
	xml.Unmarshal(bytes, &s)
	fmt.Println(s.Locations)

	for _, loc := range s.Locations {
		fmt.Println(loc)

	}
}
