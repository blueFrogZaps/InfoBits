package com.bitspilani.library.infobits.Responses;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by SowmyaY on 29/08/16.
 */
public class NewsResponse {

    public ArrayList<String> news = new ArrayList<String>();
    public ArrayList<String> urls = new ArrayList<String>();
    public ArrayList<String> newsPaperAndDate = new ArrayList<String>();
    private String json;

    public NewsResponse(String json){
        this.json = json;
    }

    public void parseJSON(){
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            Iterator keysToCopyIterator = jsonObject.keys();
            List<String> keysList = new ArrayList<String>();
            while(keysToCopyIterator.hasNext()) {
                String key = (String) keysToCopyIterator.next();
                keysList.add(key);
            }
            String[] keys = keysList.toArray(new String[keysList.size()]);

            for (int i = 0; i < keys.length ; i++) {
                System.out.println(keys[i]);
                JSONObject json = jsonObject.getJSONObject(keys[i]);
                String new1 = json.getString("title");
                news.add(new1);
                urls.add(json.getString("url"));
                newsPaperAndDate.add(json.getString("newspaper") + " : " + json.getString("date"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
