package DataPreProcessing.Crawler;

/**
 * Created by sanchitmehta on 01/12/16.
 */

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.Header;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;


public class App extends WebCrawler {

    //int count = 0;

    HashSet<String> visited = new HashSet<String>();
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp3|zip|gz))$");


    /**
     * This function to specifies whether the given url
     * should be crawled or not (based on the crawling logic).
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();

        // Only accept the url if it is in the "theoninon" domain and protocol is "http".
        return !FILTERS.matcher(href).matches()
                && href.contains("theonion.com");
        //return href.contains("unrealtimes");
        //return true;
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     */
    @Override
    public void visit(Page page) {
        int docid = page.getWebURL().getDocid();
        String url = page.getWebURL().getURL();
        String domain = page.getWebURL().getDomain();
        String path = page.getWebURL().getPath();
        String subDomain = page.getWebURL().getSubDomain();
        String parentUrl = page.getWebURL().getParentUrl();
        String anchor = page.getWebURL().getAnchor();

        logger.debug("Docid: {}", docid);
        logger.info("URL: {}", url);
        logger.debug("Domain: '{}'", domain);
        logger.debug("Sub-domain: '{}'", subDomain);
        logger.debug("Path: '{}'", path);
        logger.debug("Parent page: {}", parentUrl);
        logger.debug("Anchor text: {}", anchor);
        //count++;
        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            try {
                    int hashIndex = url.lastIndexOf("#");
                    if(hashIndex>0) {
                        url = url.substring(0, hashIndex);
                        System.out.println("# URL :::::: "+url);
                    }
                    if(htmlParseData.getTitle()!=null) {
                        if (htmlParseData.getText().length() > 3000 && domain.contains("theonion") && !visited.contains(url)) {
                            String title = htmlParseData.getTitle();
                            if (title != null)
                                title = title.replace(" - The Onion - America's Finest News Source", "");
                            visited.add(url);
                            Writer writer = new BufferedWriter(new OutputStreamWriter(
                                    new FileOutputStream("./data/" + docid + ".txt"), "utf-8"));
                            writer.write(title + "\n" + url + "\n" + OneLiner.getMainText(htmlParseData.getHtml()));
                            writer.close();
                        }
                    }
            }catch (Exception e){
                System.out.println("some err");
                System.out.println(e);
            }

            logger.debug("Text length: {}", text.length());
            logger.debug("Html length: {}", html.length());
            logger.debug("Number of outgoing links: {}", links.size());
        }

        Header[] responseHeaders = page.getFetchResponseHeaders();
        if (responseHeaders != null) {
            logger.debug("Response headers:");
            for (Header header : responseHeaders) {
                logger.debug("\t{}: {}", header.getName(), header.getValue());
            }
        }
        logger.debug("=============");
    }
}
