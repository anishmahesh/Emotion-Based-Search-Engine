package edu.nyu.cs.cs2580;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import edu.nyu.cs.cs2580.QueryHandler.CgiArguments.EmotionType;
import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Implement this class for HW2.
 */
public class IndexerInvertedCompressed extends Indexer implements Serializable {

  private static int FILE_COUNT_FOR_INDEX_SPLIT = 500;

  private static int TERM_COUNT_FOR_INDEX_SPLIT = 500;

  private int indexCount = 0;
  private int partCount = 0;

  private boolean endSearch = false;

  // Maps each term to their integer representation
  private Map<String, Integer> _dictionary = new HashMap<String, Integer>();
  // All unique terms appeared in corpus. Offsets are integer representations.
  public Vector<String> _terms = new Vector<String>();

  // Stores all Document in memory.
  private Vector<DocumentIndexed> _documents = new Vector<DocumentIndexed>();

  private Map<Integer, Vector<Integer>> _postings = new HashMap<>();

  private Map<Integer, Vector<Integer>> _joyPostings = new HashMap<>();
  private Map<Integer, Vector<Integer>> _sadPostings = new HashMap<>();
  private Map<Integer, Vector<Integer>> _funnyPostings = new HashMap<>();

  //Stores the index of each docid in posting list in sorted order(acc to doc ids)
  private Map<Integer,Vector<Integer>> _skipList = new HashMap<>();

  private Map<Integer,Vector<Integer>> _joySkipList = new HashMap<>();
  private Map<Integer,Vector<Integer>> _sadSkipList = new HashMap<>();
  private Map<Integer,Vector<Integer>> _funnySkipList = new HashMap<>();
  private HashMap<String, Integer> _startIndexes = new HashMap<>();

  public IndexerInvertedCompressed() {
  }

  public IndexerInvertedCompressed(Options options) {
    super(options);
    System.out.println("Using Indexer: " + this.getClass().getSimpleName());
  }

  @Override
  public void constructIndex() throws IOException {
    System.out.println("Starting indexing for Joy and Sad");

    String dataDir = _options._dataPrefix;
    String corpusDir = _options._corpusPrefix;
    String indexDir  = _options._indexPrefix;

    deleteExistingFile(indexDir);

    processDocFiles(dataDir, corpusDir, indexDir);

    System.out.println("Created partial indexes. Now merging them");

    mergeIndex();

    System.out.println("Splitting index file based on number of terms");

    splitIndexFile();

    System.out.println(
            "Indexed " + Integer.toString(_numDocs) + " docs with " +
                    Long.toString(_terms.size()) + " terms.");

    _postings = null;

    writeIndexerObjectToFile();

    compressMergedFiles(indexDir);

  }

  private List<String> stringTokenizer(String str) {
    List<String> tokenList = new ArrayList<String>();
    try {
      StringTokenizer st = new StringTokenizer(str, "\t");
      while (st.hasMoreElements()) {
        tokenList.add(st.nextElement().toString());
      }
    }catch (Exception e) {
      //IT is null
    }
    return tokenList;
  }

  public void compressDocTermFile(String dir, int docid, Vector<Integer> termIds) throws IOException {

    OutputStream os = new FileOutputStream(dir + "/" + docid, true);

    Vector<Byte> bytes1 = IndexCompressor.vByteEncoder(docid);

    byte[] bArr1 = new byte[bytes1.size()];

    for (int i = 0 ; i < bArr1.length; i++) {
      bArr1[i] = bytes1.get(i);
    }

    os.write(bArr1);

    Vector<Integer> terms = new Vector<>();

    int i = 0;
    while (i < termIds.size()){
      int offset = termIds.get(i);

      terms.add(offset);
      terms.add(termIds.get(i+1));

      i += 2;
    }

    Vector<Byte> bytes3 = IndexCompressor.vByteEncoder(terms);

    byte[] bArr3 = new byte[bytes3.size()];

    for (int j = 0 ; j < bArr3.length; j++) {
      bArr3[j] = bytes3.get(j);
    }

    os.write(bArr3);

    os.close();
  }

