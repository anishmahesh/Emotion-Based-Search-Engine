package edu.nyu.cs.cs2580;

import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;

/**
 * Created by naman on 12/6/2016.
 */
public class RankerFavoriteforEmotion extends Ranker{
    public RankerFavoriteforEmotion(SearchEngine.Options options,
                          QueryHandler.CgiArguments arguments, Indexer indexer) {
        super(options, arguments, indexer);
        System.out.println("Using Ranker: " + this.getClass().getSimpleName());
    }

    @Override
    public Vector<ScoredDocument> runQuery(Query query, int numResults) {
        Queue<ScoredDocument> rankQueue = new PriorityQueue<ScoredDocument>();
        Document doc = null;
        int docid = -1;
        int startIndex = 0;
        int endIndex = Integer.parseInt(_options._postingThreshold);
        int threshold = Integer.parseInt(_options._postingThreshold);
        int multiplier = Integer.parseInt(_options._multiplierForThreshold);
        int docFetched = 0;
        Vector<ScoredDocument> finalResult = new Vector<>();
        while(true) {
            while ((doc = _indexer.nextDoc(query, docid)) != null &&  docFetched <= threshold) {
                docFetched++;
                rankQueue.add(scoreDocument(doc, query));
                if (rankQueue.size() > numResults) {
                    rankQueue.poll();
                }
                docid = doc._docid;
            }

            docFetched = 0;
            threshold = threshold * multiplier;

            Vector<ScoredDocument> results = new Vector<ScoredDocument>();
            ScoredDocument scoredDoc = null;
            while ((scoredDoc = rankQueue.poll()) != null) {
                results.add(scoredDoc);
            }
            Collections.sort(results, Collections.reverseOrder());

            for(ScoredDocument resultDoc: results){
                finalResult.add(resultDoc);
            }
            if(finalResult.size() == numResults || doc == null){
                break;
            }
        }

        return finalResult;
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

        return new ScoredDocument(doc, queryLikelyhoodProbability);
    }

}
