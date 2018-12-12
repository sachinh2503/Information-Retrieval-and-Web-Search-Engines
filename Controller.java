import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {
public static void main(String[] args) throws Exception {
	String crawlStorageFolder = "C:\\Users\\sachi\\OneDrive\\Documents\\IR\\HW-2\\Crawler_Downloads";
	int numberOfCrawlers = 7;
	CrawlConfig config = new CrawlConfig();
	config.setCrawlStorageFolder(crawlStorageFolder);
	config.setMaxDepthOfCrawling(16);
	config.setMaxPagesToFetch(20000);
	config.setPolitenessDelay(200);
	config.setUserAgentString("Panda");
	config.setIncludeBinaryContentInCrawling(Boolean.TRUE);
/*  Instantiate the controller for this crawl.*/
	PageFetcher pageFetcher = new PageFetcher(config);
	RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
	RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
	CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
/*  For each crawl, you need to add some seed urls. These are the first
* URLs that are fetched and then the crawler starts following links
* which are found in these pages  */
	controller.addSeed("https://www.mercurynews.com/");
/*  Start the crawl. This is a blocking operation, meaning that your code
* will reach the line after this only when crawling is finished.  */
	controller.start(Crawler.class, numberOfCrawlers);
}
}
