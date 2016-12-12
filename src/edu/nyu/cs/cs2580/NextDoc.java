package edu.nyu.cs.cs2580;

import java.util.Vector;

/**
 * Created by naman on 12/9/2016.
 */
public class NextDoc {
    boolean stopRepeat;
    Vector<Document> documnets;

    public NextDoc(boolean stopRepeat, Vector<Document> documents){
        this.stopRepeat = stopRepeat;
        this.documnets = documents;
    }
}
