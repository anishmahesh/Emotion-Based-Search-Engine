package DataPreProcessing.Mining;

import com.ibm.watson.developer_cloud.alchemy.v1.model.DocumentEmotion;
import com.ibm.watson.developer_cloud.alchemy.v1.model.DocumentSentiment;

import javax.swing.text.Document;

/**
 * Created by naman on 12/2/2016.
 */
public class MinedDocument {
    DocumentSentiment sentiment;
    DocumentEmotion emotion;

}
