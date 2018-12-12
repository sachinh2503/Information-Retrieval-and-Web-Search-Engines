package com.crawler.web.controller;

import edu.uci.ics.crawler4j.crawler.WebCrawler;

import java.io.FileWriter;
import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
public class MyCrawler extends  WebCrawler{
	
	 private static final Pattern FILTERS = Pattern.compile(
        ".*(\\.(css|js|bmp" + "|wav|avi|mov|mpeg|ram|m4v" + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

		static ArrayList<UrlInfo> allDiscoveredURLs = new ArrayList<UrlInfo>();
		static ArrayList<UrlInfo> attemptedURLs = new ArrayList<UrlInfo>();
		static ArrayList<UrlInfo> visitedURLs = new ArrayList<UrlInfo>();
		static String NAME = "JAGRUTHI SHIVAPURA PRABHUDEV";
        static String USC_ID = "3869428008";
        static String NEWS_SITE = "https://www.latimes.com/";
	    String typeOfURL = " ";
	   
	   
	 @Override
     public boolean shouldVisit(Page referringPage, WebURL url) {
       
		String url_file = "urls_latimes.csv";
		String href = url.getURL().toLowerCase();
         
        //check and allow only pdf,image and http files
		List<String> url_data = new ArrayList<String>();
		url_data.add(href.replaceAll(",", "-"));
        if(href.startsWith("https://www.latimes.com/")){
        	typeOfURL = "OK"; 
			url_data.add("OK");
			 allDiscoveredURLs.add(new UrlInfo(href, typeOfURL));
		}
        else{
			typeOfURL = "N_OK";
			url_data.add("N_OK");
			 allDiscoveredURLs.add(new UrlInfo(href, typeOfURL));
		}
		 
		writeCSV(url_file, url_data);
		return ((!FILTERS.matcher(href).matches())&&
				(href.startsWith("http://www.latimes.com/") || href.startsWith("https://www.latimes.com/") 
				||href.startsWith("http://latimes.com/") || href.startsWith("https://latimes.com/")));
	 }
	 
	 @Override
	 protected WebURL handleUrlBeforeProcess(WebURL curURL) {
			// TODO Auto-generated method stub
			String	href=curURL.getURL().toLowerCase();
			return super.handleUrlBeforeProcess(curURL);
		}
	 
	 @Override
	 protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
			// TODO Auto-generated method stub
			String	href=webUrl.getURL().toLowerCase();
			String fetch_file = "fetch_latimes.csv";
			List<String> fetchData = new ArrayList<String>();
			fetchData.add(href.replaceAll(",", "-"));
			fetchData.add(String.valueOf(statusCode));
			attemptedURLs.add(new UrlInfo(href.replaceAll(",", "-"), statusCode));
			writeCSV(fetch_file,fetchData);
			super.handlePageStatusCode(webUrl, statusCode, statusDescription);
	 }

	 @Override
	 protected void onUnexpectedStatusCode(String urlStr, int statusCode, String contentType, String description) {
			// TODO Auto-generated method stub
			List<String> fetchData = new ArrayList<String>();
			String fetch_file = "fetch_latimes.csv";
			fetchData.add(urlStr.replaceAll(",", "-"));
			fetchData.add(String.valueOf(statusCode));
			attemptedURLs.add(new UrlInfo(urlStr.replaceAll(",", "-"), statusCode));
			writeCSV(fetch_file,fetchData);
			super.onUnexpectedStatusCode(urlStr, statusCode, contentType, description);
	 }

	 @Override
	 protected void onPageBiggerThanMaxSize(String urlStr, long pageSize) {
			// TODO Auto-generated method stub
			List<String> fetchData = new ArrayList<String>();
			String fetch_file = "fetch_latimes.csv";
			super.onPageBiggerThanMaxSize(urlStr, pageSize);
	  }

