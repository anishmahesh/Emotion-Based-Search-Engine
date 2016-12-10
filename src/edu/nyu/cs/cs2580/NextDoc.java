package edu.nyu.cs.cs2580;


import java.util.HashMap;

/**
 * Created by naman on 12/9/2016.
 */
public class NextDoc {
    boolean stopRepeat;
    HashMap<String,Integer> prevDocIndex;
    Document doc;

    public NextDoc(boolean stopRepeat, Document doc, HashMap<String, Integer> prevDocIndex){
        this.stopRepeat = stopRepeat;
        this.doc = doc;
        this.prevDocIndex = prevDocIndex;
    }
}
