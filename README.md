WebCrawler
------------------------------------------------------------------------
This project is a simple web crawler that is limited to one doman and developed in Java using Spring Boot.

Getting Started
------------------------------------------------------------------------
Please follow these instructions to get the project running in your system.

Prerequisites
------------------------------------------------------------------------
Java 8
Gradle (Version 4.5)

Installing
------------------------------------------------------------------------
Clone the github project using :
git clone https://github.com/Smr2118/WebCrawler.git

In command line , cd to the root of the project (WebCrawler folder)

Execute :

gradlew bootrun

This will start an instance of the spring boot application on tomcat . It will run in port 8080.

Testing 
------------------------------------------------------------------------

You can use the below url to test. Substitute the value of url with any other URL if you need to.

http://localhost:8080/main/crawl?url=http://wiprodigital.com

You have a choice to not provide the url parameter. 
In this case the request url will be the below one and the application will use the default url which is http://wiprodigital.com .

http://localhost:8080/main/crawl

Response 
------------------------------------------------------------------------

The response is a json response in the format :

"crawlerUrl": The Input URL
"statusMessage" :  The status of the request .
"outputList" : The list of crawled urls . This list contains :
		"processedUrl" 
		"domainLinks" - All the (same)domain links in the processed url
		"externalLinks" - All external links
		"staticLinks" - Images 
		
The crawler is limited to the input domain. 
The output list consists of a list of processed url's , each of which are other links belonging to the same domain. 

A sample output will be :
{  
   "crawlerUrl":"https://www.google.com",
   "outputList":[  
      {  
         "processedUrl":"https://www.google.com",
         "domainLinks":[  
            "https://play.google.com/?hl=en&tab=w8",
            "https://www.google.com/intl/en/about/products?tab=wh",
            "https://www.google.com/imghp?hl=en&tab=wi",
            "https://accounts.google.com/ServiceLogin?hl=en&passive=true&continue=https://www.google.com/",
            "https://plus.google.com/116899029375914044550",
            "https://news.google.com/nwshp?hl=en&tab=wn",
            "https://mail.google.com/mail/?tab=wm",
            "https://maps.google.com/maps?hl=en&tab=wl",
            "http://www.google.com/history/optout?hl=en",
            "https://drive.google.com/?tab=wo"
         ],
         "externalLinks":[  
            "https://www.youtube.com/?gl=US&tab=w1"
         ],
         "staticLinks":[  
            "/images/branding/googlelogo/1x/googlelogo_white_background_color_272x92dp.png"
         ]
      }
   ],
   "statusMessage":"Success"
}