	 @Override
	 protected void onContentFetchError(WebURL webUrl) {
		 
			// TODO Auto-generated method stub
			List<String> fetchData = new ArrayList<String>();
			String	href=webUrl.getURL().toLowerCase();
			String fetch_file = "fetch_latimes.csv";
			
			fetchData.add(href.replaceAll(",", "-"));
			fetchData.add(String.valueOf(0));
			attemptedURLs.add(new UrlInfo(href.replaceAll(",", "-"), 0));
			
			writeCSV(fetch_file,fetchData);
			super.onContentFetchError(webUrl);
	  }
	 
	 @Override
	 protected void onParseError(WebURL webUrl) {
			String	href=webUrl.getURL().toLowerCase();
			super.onParseError(webUrl);
	  }

	 @Override
	 public void visit(Page page) {
			List<String> visitData = new ArrayList<String>();
			String visit_file = "visit_latimes.csv";
			String url  = page.getWebURL().getURL();
			
			if (page.getParseData()  instanceof HtmlParseData) {
				HtmlParseData htmlParseData  = (HtmlParseData) page.getParseData();
				Set< WebURL >links  = htmlParseData.getOutgoingUrls();
				visitData.add(url);
				visitData.add(String.valueOf(page.getContentData().length));
				visitData.add(String.valueOf(links.size()));
				visitData.add(page.getContentType());
				UrlInfo urlInfo = new UrlInfo(url, page.getContentData().length,(links.size()) , page.getContentType());
                visitedURLs.add(urlInfo);
				writeCSV(visit_file,visitData);
				/**System.out.println("a count:: "+a_count);
				System.out.println("b count:: "+ b_count);
				System.out.println("c count:: "+ c_count);
				System.out.println("total count:: "+ total_count);*/
			 }
		}
	/**
	 * GeneralMethod for writing the output data into CSV files
	 * @param fileName
	 * @param data
	 */
	
	public void writeCSV(String fileName,List<String> data){
		  FileWriter fileWriter = null;
		  final String COMMA_DELIMITER = ",";
		  final String NEW_LINE_SEPARATOR = "\n";
          try {
 
        	  fileWriter = new FileWriter(fileName,true);
        	  for(int i=0;i<data.size();i++){
        		  fileWriter.append(data.get(i));
        		  if(i!=data.size()-1) {
        			  fileWriter.append(COMMA_DELIMITER);
        		  }
        		  else { 
        			  fileWriter.append(NEW_LINE_SEPARATOR);
        		  }
        	  }
           }catch (Exception e) {
        	   System.out.println("Error in CsvFileWriter !!!");
        	   e.printStackTrace();
           } 
          finally {
               try {
            	   fileWriter.flush();
            	   fileWriter.close();
                } 
               catch (IOException e) {
            	   System.out.println("Error while flushing/closing fileWriter !!!");
            	   e.printStackTrace();
                }
            }
        }  
	
	/**
	 * Create the report txt file
	 */
	
