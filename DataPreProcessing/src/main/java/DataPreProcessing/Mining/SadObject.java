package DataPreProcessing.Mining;

/**
 * Created by naman on 12/2/2016.
 */
public class SadObject extends EmotionObject implements Comparable<SadObject> {
    public SadObject(String docName, double anger, double disgust, double fear, double joy, double sadness){
        super(docName, anger, disgust, fear, joy, sadness);
    }

    public int compareTo(SadObject o) {
        if (this.sadness > o.sadness){
            return 0;
        }
        return (this.sadness > o.sadness) ? 1 : -1;
    }
}
