package com.buildit.webcrawler.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.buildit.webcrawler.model.Response;
import com.buildit.webcrawler.model.WebCrawlerResult;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WebCrawlerControllerTest {
	
	 @Autowired
	 private WebCrawlerController webCrawlerController;
	 
	 
	@Test
	public void testNullUrl() {
		Response outputResponse = webCrawlerController.webCrawler(null);
		assertNotNull(outputResponse);	
		assertEquals("Success", outputResponse.getStatusMessage());
		assertEquals("http://wiprodigital.com", outputResponse.getCrawlerUrl());
		
	}
	
	@Test
	public void testNotNullUrl() {
		Response outputResponse = webCrawlerController.webCrawler("https://www.google.com/maps");
		assertNotNull(outputResponse);	
		assertEquals("Success", outputResponse.getStatusMessage());
		assertEquals("https://www.google.com/maps", outputResponse.getCrawlerUrl());
		assertNotNull(outputResponse.getOutputList());
		
		
	}
	
	@Test
	public void testHasDomainLinks() {
		Response outputResponse = webCrawlerController.webCrawler("https://www.google.com/maps");
		List<WebCrawlerResult>  listResults = outputResponse.getOutputList();
		WebCrawlerResult output = null;
		for(WebCrawlerResult result : listResults) {
			if(!result.getDomainLinks().isEmpty()) {
				output = result;
			}
		}
		
		assertNotNull(output);
		
		
	}
	
	@Test
	public void testHasExternalLinks() {
		Response outputResponse = webCrawlerController.webCrawler("https://www.google.com");
		List<WebCrawlerResult>  listResults = outputResponse.getOutputList();
		WebCrawlerResult output = null;
		for(WebCrawlerResult result : listResults) {
			if(!result.getExternalLinks().isEmpty()) {
				output = result;
			}
		}
		
		assertNotNull(output);
		
		
	}
	
	@Test
	public void testHasStaticLinks() {
		Response outputResponse = webCrawlerController.webCrawler("https://www.google.com");
		List<WebCrawlerResult>  listResults = outputResponse.getOutputList();
		WebCrawlerResult output = null;
		for(WebCrawlerResult result : listResults) {
			if(!result.getStaticLinks().isEmpty()) {
				output = result;
			}
		}
		
		assertNotNull(output);
		
		
	}
}
