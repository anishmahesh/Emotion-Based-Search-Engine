package DataPreProcessing.Mining;

import DataPreProcessing.Mining.HTMLParse;
import com.ibm.watson.developer_cloud.alchemy.v1.AlchemyLanguage;
import com.ibm.watson.developer_cloud.alchemy.v1.model.DocumentEmotion;
import com.ibm.watson.developer_cloud.alchemy.v1.model.DocumentSentiment;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by naman on 12/2/2016.
 */
public class Emotion {

    public void fileResult(String key) throws IOException {
        String dir = "data/crawled";
        File[] fileNames = new File(dir).listFiles();
        HTMLParse htmlParse = new HTMLParse();
        Map<String, DocumentEmotion> fileEmotion = new HashMap();
        for (File file : fileNames) {
            try {
                if (file.isFile() && !file.isHidden()) {
                    String text = htmlParse.getDocument(file);
                    fileEmotion.put(file.getName(), getEmotion(text, key));
                }
            } catch (Exception e){

            }
        }
        String outputPath =   "data/analyzed/emotions.tsv";
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputPath));
        for(String docName : fileEmotion.keySet()) {
            DocumentEmotion.Emotion emotions = fileEmotion.get(docName).getEmotion();
            bufferedWriter.write(docName + "\t" + emotions.getAnger() + "\t" + emotions.getDisgust() + "\t" + emotions.getFear() + "\t" + emotions.getJoy() + "\t" + emotions.getSadness());
            bufferedWriter.newLine();
            System.out.print(emotions);
        }
        bufferedWriter.close();
    }

    private DocumentEmotion getEmotion(String text, String key) {
        AlchemyLanguage service = new AlchemyLanguage();
        service.setApiKey(key);

        Map<String,Object> params = new HashMap<String, Object>();
        params.put(AlchemyLanguage.TEXT, text);
        DocumentEmotion emotion = service.getEmotion(params).execute();
        DocumentSentiment sentiment = service.getSentiment(params).execute();
        return emotion;
    }

    public void scoreSortDocs() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("data/analyzed/emotions.tsv"));
        String line;
        String splits[];
        ArrayList<HappyObject> docHappy = new ArrayList<HappyObject>();
        ArrayList<SadObject> docSad = new ArrayList<SadObject>();
        while ((line = bufferedReader.readLine()) != null) {
            splits = line.split("\t");
            HappyObject happyObject = new HappyObject(splits[0], Double.parseDouble(splits[1]), Double.parseDouble(splits[2]), Double.parseDouble(splits[3]), Double.parseDouble(splits[4]),Double.parseDouble(splits[5]));
            SadObject sadObject = new SadObject(splits[0],Double.parseDouble(splits[1]), Double.parseDouble(splits[2]), Double.parseDouble(splits[3]), Double.parseDouble(splits[4]),Double.parseDouble(splits[5]));
            docHappy.add(happyObject);
            docSad.add(sadObject);
        }
        docHappy = normalizeObjects(docHappy);
        docSad = normalizeObjects(docSad);
        Collections.sort(docHappy, Collections.reverseOrder());
        Collections.sort(docSad, Collections.reverseOrder());

        createFile(docHappy, "data/analyzed/happy.tsv");
        createFile(docSad, "data/analyzed/sad.tsv");
    }

    private <T extends  EmotionObject> ArrayList<T> normalizeObjects(ArrayList<T> docEmotions) {
        return docEmotions;
    }

    public <T extends  EmotionObject> void createFile (ArrayList<T> docs, String outputPath) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputPath));
        for(T doc : docs){
            bufferedWriter.write(doc.name + "\t" + doc.anger + "\t" + doc.disgust + "\t" + doc.fear + "\t" + doc.joy + "\t" + doc.sadness);
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
    }
}
