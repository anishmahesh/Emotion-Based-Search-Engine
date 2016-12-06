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
        String crawlStorageFolder = "./data/";

    /*
     * numberOfCrawlers shows the number of concurrent threads that should
     * be initiated for crawling.
     */
        int numberOfCrawlers = 20;

        CrawlConfig config = new CrawlConfig();

        config.setCrawlStorageFolder(crawlStorageFolder);
        //config.setMaxPagesToFetch(100);

        config.setPolitenessDelay(100);


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
        controller.addSeed("http://www.theonion.com/");
        controller.addSeed("http://www.theonion.com/section/politics/");
        controller.addSeed("http://www.theonion.com/section/sports/");
        controller.addSeed("http://www.theonion.com/section/local/");
        controller.addSeed("http://www.theonion.com/section/business");
        controller.addSeed("http://www.theonion.com/section/entertainment/");
        controller.addSeed("http://www.theonion.com/section/science-technology/");
        //controller.addSeed("http://www.ics.uci.edu/~lopes/");
        //controller.addSeed("http://www.ics.uci.edu/~welling/");

    /*
     * Start the crawl. ie code
     * will reach the line after this only when crawling is finished.
     */
        controller.start(App.class, numberOfCrawlers);
    }
}