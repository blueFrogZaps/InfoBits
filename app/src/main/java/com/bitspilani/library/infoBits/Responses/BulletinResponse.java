package com.bitspilani.library.infoBits.Responses;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class BulletinResponse {

   // public Subject CHEMICAL, CIVIL, EEE, CS, MECH, PHARMA, BIO, CHEM, ECO, MATH, PHY, HUM, MAN;
    public ArrayList<Subject> subjects = new ArrayList<>();
   // public String[] tabTitles = {"CHEMICAL", "CIVIL", "EEE", "CS", "MECH", "PHARMA", "BIO", "CHEM", "ECO", "MATHS", "PHY", "HUM", "MAN"};
    private String json;
    public ArrayList<String> subjectAvailable=new ArrayList<>();

    public BulletinResponse(String json){
        this.json = json;
        System.out.println("MYJSON:  "+json.toString());
        parseJSON();
    }

    public void parseJSON(){
        JSONObject jsonObject;


        try {
            jsonObject = new JSONObject(json);
            Iterator<String> iter = jsonObject.keys();

            while(iter.hasNext()) {
                String sub = iter.next();
                subjectAvailable.add(sub);
                subjects.add(new Subject(jsonObject.getString(sub)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}

