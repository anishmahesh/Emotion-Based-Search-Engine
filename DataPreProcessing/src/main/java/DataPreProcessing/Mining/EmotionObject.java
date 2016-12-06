package DataPreProcessing.Mining;

/**
 * Created by naman on 12/2/2016.
 */
public class EmotionObject {
    String name;
    double anger;
    double disgust;
    double fear;
    double joy;
    double sadness;

    public EmotionObject(String name, double anger, double disgust, double fear, double joy, double sadness) {
        this.name = name;
        this.anger = anger;
        this.disgust = disgust;
        this.fear = fear;
        this.joy = joy;
        this.sadness = sadness;
    }
}
