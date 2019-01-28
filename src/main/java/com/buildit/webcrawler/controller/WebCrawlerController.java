package com.buildit.webcrawler.controller;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buildit.webcrawler.model.Response;
import com.buildit.webcrawler.model.WebCrawlerResult;

@RestController
@RequestMapping("/main")
public class WebCrawlerController {
	
	private Set<String> visitedLinks ;		
	private Map<String, WebCrawlerResult> crawledLinksMap ;
	private Queue<String> queueSites ; 
	private String domain = null;
	private URI inputURI = null;
	
	private static final String EXTERNAL_LINKS ="External";
	private static final String DOMAIN_LINKS ="Domain";
	private static final String IMG_LINKS ="Image";
	private static final String OUTPUT_FILE_NAME="CrawledLinks.txt";
	private String errorMessage = "";
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	@RequestMapping("/crawl")
	public Response webCrawler(@RequestParam(value = "url", required=false) String url){
		Response outputResponse = new Response();
		visitedLinks = new HashSet<String>();	
		crawledLinksMap = new HashMap<String, WebCrawlerResult>();
		queueSites = new LinkedList<String>(); 
		domain = null;
		
		
		
		if(url == null || url.trim().length() == 0) {
			url = "http://wiprodigital.com";
		}
		logger.info("Starting webcrawler for  "+url);
		boolean status = crawlWebPage(url) ;
		
		outputResponse.setCrawlerUrl(url);
		if(!status) {
			outputResponse.setStatusMessage("Error in Processing : "+errorMessage);
		}else {			
			outputResponse.setStatusMessage("Success");
		}
		
		
		formatOutput(outputResponse ) ;
		logger.info("Webcrawler for  "+url +" is complete.");
		return outputResponse;
	}
	
	/*
	 * This method crawls the provided URL.
	 */
	public boolean crawlWebPage(String url) {
		
				
		// Set the Domain name of the main URL .
		try {
			domain = getDomain(formatUrl(url));	
			inputURI = formatAndNormalizeUrl(url);
		}catch (URISyntaxException ux) {
			logger.error("Cannot format input url "+url + ". Exception is "+url);
			errorMessage = "Cannot format input url "+url + ". Exception is "+url;
			return false;
		}
		// Add the URL to the Queue which contains links to process.
		addURLToQueue(url);		
		
		if(queueSites.isEmpty()) {
			errorMessage = "Cannot format input url. Verify if the url was in the format http:// or https://  "+url ;
			return false;
		}
		processWebPage(url);			
		/*try {
			writeOutputToFile(url);
		} catch (IOException iox) {
			logger.error("Error while writing to file "+ iox.getMessage());
			errorMessage = "Error while writing to file "+ iox.getMessage();
		}*/
		
		return true;
	}
	
	/*
	 * This is the method that processes the mail URL link.
	 * It adds every crawled URL into a queue. 
	 * URL's in the Queue are processed. The method ends when all elements in the queue have been processed.
	 */
	private void  processWebPage(String url) /*throws IOException, URISyntaxException*/  {

		
		
		 while (!queueSites.isEmpty()) {
	        
	         String urlToProcess = queueSites.poll(); 
			
			 // Get if Visited, Do not process this element if it has been visited .
			 if (urlToProcess ==null || visitedLinks.contains(urlToProcess)) {
				 continue;
			 }			 
			 visitedLinks.add(urlToProcess);
			 			 
			 
			 Document documentToProcess = null;
			 try {
				 documentToProcess = Jsoup.connect(url).get();
			 }catch (HttpStatusException httpEx) {
				logger.error("Cannot access/find URL --> "+httpEx.getMessage());
			 } catch (IOException iox) {
				 logger.error("Cannot access/find URL --> "+iox.getMessage());
			}
			 
			 // Some links are not accessible. Skip processing in that case.
			 if(documentToProcess == null) {
				 continue;
			 }
			 
			//Get domain of the url that is being processed.			
			String currentDomain = null;
			try {
				currentDomain = getDomain(formatUrl(urlToProcess));
			} catch (URISyntaxException e1) {
				logger.error("Cannot get domain for url --> "+urlToProcess);
			}				
			 //If there is no domain , the url is not correct. Do not process.
			 if(currentDomain == null ) {
				 continue;
			 }
			 
			
			 
			 //If not current domain , add it to the external links
			 if( !currentDomain.equalsIgnoreCase(domain)) {
				 continue;
			 }
			 
			 processLinksInPage (  documentToProcess,   urlToProcess);
			 processImagesInPage (  documentToProcess,   urlToProcess);
			 
			
	        } 
	
	}
	
