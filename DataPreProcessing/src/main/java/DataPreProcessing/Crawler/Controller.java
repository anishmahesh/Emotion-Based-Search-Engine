package DataPreProcessing.Crawler;


/**
 * Created by sanchitmehta on 01/12/16.
 */


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.io.BufferedReader;
import java.io.FileReader;


public class Controller {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    public static void main(String[] args) throws Exception {
      /*  if (args.length != 2) {
            logger.info("Needed parameters: ");
            logger.info("\t rootFolder (it will contain intermediate crawl data)");
            logger.info("\t numberOfCralwers (number of concurrent threads)");
            return;
        }*/

    /*
     * crawlStorageFolder is a folder where intermediate crawl data is
     * stored.
     */
        String crawlStorageFolder = "./data/intermediate/";

    /*
     * numberOfCrawlers shows the number of concurrent threads that should
     * be initiated for crawling.
     */
        int numberOfCrawlers = 20;

        CrawlConfig config = new CrawlConfig();

        config.setCrawlStorageFolder(crawlStorageFolder);
        //config.setMaxPagesToFetch(100);

        config.setPolitenessDelay(200);

        //set the max depth of the crawler here
        config.setMaxDepthOfCrawling(0);


    /*
     * We can set the maximum number of pages to crawl. The default value
     * is -1 for unlimited number of pages
     */
        config.setMaxPagesToFetch(1000000);

        /**
         * Crawl binary data ?
         * example: the contents of pdf, or the metadata of images etc
         */
        config.setIncludeBinaryContentInCrawling(false);
        config.setResumableCrawling(false);

    /*
     * Instantiate the controller for this crawl.
     */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

    /*
     * These are the first
     * URLs that are fetched and then the crawler starts following links
     * which are found in these pages
     */

        BufferedReader reader = new BufferedReader(new FileReader("path/to/file/with/urls"));
        String line;
        while ((line = reader.readLine()) != null) {
            controller.addSeed(line);
        }
        System.out.println("Added the seeds");

    /*
     * Start the crawl. ie code
     * will reach the line after this only when crawling is finished.
     */
        controller.start(App.class, numberOfCrawlers);
    }
}