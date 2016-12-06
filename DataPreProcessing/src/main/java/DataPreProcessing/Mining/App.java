package DataPreProcessing.Mining;

import java.io.IOException;

public class App 
{
    public static void main( String[] args ) throws IOException {
        //Emotion mineEmotion = new Emotion();
        String key = "747b1889e65d247e1c1c1ba05d5ff016bda79ba0";
        //mineEmotion.fileResult(key);
        //mineEmotion.scoreSortDocs();
        Test t = new Test();
        t.testAPI("Resources/Images/t2.jpg" ,key);
    }
}
