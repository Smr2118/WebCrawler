package com.buildit.webcrawler.model;

import java.util.List;

public class Response {
	
	String crawlerUrl ;
	List<WebCrawlerResult> outputList;
	String statusMessage ;
	
	
	public List<WebCrawlerResult> getOutputList() {
		return outputList;
	}
	public void setOutputList(List<WebCrawlerResult> outputList) {
		this.outputList = outputList;
	}
	public String getStatusMessage() {
		return statusMessage;
	}
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	public String getCrawlerUrl() {
		return crawlerUrl;
	}
	public void setCrawlerUrl(String crawlerUrl) {
		this.crawlerUrl = crawlerUrl;
	}
	
	
}