	private void processLinksInPage ( Document documentToProcess,  String urlToProcess){
		 Elements linksInPage = documentToProcess.select("a[href]");
		 
		 
		 for(Element link: linksInPage){
			 String currentUrl = link.attr("href");
			 try {
				String normUrl = normalizeUrl(currentUrl);
				
				if (normUrl == null || normUrl.length() < 1) {
					continue;
				}
				
				String linkDomain = null;
				try {
					linkDomain = getDomain(formatUrl(currentUrl));
				} catch (URISyntaxException e1) {
					logger.error("Cannot get domain for url --> "+urlToProcess);
				}				
				 //If there is no domain , the url is not correct. Do not process.
				 if(linkDomain == null ) {
					 continue;
				 }
				 boolean isExternalLink = isExternalLink(normUrl, linkDomain);
				 /*String domainSub = domain;
				 if(domain.startsWith("www.")) {
					 domainSub = domain.substring(4);
				 }
				 if( !linkDomain.equalsIgnoreCase(domain) && 
						 !linkDomain.endsWith(domainSub)) {*/
				 if(isExternalLink) {
					 addToResponseMap(urlToProcess,  normUrl ,  EXTERNAL_LINKS);
				 }else {
					addURLToQueue(normUrl);
					addToResponseMap(urlToProcess,  normUrl ,  DOMAIN_LINKS);
				 }
				
				
				
			} catch (URISyntaxException e) {
				logger.error("Exception while formatting / normalizing the URL  --> "
						+currentUrl+ " . Exception is "+e.getMessage());
			}
			 
		 }
	}
	private void processImagesInPage ( Document documentToProcess,  String urlToProcess){
		Elements imagesInPage = documentToProcess.select("img[src]");
		for(Element image: imagesInPage){
			 String imagesrc = image.attr("src");
			 addToResponseMap(urlToProcess,  imagesrc ,  IMG_LINKS);
			 
			 
		 }
		
	}
	
	
	private void addToResponseMap(String urlToProcess, String normalizedUrl, String type) {
		WebCrawlerResult responseObj = crawledLinksMap.get(urlToProcess);
		
		if (responseObj == null) {
			responseObj = new WebCrawlerResult();	
			responseObj.setProcessedUrl(urlToProcess);
			crawledLinksMap.put(urlToProcess, responseObj);
		}
		
		
		if(EXTERNAL_LINKS.equals(type)) {
			Set<String> externalLinks = responseObj.getExternalLinks();
			if(externalLinks == null) {
				externalLinks = new HashSet<String>();
				responseObj.setExternalLinks(externalLinks);
			}
			externalLinks.add(normalizedUrl);
			
		}else if(DOMAIN_LINKS.equals(type)) {
			Set<String> domainLinks = responseObj.getDomainLinks();
			if(domainLinks == null) {
				domainLinks = new HashSet<String>();
				responseObj.setDomainLinks(domainLinks);
			}
			domainLinks.add(normalizedUrl);
		}else if (IMG_LINKS.equals(type)) {
			Set<String> staticLinks = responseObj.getStaticLinks();
			if(staticLinks == null) {
				staticLinks = new HashSet<String>();
				responseObj.setStaticLinks(staticLinks);
			}
			staticLinks.add(normalizedUrl);
		}

			
			
		
		
		
	}
	
	
	private boolean isExternalLink(String urlName, String linkDomain) {
        URI uri;
        boolean isExternal = false;
        try {
            uri = new URI(urlName);
            String domainSub = domain;
            if(domain.startsWith("www.")) {
				 domainSub = domain.substring(4);
			 }
            
            if(!uri.getHost().endsWith(inputURI.getHost()) && !linkDomain.endsWith(domainSub)) {
            	isExternal = true;
            }
            

        } catch (URISyntaxException e) {
        	 String domainSub = domain;
			 if(domain.startsWith("www.")) {
				 domainSub = domain.substring(4);
			 }
			 if( !linkDomain.equalsIgnoreCase(domain) && 
					 !linkDomain.endsWith(domainSub)) {
				 isExternal = true;
			 }
        }

        return isExternal;
    }