  public void compressMergedFiles(String dir) throws IOException {

    String joyDir = dir + "/joy";
    String sadDir = dir + "/sad";
    File joyDirectory = new File(joyDir);
    File sadDirectory = new File(sadDir);

    if (!joyDirectory.exists()) joyDirectory.mkdir();
    if (!sadDirectory.exists()) sadDirectory.mkdir();

    for (int part = 0; part <= partCount; part++) {
      File file = new File(dir + "/phase1/index-part-" + part + ".tsv");
      if (file.isFile() && !file.isHidden() && !file.getName().toLowerCase().contains("object.idx")) {
        OutputStream osjoy = new FileOutputStream(joyDir + "/index-comp-part-"+ part +".tsv", true);
        OutputStream osSad = new FileOutputStream(sadDir + "/index-comp-part-"+ part +".tsv", true);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
          String line;
          while ((line = br.readLine()) != null) {

            Vector<Byte> bytes1 = IndexCompressor.vByteEncoder(Integer.parseInt(line));

            byte[] bArr1 = new byte[bytes1.size()];

            for (int i = 0 ; i < bArr1.length; i++) {
              bArr1[i] = bytes1.get(i);
            }

            osjoy.write(bArr1);
            osSad.write(bArr1);

            line = br.readLine();
            List<String> strings = stringTokenizer(line);

            Vector<Byte> bytes2 = IndexCompressor.vByteEncoder(strings.size());

            byte[] bArr2 = new byte[bytes2.size()];

            for (int i = 0 ; i < bArr2.length; i++) {
              bArr2[i] = bytes2.get(i);
            }

            osjoy.write(bArr2);
            osSad.write(bArr2);

            Vector<Integer> joyPost = new Vector<>();
            Vector<Integer> sadPost = new Vector<>();
            Map<Integer, Vector<Integer>> docToOccs = new HashMap<>();
            Vector<Integer> docIds = new Vector<>();

            int idx = 0;
            while (idx < strings.size()) {
              String word = strings.get(idx);
              int did = Integer.parseInt(word);
              int occ = Integer.parseInt(strings.get(idx+1));
              Vector<Integer> postList = new Vector<>();

              for (int j = idx+1; j < idx + occ + 2; j++) {
                postList.add(Integer.parseInt(strings.get(j)));
              }
              docToOccs.put(did, postList);
              docIds.add(did);

              idx += occ + 2;
            }

            Collections.sort(docIds, Collections.reverseOrder(new joyComparator()));

            for (Integer docId : docIds) {
              joyPost.add(docId);
              joyPost.addAll(docToOccs.get(docId));
            }

            Collections.sort(docIds, Collections.reverseOrder(new SadComparator()));

            for (Integer docId : docIds) {
              sadPost.add(docId);
              sadPost.addAll(docToOccs.get(docId));
            }

            Vector<Byte> bytes3 = IndexCompressor.vByteEncoder(joyPost);

            byte[] bArr3 = new byte[bytes3.size()];

            for (int i = 0 ; i < bArr3.length; i++) {
              bArr3[i] = bytes3.get(i);
            }

            osjoy.write(bArr3);

            bytes3 = IndexCompressor.vByteEncoder(sadPost);

            bArr3 = new byte[bytes3.size()];

            for (int i = 0 ; i < bArr3.length; i++) {
              bArr3[i] = bytes3.get(i);
            }

            osSad.write(bArr3);
          }
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }

        osjoy.close();
        osSad.close();
      } else if (file.isDirectory()) {
        //not recursively going inside a directory
        continue;
        //processFiles(dir+file.getName());
      }
      file.delete();
    }

  }

  class joyComparator implements Comparator<Integer> {

    @Override
    public int compare(Integer docId1, Integer docId2) {
      if (_documents.get(docId1).getJoy() >= _documents.get(docId2).getJoy())
        return 1;
      else return -1;
    }
  }

  class SadComparator implements Comparator<Integer> {

    @Override
    public int compare(Integer docId1, Integer docId2) {
      if (_documents.get(docId1).getSadness() >= _documents.get(docId2).getSadness())
        return 1;
      else return -1;
    }
  }

  private void writeIndexerObjectToFile() throws IOException {
    String indexFile = _options._indexPrefix + "/objects.idx";
    System.out.println("Store other objects to: " + indexFile);
    ObjectOutputStream writer =
            new ObjectOutputStream(new FileOutputStream(indexFile));
    writer.writeObject(this);
    writer.close();
  }

  private void deleteExistingFile(String indexDir) {
    for(File file: new File(indexDir).listFiles()) {
      if (file.isDirectory()) {
        for (File subFile : file.listFiles()) {
          subFile.delete();
        }
      }
      file.delete();
    }
  }

  private void processDocFiles(String dataDir, String corpusDir, String indexDir) throws IOException {

    String indexPath = indexDir + "/phase1";
    File indexDirectory = new File(indexPath);

    if (!indexDirectory.exists()) {
      indexDirectory.mkdir();
    }

    String emotionsFile = dataDir + "/emotions.tsv";
    BufferedReader docReader = new BufferedReader(new FileReader(emotionsFile));

    String docsDir = indexDir + "/Documents";
    File docDirectory = new File(docsDir);

    if (!docDirectory.exists()) {
      docDirectory.mkdir();
    }

    int fileNum = 0;

    String line;
    while ((line = docReader.readLine()) != null) {
      List<String> cols = stringTokenizer(line);

      String docPath = corpusDir + "/" + cols.get(0);

      File docFile = new File(docPath);

      DocumentIndexed doc = new DocumentIndexed(_numDocs);
      ++_numDocs;

      DocTextFields docTextFields = getDocTextFields(docFile);


      doc.setTitle(docTextFields.title);
      doc.setUrl(docTextFields.url);

      doc.setAnger(Double.parseDouble(cols.get(1)));
      doc.setDisgust(Double.parseDouble(cols.get(2)));
      doc.setFear(Double.parseDouble(cols.get(3)));
      doc.setJoy(Double.parseDouble(cols.get(4)));
      doc.setSadness(Double.parseDouble(cols.get(5)));

      processDocument(docTextFields.bodyText, doc);

      _documents.add(doc);

      if (fileNum == FILE_COUNT_FOR_INDEX_SPLIT) {
        indexCount++;
        System.out.println("Constructing partial index number: " + indexCount);

        persistToFile(indexCount, indexPath);
        fileNum = 0;
      }

      fileNum++;
    }

    indexCount++;
    System.out.println("Constructing partial index number: " + indexCount);
    persistToFile(indexCount, indexPath);
  }

  private void processFiles(String dir) throws IOException {
    String indexPath = _options._indexPrefix;
    System.out.println("Inside Directory : "+ dir);
    File[] fileNames = new File(dir).listFiles();
    System.out.println("Construct index from: " + dir);
    HTMLParse htmlParse = new HTMLParse();

    String docsDir = _options._indexPrefix + "/Documents";
    File directory = new File(docsDir);

    if (!directory.exists()) {
      directory.mkdir();
    }

    //Map<String, Double> numViews = (Map<String, Double>) _logMiner.load();
    //Map<String, Double> pageRanks = (Map<String, Double>) _corpusAnalyzer.load();
    int fileNum = 0;

    for (File file : fileNames) {

      try {

        if (file.isFile() && !file.isHidden()) {
          HTMLDocument htmlDocument = htmlParse.getDocument(file);
          DocumentIndexed doc = new DocumentIndexed(_documents.size());

          processDocument(htmlDocument.getBodyText(), doc);

          doc.setTitle(htmlDocument.getTitle());
          doc.setUrl(htmlDocument.getUrl());
          //int currNumViews = numViews.get(file.getName()).intValue();
          //maxNumViews = currNumViews > maxNumViews ? currNumViews : maxNumViews;
          //doc.setNumViews(currNumViews);
          //float currPageRank = pageRanks.get(file.getName()).floatValue();
          //maxPageRank = currPageRank > maxPageRank ? currPageRank : maxPageRank;
          //doc.setPageRank(currPageRank);
          _documents.add(doc);
          ++_numDocs;

          if (fileNum == FILE_COUNT_FOR_INDEX_SPLIT) {
            indexCount++;
            System.out.println("Constructing partial index number: " + indexCount);

            persistToFile(indexCount, indexPath);
            fileNum = 0;
          }

          fileNum++;
        } else if (file.isDirectory()) {
          //not recursively going inside a directory
          continue;
          //processFiles(dir+file.getName());
        }
      } catch (Exception e) {

      }
    }

    indexCount++;
    System.out.println("Constructing partial index number: " + indexCount);
    persistToFile(indexCount, indexPath);
  }

  private void splitIndexFile() throws IOException {
    String indexFile = _options._indexPrefix + "/phase1/corpus.tsv";
    BufferedReader reader = new BufferedReader(new FileReader(indexFile));

    String partFile = _options._indexPrefix + "/phase1/index-part-0.tsv";
    BufferedWriter writer = new BufferedWriter(new FileWriter(partFile, true));

    int count = -1;
    String line;
    while((line = reader.readLine()) != null) {
      count++;

      if (count == TERM_COUNT_FOR_INDEX_SPLIT) {
        count = 0;
        partCount++;
        writer.close();
        partFile = _options._indexPrefix + "/phase1/index-part-" + partCount + ".tsv";
        writer = new BufferedWriter(new FileWriter(partFile, true));
        writer.flush();
      }

      writer.write(line + "\n");
      line = reader.readLine();
      writer.write(line + "\n");


    }

    reader.close();
    writer.close();
    File index = new File(indexFile);
    index.delete();
  }

  private void mergeIndex() throws IOException {
    String indexFile = _options._indexPrefix + "/phase1/corpus.tsv";
    String firstFile = _options._indexPrefix + "/phase1/tempIndex1.tsv";

    File index = new File(indexFile);
    File first = new File(firstFile);

    if (indexCount == 1) {
      first.renameTo(index);
    }

    for (int i = 2; i <= indexCount; i++) {
      String secondFile = _options._indexPrefix + "/phase1/tempIndex" + i + ".tsv";

      File second = new File(secondFile);

      File mergedFile = mergeFiles(first, second);

      first.delete();
      second.delete();
      mergedFile.renameTo(index);
      first = new File(indexFile);
    }

  }

  private File mergeFiles(File first, File second) throws IOException {
    String tempFile = _options._indexPrefix + "/phase1/temp.tsv";

    File temp = new File(tempFile);
    BufferedWriter writer = new BufferedWriter(new FileWriter(temp, true));

    BufferedReader firstReader = new BufferedReader(new FileReader(first));
    BufferedReader secondReader = new BufferedReader(new FileReader(second));

    String lineInFirstFile = firstReader.readLine();
    String lineInSecondFile = secondReader.readLine();

    while ((lineInFirstFile != null) && (lineInSecondFile != null)) {

      if (Integer.parseInt(lineInFirstFile) < Integer.parseInt(lineInSecondFile)) {
        writer.write(lineInFirstFile + "\n");
        lineInFirstFile = firstReader.readLine();
        writer.write(lineInFirstFile + "\n");

        lineInFirstFile = firstReader.readLine();
      }
      else if (Integer.parseInt(lineInSecondFile) > Integer.parseInt(lineInFirstFile)) {
        writer.write(lineInSecondFile + "\n");
        lineInSecondFile = secondReader.readLine();
        writer.write(lineInSecondFile + "\n");

        lineInSecondFile = secondReader.readLine();
      }
      else {
        writer.write(lineInFirstFile + "\n");
        lineInFirstFile = firstReader.readLine();
        lineInSecondFile = secondReader.readLine();
        writer.write(lineInFirstFile + "\t" + lineInSecondFile + "\n");

        lineInFirstFile = firstReader.readLine();
        lineInSecondFile = secondReader.readLine();
      }

    }

    while (lineInFirstFile != null) {
      writer.write(lineInFirstFile + "\n");
      lineInFirstFile = firstReader.readLine();
    }

    while (lineInSecondFile != null) {
      writer.write(lineInSecondFile + "\n");
      lineInSecondFile = secondReader.readLine();
    }

    firstReader.close();
    secondReader.close();
    writer.close();

    return temp;
  }

  private void persistToFile(int index, String path) throws IOException {
    String indexFile = path + "/tempIndex" + index + ".tsv";
    BufferedWriter writer = new BufferedWriter(new FileWriter(indexFile));

    List<Integer> termIds = new ArrayList<>();
    termIds.addAll(_postings.keySet());
    Collections.sort(termIds);

    for (Integer termId: termIds) {
      writer.write(termId.toString() + "\n");

      Vector<Integer> docOccs = _postings.get(termId);
      for (int i = 0; i < docOccs.size(); i++) {

        writer.write(docOccs.get(i).toString() + "\t");
      }
      writer.write("\n");
    }

    writer.close();
    _postings.clear();
  }

  private void processDocument(String content, DocumentIndexed doc) throws IOException {
    Scanner s = new Scanner(content);

    Map<String, Vector<Integer>> termOccurenceMap = new HashMap<>();

    int offset = 0;
    Stemmer stemmer = new Stemmer();
    while (s.hasNext()) {
      String term = s.next();
      stemmer.add(term.toCharArray(), term.length());
      stemmer.stem();
      term = stemmer.toString();

      if (!termOccurenceMap.containsKey(term)) {
        Vector<Integer> occurence = new Vector<>();
        occurence.add(doc._docid);
        occurence.add(1);
        occurence.add(offset);
        termOccurenceMap.put(term, occurence);
      }
      else {
        Vector<Integer> occurence = termOccurenceMap.get(term);
        occurence.set(1, occurence.get(1) + 1);
        occurence.add(offset);
      }
      offset++;
    }

    doc.setTotalTerms(offset);

    Vector<Integer> termIds = new Vector<>();

    for (String token : termOccurenceMap.keySet()) {
      int idx;
      if (_dictionary.containsKey(token)) {
        idx = _dictionary.get(token);
      } else {
        idx = _terms.size();
        _terms.add(token);
        _dictionary.put(token, idx);
      }

      termIds.add(idx);
      termIds.add(termOccurenceMap.get(token).get(1));

      if (_postings.containsKey(idx)) {
        _postings.get(idx).addAll(termOccurenceMap.get(token));
      }
      else {
        _postings.put(idx, termOccurenceMap.get(token));
      }

    }

    String docsDir = _options._indexPrefix + "/Documents";

    compressDocTermFile(docsDir, doc._docid, termIds);

    s.close();
  }

  @Override
  public void loadIndex() throws IOException, ClassNotFoundException {
    String indexFile = _options._indexPrefix + "/objects.idx";
    System.out.println("Loading index objects other than postings list from: " + indexFile);

    ObjectInputStream reader =
            new ObjectInputStream(new FileInputStream(indexFile));
    IndexerInvertedCompressed loaded = (IndexerInvertedCompressed) reader.readObject();

    this._dictionary = loaded._dictionary;
    this._terms = loaded._terms;
    this._documents = loaded._documents;
    this.partCount = loaded.partCount;

    // Compute numDocs and totalTermFrequency b/c Indexer is not serializable.
    this._numDocs = _documents.size();
    for (Document doc : _documents) {
      this._totalTermFrequency += ((DocumentIndexed) doc).getTotalTerms();
    }

    reader.close();
  }

  @Override
  public Document getDoc(int docid) {
    return _documents.get(docid);
  }

  /**
   * In HW2, you should be using {@link DocumentIndexed}.
   */
  @Override
  public Document nextDoc(Query query, int docid){
    return null;
  }

  public NextDoc nextDocForEmotion(Query query, int endIndex) {
    if(query instanceof QueryPhrase){
      return nextDocPhrase((QueryPhrase) query, query._emotionType, endIndex);
    } else {
      return nextDocIndividualTokens(query._tokens, query._emotionType, endIndex);
    }
  }

  public NextDoc nextDocIndividualTokens(Vector<String> queryTokens, EmotionType emotionType, int endIndex) {
    HashSet<Integer> commonDocid = new HashSet<>();
    endSearch = false;
    if(queryTokens.size() < 1 ){
      endSearch = true;
      return new NextDoc(endSearch,null);
    }

    if (!_dictionary.containsKey(queryTokens.get(0))) {
      return null;
    }

    loadTermIfNotLoaded(queryTokens.get(0), emotionType);
    Vector <Integer> PostingList = getPostingListforTerm(queryTokens.get(0), emotionType);
    Vector <Integer> SkipList = getSkipListforTerm(queryTokens.get(0), emotionType);

    for(int i=0, k=0; k<=endIndex ;i += PostingList.get(i+1) + 2, k++){
      if(k >= SkipList.size()){
        endSearch = true;
        break;
      }
      commonDocid.add(PostingList.get(i));
    }

    if(queryTokens.size() == 1){
      Vector<Document> documents = new Vector<>();
      for (int id: commonDocid){
        documents.add(_documents.get(id));
      }
      return new NextDoc(endSearch,documents);
    }

    for(int i=1; i<queryTokens.size(); i++) {
      if (!_dictionary.containsKey(queryTokens.get(i))) {
        return null;
      }
      loadTermIfNotLoaded(queryTokens.get(i), emotionType);
      PostingList = getPostingListforTerm(queryTokens.get(i), emotionType);
      SkipList = getSkipListforTerm(queryTokens.get(i), emotionType);
      HashSet<Integer> termPostingList = new HashSet<>();

      for (int j = 0, k = 0; k <= endIndex; j += PostingList.get(j + 1) + 2, k++) {
        if (k >= SkipList.size()) {
          endSearch = true;
          break;
        }
        termPostingList.add(PostingList.get(j));
      }

      commonDocid.retainAll(termPostingList);
      if(commonDocid.size() < 1){
        return new NextDoc(endSearch,null);
      }
    }

    Vector<Document> documents = new Vector<>();
    for (int id: commonDocid){
      documents.add(_documents.get(id));
    }
    return new NextDoc(endSearch,documents);
  }

  public NextDoc nextDocPhrase(QueryPhrase query, EmotionType emotionType, int endIndex){
    Vector<String> queryTokens = new Vector<>();
    for(String term: query._tokens){
      queryTokens.add(term);
    }
    for(Vector<String> phraseTerm: query._phraseTokens){
      for(String term: phraseTerm){
        queryTokens.add(term);
      }
    }

    NextDoc nextDoc = nextDocIndividualTokens(queryTokens,emotionType,endIndex);

    if(nextDoc.documnets != null){
      Vector<Document> documents = new Vector<>();
      for(Document document: nextDoc.documnets){
        if(checkConsecutivePhrase(query._phraseTokens,emotionType,document._docid)){
          documents.add(document);
        }
      }
      if (documents.size()<1){
        return new NextDoc(nextDoc.stopRepeat,null);
      } else {
        return new NextDoc(nextDoc.stopRepeat, documents);
      }
    } else {
      return new NextDoc(nextDoc.stopRepeat,null);
    }
  }

  private boolean checkConsecutivePhrase(Vector<Vector<String>> phraseTerms, EmotionType emotionType ,int docid){
    boolean contains = true;
    HashSet<Integer> storePhrasePositionSet = new HashSet<>();
    for(Vector<String> phraseTerm: phraseTerms){
      if(!phraseExists(phraseTerm,docid,emotionType)){
        contains = false;
        break;
      }
    }
    return contains;
  }

  private boolean phraseExists(Vector<String> phraseTerm, int docid, EmotionType emotionType) {
    HashMap<String,Vector<Integer>> termPositionMap = getTermPositionMap(phraseTerm,emotionType,docid);
    String firstTerm = phraseTerm.get(0);
    for (int firstPos : termPositionMap.get(firstTerm)) {
      int i;
      for (i = 1; i < phraseTerm.size(); i++) {
        if (!termPositionMap.get(phraseTerm.get(i)).contains(firstPos + i)) {
          break;
        }
      }
      if (i == phraseTerm.size()) {
        return true;
      }
    }
    return  false;
  }

  private HashMap<String, Vector<Integer>> getTermPositionMap(Vector<String> phraseTerm, EmotionType emotionType ,int docid){
    HashMap<String,Vector<Integer>> termPositionMap= new HashMap<>();
    for(String term: phraseTerm) {
      Vector<Integer> PostingList = getPostingListforTerm(term, emotionType);
      Vector<Integer> termPositionVector = new Vector<>();
      int j = 0;
      for (; j < PostingList.size() - 1; j += PostingList.get(j + 1) + 2) {
        if (PostingList.get(j) == docid) {
          break;
        }
      }
      for (int i = 0; i < PostingList.get(j + 1); i++) {
        termPositionVector.add(PostingList.get(j + 2 + i));
      }
      termPositionMap.put(term, termPositionVector);
    }
    return termPositionMap;
  }

  private Vector<Integer> getPostingListforTerm(String term, EmotionType emotionType){
    return getPostingByEmotion(emotionType).get(_dictionary.get(term));
  }

  private Vector<Integer> getSkipListforTerm(String term, EmotionType emotionType){
    return getSkipListByEmotion(emotionType).get(_dictionary.get(term));
  }

  @Override
  public int corpusDocFrequencyByTerm(String term, EmotionType emotionType) {
    loadTermIfNotLoaded(term, emotionType);
    Vector<Integer> PostingList = getPostingListforTerm(term, emotionType);
    int corpusDocFrequencyByTerm = 0;
    for(int i=0; i< PostingList.size()-1;){
      corpusDocFrequencyByTerm++;
      i += PostingList.get(i+1) + 2;
    }
    return corpusDocFrequencyByTerm;
  }

  @Override
  public int corpusTermFrequency(String term, EmotionType emotionType) {
    Vector<Integer> PostingList = getPostingListforTerm(term, emotionType);
    int corpusTermFrequency = 0;
    for(int i=0; i< PostingList.size()-1;){
      corpusTermFrequency += PostingList.get(i+1);
      i += PostingList.get(i+1) + 2;
    }
    return corpusTermFrequency;
  }

  /**
   * @CS2580: Implement this to work with your RankerFavorite.
   */
  @Override
  public int documentTermFrequency(String term, int docid, EmotionType emotionType) {
    Vector<Integer> PostingList = getPostingListforTerm(term, emotionType);
    for(int i=0; i< PostingList.size()-1;){
      if(docid == PostingList.get(i)){
        return  PostingList.get(i+1);
      } else {
        i += PostingList.get(i+1) + 2;
      }
    }
    return 0;
  }

  private void loadTermIfNotLoaded(String term, EmotionType emotionType) {
    if (!getPostingByEmotion(emotionType).containsKey(_dictionary.get(term))) {
      try {
        loadIndexOnFlyForTerm(term, emotionType);
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
  }

  private void loadIndexOnFlyForTerm(String term, EmotionType emotionType) throws IOException, ClassNotFoundException {
    int termId = _dictionary.get(term);
    loadMiniIndex(termId/TERM_COUNT_FOR_INDEX_SPLIT, emotionType);
  }

  private void loadMiniIndex(int indexNo, EmotionType emotionType) throws IOException {

    Map<Integer, Vector<Integer>> postingByEmotion = getPostingByEmotion(emotionType);
    Map<Integer, Vector<Integer>> skipListByEmotion = getSkipListByEmotion(emotionType);

    String idxDir = _options._indexPrefix + "/" + emotionType.name().toLowerCase();

    File idxFolder = new File(idxDir);
    File[] indexFiles = idxFolder.listFiles();

    if (indexNo < indexFiles.length) {
      String fileName = idxDir + "/index-comp-part-" + indexNo + ".tsv";

      byte[] bytes = Files.readAllBytes(new File(fileName).toPath());


      Vector<Byte> vb = new Vector<>();
      for (byte b : bytes) {
        vb.add(b);
      }

      Vector<Integer> numbers = IndexCompressor.vByteDecoder(vb);


      int i = 0;

      while (i < numbers.size()) {
        int termId = numbers.get(i);

        int postingSize = numbers.get(i+1);

        Vector<Integer> termPostingList = new Vector<>();

        Integer num;
        int j;
        for (j = i+2; j < i+2+postingSize; j++) {
          num = numbers.get(j);
          termPostingList.add(num);
        }

        postingByEmotion.put(termId, termPostingList);

        Vector<Integer> skipPtrs = new Vector<>();
        int k = 0;
        while (k < termPostingList.size()) {
          skipPtrs.add(k);
          k += termPostingList.get(k + 1) + 2;
        }

        skipListByEmotion.put(termId, skipPtrs);

        i=j;
      }
    }
  }

  private Map<Integer, Vector<Integer>> getPostingByEmotion(EmotionType emotionType) {
    switch (emotionType) {
      case JOY: return _joyPostings;
      case SAD: return _sadPostings;
      case FUNNY:
      default: return _funnyPostings;
    }
  }

  private Map<Integer, Vector<Integer>> getSkipListByEmotion(EmotionType emotionType) {
    switch (emotionType) {
      case JOY: return _joySkipList;
      case SAD: return _sadSkipList;
      case FUNNY:
      default: return _funnySkipList;
    }
  }

  private DocTextFields getDocTextFields(File doc) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(doc));
    String title = br.readLine();
    String url = br.readLine();
    String temp ;
    StringBuilder bodyText = new StringBuilder();
    while((temp = br.readLine()) != null){
      bodyText.append("\n"+temp);
    }
    br.close();
    String processedBodyText = TextProcessor.regexRemoval(bodyText.toString().toLowerCase());

    DocTextFields dtf = new DocTextFields();
    dtf.title = title;
    dtf.url = url;
    dtf.bodyText = processedBodyText;

    return dtf;
  }

  class DocTextFields{
    String title;
    String url;
    String bodyText;

    public DocTextFields() {
    }
  }
}
