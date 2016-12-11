package edu.nyu.cs.cs2580;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sanchitmehta on 10/12/16.
 */

public class AutoCompleteResponse {
    public  String getDataFor(String s, SearchEngine.Options _options){
        JSONObject obj = new JSONObject();
        JSONArray list = new JSONArray();
        if(s.length()<1){
            return "";
        }else{
            char c = s.toLowerCase().charAt(0);
            String suggestions[] = new String[40];

            String fileName = new StringBuilder().append(_options._logPrefix+"/").append(c).append(".tsv").toString();
            Map<String, Integer> results = null;
            try {
                results = searchSuggestions(s, fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(results.size() != 0){
                for(String result : results.keySet()){
                    result = result.replace("+"," ");
                    result = result.substring(0, 1).toUpperCase() + result.substring(1);
                    list.add(result);
                }
                obj.put("autofill", list);
                return obj.toString();
            }
        }
//        list.add("Sanchit");
//        list.add("Su Chhe");
//        list.add("Somber");
//
//        obj.put("autofill", list);
        return obj.toJSONString();
    }

    public Map<String, Integer> searchSuggestions(String prefix, String fileName ) {

        Map<String, Integer> results = new HashMap<String, Integer>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line = br.readLine();
            //boolean start = true;
            while ((line = br.readLine()) != null) {
                String[] splitFileLine = line.split("\t");
                if ((splitFileLine[0].compareToIgnoreCase(prefix) >= 0) && splitFileLine[0].contains(prefix)) {
                    results.put(splitFileLine[0], Integer.parseInt(splitFileLine[1]));
                }
            }
        }catch(Exception e){
            //pass
        }
        return results;
    }
}