	private String getDomain(String uriToProcess) throws URISyntaxException {
		URI uri = new URI(uriToProcess);
		String currentDomain = uri.getHost();	
		return currentDomain;
	}

	private String formatUrl(String uRL) throws URISyntaxException{
		String formattedUrl = uRL.trim().replace(" ", "%20");
		return formattedUrl;
	}
	
	private void addURLToQueue(String currentUrl)  {		
		try {
			URI formattedNormalizedCurrentURI = formatAndNormalizeUrl(currentUrl);
			String formattedURL = formattedNormalizedCurrentURI.toURL().toString();
			queueSites.add(formattedURL);
		}catch(IllegalArgumentException | MalformedURLException | URISyntaxException ex) {
			logger.info("URL is malformed , do not add to queue "+currentUrl + " " +ex.getMessage());
		}
	}
	
	private URI formatAndNormalizeUrl(String url) throws URISyntaxException{
		String formattedUrl = formatUrl(url);
		String normalizedUrl = normalizeUrl(formattedUrl);
		URI formattedNormalizedUri = new URI(normalizedUrl);	
		
		return formattedNormalizedUri;
	}
	
	
	
	private String normalizeUrl(String formattedUrlStr) throws URISyntaxException {
		
		String domain = getDomain(formattedUrlStr);
		URI uri;
        try {
            uri = new URI(formattedUrlStr);
            if (!uri.isAbsolute()) {
                uri = new URI(domain + "/").resolve(uri).normalize();
            }
            String strUri = uri.toString();

            if(uri.getFragment() != null) {
                int index = strUri.indexOf("#");
                if (index == -1) { 
                    return strUri;
                }
                return strUri.substring(0, index);
            }
            
            return strUri;

        } catch (URISyntaxException e) {}

        return "";
	}
	
	public List<WebCrawlerResult> formatOutput(Response crawlerResult ) {
		List<WebCrawlerResult> results = new ArrayList<WebCrawlerResult>();
		
		
		for(String visitedLink : visitedLinks) {			
	    	WebCrawlerResult responseObj = crawledLinksMap.get(visitedLink);
	    	if(responseObj != null) {
	    		results.add(responseObj);
	    	}
		}
		
		crawlerResult.setOutputList(results);
		return results;
	}
	
	private void writeOutputToFile(String url) throws IOException {
	    
	    BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_NAME));
	    
	    try {
		    writer.write("Output File for --> "+url);
		    writer.newLine();
		    writer.newLine();
		    
		    for(String visitedLink : visitedLinks) {
				
		    	WebCrawlerResult responseObj = crawledLinksMap.get(visitedLink);
		    	Set<String> domainLinks =  responseObj.getDomainLinks();
		    	Set<String> externalLinks =  responseObj.getExternalLinks();
		    	Set<String> imageLinks =  responseObj.getStaticLinks();
		    	writer.append(" ********************************************************************** ");
		    	writer.newLine();
		    	writer.append("URL : "+visitedLink);
				writer.newLine();
				
				
		    	if(domainLinks != null) {
		    		writer.append("--------- Domain Links  ----------------");
					writer.newLine();
			    	for(String domainLink : domainLinks) {
						writer.append("          "+domainLink);
						writer.newLine();
					}
			    	writer.append("--------- ------------  ----------------");
					writer.newLine();
		    	}
		    	
		    	if(externalLinks != null) {
		    		writer.append("--------- External Links  ----------------");
		    		writer.newLine();
			    	for(String externalLink : externalLinks) {
						writer.append("          "+externalLink);
						writer.newLine();
					}
			    	writer.append("--------- ------------  ----------------");
					writer.newLine();
		    	}
		    	
		    	if(imageLinks != null) {
		    		writer.append("--------- Static Content Links  ----------------");
		    		writer.newLine();
			    	for(String imageLink : imageLinks) {
						writer.append("          "+imageLink);
						writer.newLine();
					}
			    	writer.append("--------- ------------  ----------------");
					writer.newLine();
		    	}
		    	
		    	
		    	writer.append(" ***************************************************************** ");
		    	writer.newLine();
		    	
				
	
				
			}
		    writer.append(" ///////////////////////////////////////////////////////////////////////// ");  
		    writer.newLine();
	    }catch(Exception ex){
	    	logger.info("Error while writng to file");
	    }finally {
	    	writer.close();
	    }
	    
	}
}
