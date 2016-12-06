package DataPreProcessing.Mining;

/**
 * Created by naman on 12/2/2016.
 */
public class HappyObject extends EmotionObject implements Comparable<HappyObject> {

    public HappyObject(String docName, double anger, double disgust, double fear, double joy, double sadness){
        super(docName, anger, disgust, fear, joy, sadness);
    }

    public int compareTo(HappyObject o) {
        if (this.joy > o.joy){
            return 0;
        }
        return (this.joy > o.joy) ? 1 : -1;
    }
}
