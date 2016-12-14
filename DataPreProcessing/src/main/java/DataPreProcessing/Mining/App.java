package DataPreProcessing.Mining;

import java.io.IOException;
import java.util.Vector;

public class App 
{
    public static void main( String[] args ) throws IOException {
        Emotion mineEmotion = new Emotion();
        Vector<String> keys = new Vector<String>();
        keys.add("5521cbde39fd9d2a93c0243ea147810ce4c1ef2b");
        keys.add("747b1889e65d247e1c1c1ba05d5ff016bda79ba0");
        keys.add("0eff30041608d2aea3b505c2eadbe3e9c68ae2e5");
        mineEmotion.fileResult(keys);
        //mineEmotion.scoreSortDocs();
        //Test t = new Test();
        //t.testAPI("Resources/Images/t2.jpg" ,key);
    }
}
