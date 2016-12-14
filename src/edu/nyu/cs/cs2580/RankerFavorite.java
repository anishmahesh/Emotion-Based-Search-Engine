package edu.nyu.cs.cs2580;

import java.util.*;

import edu.nyu.cs.cs2580.QueryHandler.CgiArguments;
import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Implement this class for HW2 based on a refactoring of your favorite
 * Ranker (except RankerPhrase) from HW1. The new Ranker should no longer rely
 * on the instructors' {@link IndexerFullScan}, instead it should use one of
 * your more efficient implementations.
 */
public class RankerFavorite extends Ranker {

  public RankerFavorite(Options options,
                        CgiArguments arguments, Indexer indexer) {
    super(options, arguments, indexer);
    System.out.println("Using Ranker: " + this.getClass().getSimpleName());
  }

  @Override
  public Vector<ScoredDocument> runQuery(Query query, int numResults) {
    Queue<ScoredDocument> rankQueue = new PriorityQueue<ScoredDocument>();
    Document doc = null;
    int docid = -1;
    int documentFetched = 0;
    while ((doc = _indexer.nextDoc(query, docid)) != null) {
      documentFetched++;
      rankQueue.add(scoreDocument(doc,query));
      if (rankQueue.size() > query._pagination  * numResults) {
        rankQueue.poll();
      }
      docid = doc._docid;
    }

    if(documentFetched > query._pagination * numResults){
      CgiArguments.moreDocFlag = true;
    } else {
      CgiArguments.moreDocFlag = false;
    }

    Vector<ScoredDocument> results = new Vector<ScoredDocument>();
    ScoredDocument scoredDoc = null;
    while ((scoredDoc = rankQueue.poll()) != null) {
      results.add(scoredDoc);
    }

    Collections.sort(results, Collections.reverseOrder());

    Vector<ScoredDocument> finalResults = new Vector<ScoredDocument>();
    for(int i = (query._pagination -1) * numResults; i< query._pagination *numResults && i<results.size(); i++ ) {
      finalResults.add(results.get(i));
    }
    return finalResults;
  }

  /*
    Scores Document based on Query Likelyhood probability
 */
  private ScoredDocument scoreDocument(Document doc, Query query) {


    double queryLikelyhoodProbability = 1.0;
    double totalTermsInDoc = ((DocumentIndexed)doc).getTotalTerms();
    double totalTermsInCourpus = _indexer.totalTermFrequency();
    double lambda = 0.5;

    for(String queryToken : query._tokens){
      double termFrequency = _indexer.documentTermFrequency(queryToken,doc._docid, query._emotionType);
      double corpusTermFrequency = _indexer.corpusDocFrequencyByTerm(queryToken, query._emotionType);
      queryLikelyhoodProbability *= (1-lambda)*(termFrequency/totalTermsInDoc)+(lambda)*(corpusTermFrequency/totalTermsInCourpus);
    }

    if (query instanceof QueryPhrase) {
      for (Vector<String> phraseTokens : ((QueryPhrase) query)._phraseTokens) {
        for(String queryToken : phraseTokens){
          double termFrequency = _indexer.documentTermFrequency(queryToken,doc._docid, query._emotionType);
          double corpusTermFrequency = _indexer.corpusDocFrequencyByTerm(queryToken, query._emotionType);
          queryLikelyhoodProbability *= (1-lambda)*(termFrequency/totalTermsInDoc)+(lambda)*(corpusTermFrequency/totalTermsInCourpus);
        }
      }
    }

    //Adding weightage to the title
    String[] tokens = doc.getTitle().toLowerCase().split(" ");
    String titleTokens = TextProcessor.regexRemoval(tokens.toString());
    System.out.println();

    for(String queryToken : query._tokens){
      if(titleTokens.contains(queryToken.toLowerCase())){
        queryLikelyhoodProbability += 1.33 * queryLikelyhoodProbability;
      }
    }
    return new ScoredDocument(doc, queryLikelyhoodProbability);
  }
}
