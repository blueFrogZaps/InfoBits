package com.bitspilani.library.infobits.Responses;

import org.json.JSONException;
import org.json.JSONObject;
public class BulletinResponse {

    public Subject CHEMICAL, CIVIL, EEE, CS, MECH, PHARMA, BIO, CHEM, ECO, MATH, PHY, HUM, MAN;
    public String[] tabTitles = {"CHEMICAL", "CIVIL", "EEE", "CS", "MECH", "PHARMA", "BIO", "CHEM", "ECO", "MATHS", "PHY", "HUM", "MAN"};
    private String json;

    public BulletinResponse(String json){
        this.json = json;
        parseJSON();
    }

    public void parseJSON(){
        JSONObject jsonObject;


        try {
            jsonObject = new JSONObject(json);
            System.out.println("arr: " + jsonObject.keys());
//            Iterator<String> iter = jsonObject.keys();

            for (int i = 0; i < tabTitles.length; i++) {
                switch (tabTitles[i]){
                    case "CHEMICAL":
                        CHEMICAL = new Subject(jsonObject.getString(tabTitles[i]));
                        break;
                    case "CIVIL":
                        CIVIL = new Subject(jsonObject.getString(tabTitles[i]));
                        break;
                    case "EEE":
                        EEE = new Subject(jsonObject.getString(tabTitles[i]));
                        break;
                    case "CS":
                        CS = new Subject(jsonObject.getString(tabTitles[i]));
                        break;
                    case "MECH":
                        MECH = new Subject(jsonObject.getString(tabTitles[i]));
                        break;
                    case "PHARMA":
                        PHARMA = new Subject(jsonObject.getString(tabTitles[i]));
                        break;
                    case "BIO":
                        BIO = new Subject(jsonObject.getString(tabTitles[i]));
                        break;
                    case "CHEM":
                        CHEM = new Subject(jsonObject.getString(tabTitles[i]));
                        break;
                    case "ECO":
                        ECO = new Subject(jsonObject.getString(tabTitles[i]));
                        break;
                    case "MATHS":
                        MATH = new Subject(jsonObject.getString(tabTitles[i]));
                        break;
                    case "PHY":
                        PHY = new Subject(jsonObject.getString(tabTitles[i]));
                        break;
                    case "HUM":
                        HUM = new Subject(jsonObject.getString(tabTitles[i]));
                        break;
                    case "MAN":
                        MAN = new Subject(jsonObject.getString(tabTitles[i]));
                        break;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}

