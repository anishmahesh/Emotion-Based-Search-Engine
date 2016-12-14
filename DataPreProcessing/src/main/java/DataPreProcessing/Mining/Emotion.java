package DataPreProcessing.Mining;

import com.ibm.watson.developer_cloud.alchemy.v1.AlchemyLanguage;
import com.ibm.watson.developer_cloud.alchemy.v1.model.DocumentEmotion;
import com.ibm.watson.developer_cloud.alchemy.v1.model.DocumentSentiment;

import java.io.*;
import java.util.*;

/**
 * Created by naman on 12/2/2016.
 */
public class Emotion {

    public void fileResult(Vector<String> keys) throws IOException {
        String dir = "data/HuffingpostOpinion";
        File[] fileNames = new File(dir).listFiles();
        HTMLParse htmlParse = new HTMLParse();
        Map<String, DocumentEmotion> fileEmotion = new HashMap();
        int i=0,j=0;
        String key = keys.get(0);
        for (File file : fileNames) {
            try {
                i++;
                if(i>995){
                    i=0;
                    key = keys.get(++j);
                }
                if (file.isFile() && !file.isHidden()) {
                    DocTextFields dtf = getDocTextFields(file);
                    String text = dtf.title + "\n" + dtf.bodyText;
                    fileEmotion.put(file.getName(), getEmotion(text, key));
                }
            } catch (Exception e){

            }
        }
        String outputPath =   "data/HuffingpostOpinion/emotions.tsv";
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputPath));
        for(String docName : fileEmotion.keySet()) {
            DocumentEmotion.Emotion emotions = fileEmotion.get(docName).getEmotion();
            bufferedWriter.write(docName + "\t" + emotions.getAnger() + "\t" + emotions.getDisgust() + "\t" + emotions.getFear() + "\t" + emotions.getJoy() + "\t" + emotions.getSadness());
            bufferedWriter.newLine();
            System.out.print(emotions);
        }
        bufferedWriter.close();
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

        DocTextFields dtf = new DocTextFields();
        dtf.title = title;
        dtf.url = url;
        dtf.bodyText = bodyText.toString();

        return dtf;
    }

    class DocTextFields{
        String title;
        String url;
        String bodyText;

        public DocTextFields() {
        }
    }

    private DocumentEmotion getEmotion(String text, String key) {
        AlchemyLanguage service = new AlchemyLanguage();
        service.setApiKey(key);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(AlchemyLanguage.TEXT, text);
        DocumentEmotion emotion = service.getEmotion(params).execute();
        DocumentSentiment sentiment = service.getSentiment(params).execute();
        return emotion;
    }

    public void scoreSortDocs() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("data/emotions.tsv"));
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

        createFile(docHappy, "data/happy.tsv");
        createFile(docSad, "data/sad.tsv");
    }

    private <T extends  EmotionObject> ArrayList<T> normalizeObjects(ArrayList<T> docEmotions) {
        return docEmotions;
    }

    public <T extends  EmotionObject> void createFile (ArrayList<T> docs, String outputPath) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputPath));
        for(T doc : docs){
            bufferedWriter.write(doc.name + "\t" + "\t" +  + doc.joy + "\t" + "\t"  + doc.sadness);
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
    }
}
