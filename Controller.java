package com.crawler.web.controller;

import java.io.FileWriter;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * 
 * @author Jagruthi Prabhudev
 * 
 * This class is the controller class for the web crawler, initial config variables are set 
 * and then the crawler is called. Multithreading is used
 */
public class Controller {

	
	public static void main(String args[]) throws Exception {
		
		 String crawlStorageFolder = "/data/crawler";
		 int numberOfCrawlers = 20;
		 int maxPagesToFetch = 20000;
		 int maxDepthOfCrawling = 16;
		 int politenessDelay = 10;
		 boolean includeBinaryContentInCrawling = true;
		 String userAgentString = "USC Viterbi";


		 CrawlConfig config = new CrawlConfig();
		 config.setCrawlStorageFolder(crawlStorageFolder);
		 /*
		  * Instantiate the controller for this crawl.
		  */
		 PageFetcher pageFetcher = new PageFetcher(config);
		 RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		 RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		 CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
 
		 /*
		  * For each crawl, you need to add some seed urls. These are the first
		  * URLs that are fetched and then the crawler starts following links
		  * which are found in these pages
		  */
		 controller.addSeed("https://www.latimes.com/");
 
		 // Set the max depth and max pages to fetch
		 config.setMaxPagesToFetch(maxPagesToFetch);
		 config.setMaxDepthOfCrawling(maxDepthOfCrawling);
		 config.setPolitenessDelay(politenessDelay);
 	
		 // set crawler to crawl binary data
		 config.setIncludeBinaryContentInCrawling(includeBinaryContentInCrawling);
		 config.setIncludeHttpsPages(true);
 
		 //user-agent string that is used for representing your crawler to web servers.
		 config.setUserAgentString(userAgentString);
 
		 //Create files and write headings as given in the document
		 FileWriter myFileWriter  = null;
		 myFileWriter = new FileWriter("fetch_latimes.csv",false);
		 myFileWriter.append("URL,Status_Code\n");
		 myFileWriter.close();
		 myFileWriter = new FileWriter("visit_latimes.csv"+ "",false);
		 myFileWriter.append("URL,SIZE,Outlinks_Found,Content-Type\n");
		 myFileWriter.close();
		 myFileWriter = new FileWriter("urls_latimes.csv",false);
		 myFileWriter.append("URL,Status_Indicator\n");
		 myFileWriter.close();
 
		 /*
		  * Start the crawl. This is a blocking operation, meaning that your code
		  * will reach the line after this only when crawling is finished.
		  */
		 controller.start(MyCrawler.class, numberOfCrawlers);
		 MyCrawler.saveStatistics();
		
	}
}