package edu.nyu.cs.cs2580;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * Handles each incoming query, students do not need to change this class except
 * to provide more query time CGI arguments and the HTML output.
 *
 * N.B. This class is not thread-safe. 
 *
 * @author congyu
 * @author fdiaz
 */
class QueryHandler implements HttpHandler {

  /**
   * CGI arguments provided by the user through the URL. This will determine
   * which Ranker to use and what output format to adopt. For simplicity, all
   * arguments are publicly accessible.
   */
  public static class CgiArguments {
    // The raw user query
    public String _query = "";
    // How many results to return
    private int _numResults = 10;

    // The type of the ranker we will be using.
    public enum RankerType {
      FAVORITE,
      FAVORITEFOREMOTION,
    }

    public RankerType _rankerType = RankerType.FAVORITE;

    private RankerType getRankerByEmotion(EmotionType emotionType){
      switch (emotionType) {
        case JOY: return RankerType.FAVORITEFOREMOTION;
        case SAD: return RankerType.FAVORITEFOREMOTION;
        case FUNNY:
        default: return RankerType.FAVORITE;
      }
    }

    public enum EmotionType {
      FUNNY,
      SAD,
      JOY,
    }

    public EmotionType _emotionType = EmotionType.FUNNY;

    // The output format.
    public enum OutputFormat {
      TEXT,
      HTML,
    }
    public OutputFormat _outputFormat = OutputFormat.TEXT;

    public CgiArguments(String uriQuery) {
      String[] params = uriQuery.split("&");
      for (String param : params) {
        String[] keyval = param.split("=", 2);
        if (keyval.length < 2) {
          continue;
        }
        String key = keyval[0].toLowerCase();
        String val = keyval[1];
        if (key.equals("query")) {
          _query = val;
        } else if (key.equals("num")) {
          try {
            _numResults = Integer.parseInt(val);
          } catch (NumberFormatException e) {
            // Ignored, search engine should never fail upon invalid user input.
          }
        } else if (key.equals("format")) {
          try {
            _outputFormat = OutputFormat.valueOf(val.toUpperCase());
          } catch (IllegalArgumentException e) {
            // Ignored, search engine should never fail upon invalid user input.
          }
        } else if ( key.toLowerCase().equals("emotion")){
          try {
            _emotionType = EmotionType.valueOf(val.toUpperCase());
            _rankerType = getRankerByEmotion(_emotionType);
          } catch (IllegalArgumentException e) {
            // Ignored, search engine should never fail upon invalid user input.
          }
        }
      }  // End of iterating over params
    }
  }

  // For accessing the underlying documents to be used by the Ranker. Since
  // we are not worried about thread-safety here, the Indexer class must take
  // care of thread-safety.
  private Indexer _indexer;
  private Indexer _indexerFunny;

  private Indexer getIndexerByEmotion(CgiArguments.EmotionType emotionType){
    switch (emotionType) {
      case JOY: return _indexer;
      case SAD: return _indexer;
      case FUNNY:
      default: return _indexerFunny;
    }
  }

  public QueryHandler(Options options, Indexer indexer, Indexer funnyIndexer) {
    _indexer = indexer;
    _indexerFunny = funnyIndexer;
  }

  private void respondWithMsg(HttpExchange exchange, final String message)
          throws IOException {
    Headers responseHeaders = exchange.getResponseHeaders();
    responseHeaders.set("Content-Type", "text/html");
    exchange.sendResponseHeaders(200, 0); // arbitrary number of bytes
    OutputStream responseBody = exchange.getResponseBody();
    responseBody.write(message.getBytes());
    responseBody.close();
  }

  private void constructTextOutput(
          final Vector<ScoredDocument> docs, StringBuffer response) {
    for (ScoredDocument doc : docs) {
      response.append(response.length() > 0 ? "\n" : "");
      response.append(doc.asTextResult());
    }
    response.append(response.length() > 0 ? "\n" : "");
  }

  private void constructHTMLOutput(
          final Vector<ScoredDocument> docs, StringBuffer response) {
    HTMLOutputFormatter htmlOutput = new HTMLOutputFormatter();
    response.append(htmlOutput.getHeader());
    for (ScoredDocument doc : docs) {
      response.append(response.length() > 0 ? "\n" : "");
      response.append(doc.asHtmlResult());
    }
    response.append(htmlOutput.getFooter());
    response.append(response.length() > 0 ? "\n" : "No result returned!");
  }

  public void handle(HttpExchange exchange) throws IOException {
    String requestMethod = exchange.getRequestMethod();
    if (!requestMethod.equalsIgnoreCase("GET")) { // GET requests only.
      return;
    }

    // Print the user request header.
    Headers requestHeaders = exchange.getRequestHeaders();
    System.out.print("Incoming request: ");
    for (String key : requestHeaders.keySet()) {
      System.out.print(key + ":" + requestHeaders.get(key) + "; ");
    }
    System.out.println();

    // Validate the incoming request.
    String uriQuery = exchange.getRequestURI().getQuery();
    String uriPath = exchange.getRequestURI().getPath();
    if (uriPath == null || uriQuery == null) {
      respondWithMsg(exchange, "Something wrong with the URI!");
    }
    if (!uriPath.equals("/search")) {
      respondWithMsg(exchange, "Only /search is handled!");
    }
    System.out.println("Query: " + uriQuery);

    // Process the CGI arguments.
    CgiArguments cgiArgs = new CgiArguments(uriQuery);
    if (cgiArgs._query.isEmpty()) {
      respondWithMsg(exchange, "No query is given!");
    }

    // Create the ranker.
    Ranker ranker = Ranker.Factory.getRankerByArguments(
              cgiArgs, SearchEngine.OPTIONS, getIndexerByEmotion(cgiArgs._emotionType));
    if (ranker == null) {
      respondWithMsg(exchange,
              "Ranker " + cgiArgs._rankerType.toString() + " is not valid!");
    }

    Vector<ScoredDocument> scoredDocs = null;
    // Processing the query.
    if(cgiArgs._query.matches(".*?\".*\".*?")){
      QueryPhrase processedQuery = new QueryPhrase(cgiArgs._query, cgiArgs._emotionType);
      processedQuery.processQuery();
      scoredDocs =
              ranker.runQuery(processedQuery, cgiArgs._numResults);
    } else {
      Query processedQuery = new Query(cgiArgs._query, cgiArgs._emotionType);
      processedQuery.processQuery();
      scoredDocs =
              ranker.runQuery(processedQuery, cgiArgs._numResults);
    }


    // Ranking.
    StringBuffer response = new StringBuffer();
    switch (cgiArgs._outputFormat) {
      case TEXT:
        constructTextOutput(scoredDocs, response);
        break;
      case HTML:
        constructHTMLOutput(scoredDocs,response);
        // @CS2580: Plug in your HTML output
        break;
      default:
        // nothing
    }
    respondWithMsg(exchange, response.toString());
    System.out.println("Finished query: " + cgiArgs._query);
  }
}

