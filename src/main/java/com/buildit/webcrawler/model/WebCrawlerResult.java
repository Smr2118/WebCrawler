package com.buildit.webcrawler.model;

import java.util.Set;

/*
 * Model Class for the response object.
 */
public class WebCrawlerResult {

	String processedUrl;
    Set<String> domainLinks;
    Set<String> externalLinks;
    Set<String> staticLinks;
	public Set<String> getDomainLinks() {
		return domainLinks;
	}
	public void setDomainLinks(Set<String> domainLinks) {
		this.domainLinks = domainLinks;
	}
	public Set<String> getExternalLinks() {
		return externalLinks;
	}
	public void setExternalLinks(Set<String> externalLinks) {
		this.externalLinks = externalLinks;
	}
	public Set<String> getStaticLinks() {
		return staticLinks;
	}
	public void setStaticLinks(Set<String> staticLinks) {
		this.staticLinks = staticLinks;
	}
	public String getProcessedUrl() {
		return processedUrl;
	}
	public void setProcessedUrl(String processedUrl) {
		this.processedUrl = processedUrl;
	}
	
	
}
