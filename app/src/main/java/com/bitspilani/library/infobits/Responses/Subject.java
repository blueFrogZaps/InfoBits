package com.bitspilani.library.infobits.Responses;

import org.json.JSONException;
import org.json.JSONObject;

public class Subject{
    public Book book1;
    public Book book2;
    public Book book3;
    public Book book4;
    public Book journal1;
    public Book journal2;
    public Book journal3;
    public Book journal4;

    private String json;

    void setJson(String json)
    {
        this.json = json;
    }

    public Subject(String json){
        this.json = json;
        parseJSON(json);
    }

    public void parseJSON(String json){
        this.json=json;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            JSONObject jsonBook = jsonObject.getJSONObject("books");
            JSONObject jsonJournal = jsonObject.getJSONObject("journals");
            for (int i = 0; i <jsonBook.length() + jsonJournal.length() ; i++) {
                if (i<4){
                    int count = i+1;
                    Book book = new Book(jsonBook.getString("book" + count));
                    switch (i){
                        case 0:
                            book1 = book;
                            break;
                        case 1:
                            book2 = book;
                            break;
                        case 2:
                            book3 = book;
                            break;
                        case 3:
                            book4 = book;
                            break;
                    }
                }else{
                    int j = i -3;
                    Book book = new Book(jsonJournal.getString("journal" + j));
                    book.parseJSON();
                    switch (j - 1){
                        case 0:
                            journal1 = book;
                            break;
                        case 1:
                            journal2 = book;
                            break;
                        case 2:
                            journal3 = book;
                            break;
                        case 3:
                            journal4 = book;
                            break;
                    }
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
