package main

import (
	"encoding/xml"
	"fmt"
)

// an example site map
var washingtonPostXML3 = []byte(`<sitemapindex>
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

// an example news page, listing news infos
var newsXML3 = []byte(`<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9" xmlns:news="http://www.google.com/schemas/sitemap-news/0.9" xmlns:video="http://www.google.com/schemas/sitemap-video/1.1">
<url>
	<loc>https://www.washingtonpost.com/opinions/2023/09/28/perry-bacon-live-chat/</loc>
	<lastmod>2023-09-28T16:30:54.316Z</lastmod>
	<news:news>
		<news:publication>
		<news:name>Washington Post</news:name>
		<news:language>en</news:language>
		</news:publication>
		<news:publication_date>2023-09-28T16:30:54.316Z</news:publication_date>
		<news:title>
		<![CDATA[ Chat with Perry Bacon Jr. about politics and beyond ]]>
		</news:title>
	</news:news>
	<changefreq>hourly</changefreq>
</url>
<url>
	<loc>https://www.washingtonpost.com/opinions/2023/09/27/jennifer-rubin-live-chat/</loc>
	<lastmod>2023-09-27T16:00:09.614Z</lastmod>
	<news:news>
		<news:publication>
		<news:name>Washington Post</news:name>
		<news:language>en</news:language>
		</news:publication>
		<news:publication_date>2023-09-27T16:00:09.614Z</news:publication_date>
		<news:title>
		<![CDATA[ Chat with Jennifer Rubin about her columns, politics, policy and more ]]>
		</news:title>
		<news:keywords>
		<![CDATA[ partner-exclude, jennifer rubin, washington post opinions, live chat, politics chat, politics, conservative, gop, democrats ]]>
		</news:keywords>
	</news:news>
	<changefreq>hourly</changefreq>
</url>
<url>
	<loc>https://www.washingtonpost.com/opinions/2023/09/21/murdoch-resigns-fox-changes/</loc>
	<lastmod>2023-09-21T22:45:16.519Z</lastmod>
	<news:news>
		<news:publication>
		<news:name>Washington Post</news:name>
		<news:language>en</news:language>
		</news:publication>
		<news:publication_date>2023-09-21T22:45:16.519Z</news:publication_date>
		<news:title>
		<![CDATA[ The forecast for Fox News: Business as usual, or worse ]]>
		</news:title>
		<news:keywords>
		<![CDATA[ partner-exclude ]]>
		</news:keywords>
	</news:news>
	<changefreq>hourly</changefreq>
</url>
`)

type SitemapIndex3 struct {
	Locations []string `xml:"sitemap>loc"`
}

type News3 struct {
	Locations         []string `xml:"url>loc"`
	LastMods          []string `xml:"url>lastmod"`
	ChangeFrequencies []string `xml:"url>changefreq"`
}

type NewsInfo3 struct {
	LastMod    string
	ChangeFreq string
}

func main() {
	bytes := washingtonPostXML3 // use static data
	var s SitemapIndex3
	xml.Unmarshal(bytes, &s)
	fmt.Println(s.Locations)

	for _, location := range s.Locations {
		fmt.Println("\nfrom location:", location)
		bytes2 := newsXML3 // use static data

		var n News3
		xml.Unmarshal(bytes2, &n)
		fmt.Println("News:", n)

		// create a map
		newsInfoMap := make(map[string]NewsInfo3)

		// manually populate key and value in to newsInfoMap, key is location, value is type NewsInfo3
		for idx, location := range n.Locations {
			newsInfoMap[location] = NewsInfo3{
				LastMod:    n.LastMods[idx],
				ChangeFreq: n.ChangeFrequencies[idx],
			}
		}

		// print populated map in 2 ways
		fmt.Println("NewsInfoMap:", newsInfoMap)
		fmt.Println("as same as:")
		for k, v := range newsInfoMap {
			fmt.Println(k, ":", v)
		}
	}
}