	public static void saveStatistics() throws Exception {
        FileWriter myFileWriter  = null;
	   	myFileWriter = new FileWriter("CrawlReport.txt",false);
        
        // Personal Info
	   	myFileWriter.append("Name: " + NAME + "\n");
	   	myFileWriter.append("USC ID: " + USC_ID + "\n");
	   	myFileWriter.append("News site crawled: " + NEWS_SITE + "\n");
	   	myFileWriter.append("\n");

        // Fetch Statistics
	   	myFileWriter.append("Fetch Statistics\n=====================\n");
	   	myFileWriter.append("fetches attempted: " + attemptedURLs.size() + "\n");
	   	myFileWriter.append("fetched succeeded: " + visitedURLs.size() + "\n");

        // get failed url and aborted urls
        int failedUrlsCount = 0;
        int abortedUrlsCount = 0;
        for (UrlInfo info : attemptedURLs) {
            if (info.statusCode >= 300 && info.statusCode < 400) {
            	failedUrlsCount++;
            	
            } else if (info.statusCode != 200) {
            	abortedUrlsCount++;
            }
        }

        myFileWriter.append("fetched aborted: " + abortedUrlsCount + "\n");
        myFileWriter.append("fetched failed: " + failedUrlsCount + "\n");
        myFileWriter.append("\n");

        
        //Outgoing URL's Statistics
        HashSet<String> hashSet = new HashSet<String>();
        hashSet.add("myurl"); 
        int uniqueUrls = 0;
        int withinNewsSiteUrls = 0;
        int outSideNewsSiteUrls = 0;
        myFileWriter.append("Outgoing URLs\n=====================\n");
        myFileWriter.append("Total URLS extracted: " +allDiscoveredURLs.size() + "\n");
        for (UrlInfo info : allDiscoveredURLs) {
        if(null != info) {
            if (null != hashSet && null != info.url && !hashSet.contains(info.url)) {
                hashSet.add(info.url);
                uniqueUrls++;
                if (info.type.equals("OK")) {
                    withinNewsSiteUrls++;
                } 
                else {
                    outSideNewsSiteUrls++;
                }
            }
        }
       }
        
        myFileWriter.append("# unique URLs extracted: " + uniqueUrls + "\n");
        myFileWriter.append("# unique URLs within News Site : " + withinNewsSiteUrls + "\n");
        myFileWriter.append("# unique URLs outside News Site: " + outSideNewsSiteUrls + "\n");
        myFileWriter.append("\n");

        // Status Code
        myFileWriter.append("Status Codes\n=====================\n");
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        for (UrlInfo info : attemptedURLs) {
            if (hashMap.containsKey(info.statusCode)) {
                hashMap.put(info.statusCode, hashMap.get(info.statusCode) + 1);
            } else {
                hashMap.put(info.statusCode, 1);
            }
        }
        HashMap<Integer, String> statusCodeMapping = new HashMap<Integer, String>();
        statusCodeMapping.put(200, "OK");
        statusCodeMapping.put(301, "Moved Permanently");
        statusCodeMapping.put(302, "Found");
        statusCodeMapping.put(401, "Unauthorized");
        statusCodeMapping.put(403, "Forbidden");
        statusCodeMapping.put(404, "Not Found");
        statusCodeMapping.put(405, "Method Not Allowed");
        statusCodeMapping.put(500, "Internal Server Error");

        for (Integer key : hashMap.keySet()) {
            myFileWriter.append("" + key + " " + statusCodeMapping.get(key) + ": " + hashMap.get(key) + "\n");
        }
        myFileWriter.append("\n");

        // File Size
        myFileWriter.append("File Size\n=====================\n");
        int oneK = 0;
        int tenK = 0;
        int hundredK = 0;
        int oneM = 0;
        int other = 0;
        for (UrlInfo info : visitedURLs) {
            if (info.size < 1024) {
                oneK++;
            } else if (info.size < 10240) {
                tenK++;
            } else if (info.size < 102400) {
                hundredK++;
            } else if (info.size < 1024 * 1024) {
                oneM++;
            } else {
                other++;
            }
        }
        myFileWriter.append("< 1KB: " + oneK + "\n");
        myFileWriter.append("1KB ~ <10KB: " + tenK + "\n");
        myFileWriter.append("10KB ~ <100KB: " + hundredK + "\n");
        myFileWriter.append("100KB ~ <1MB: " + oneM + "\n");
        myFileWriter.append(">= 1MB: " + other + "\n");
        myFileWriter.append("\n");

        // Content Types
        HashMap<String, Integer> hashMap1 = new HashMap<String, Integer>();
        myFileWriter.append("Content Types\n=====================\n");
        for (UrlInfo info : visitedURLs) {
            if (info.type.equals("unknown")) {
                continue;
            }
            if (hashMap1.containsKey(info.type)) {
                hashMap1.put(info.type, hashMap1.get(info.type) + 1);
            } else {
                hashMap1.put(info.type, 1);
            }
        }
        for (String key : hashMap1.keySet()) {
        	myFileWriter.append("" + key + ": " + hashMap1.get(key) + "\n");
        }
        myFileWriter.append("\n");

        myFileWriter.flush();
        myFileWriter.close();
    }
	
}

