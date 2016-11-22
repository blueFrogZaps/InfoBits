package com.bitspilani.library.infobits.Responses;

import org.json.JSONException;
import org.json.JSONObject;

public class Book{
    public String pic, url, type;

    private String json;

    public Book(String json){
        this.json = json;
        parseJSON();
    }

    public void parseJSON(){
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            pic = jsonObject.getString("pic");
            url = jsonObject.getString("url");
            type = jsonObject.getString("type");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
