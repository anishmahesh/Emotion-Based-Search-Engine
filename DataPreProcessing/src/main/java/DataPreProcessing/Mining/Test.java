package DataPreProcessing.Mining;

import com.ibm.watson.developer_cloud.alchemy.v1.AlchemyVision;
import com.ibm.watson.developer_cloud.alchemy.v1.model.ImageKeywords;

import java.io.File;

/**
 * Created by sanchitmehta on 12/5/2016.
 */
public class Test {

    public void testAPI(String file, String key) {
        AlchemyVision service = new AlchemyVision();
        service.setApiKey(key);

        File image = new File(file);
        Boolean forceShowAll = false;
        Boolean knowledgeGraph = false;
        ImageKeywords keywords =  service.getImageKeywords(image, forceShowAll, knowledgeGraph).execute();

        System.out.println(keywords);
    }
}
