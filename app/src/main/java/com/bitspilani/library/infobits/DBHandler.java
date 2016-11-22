package com.bitspilani.library.infobits;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;
import org.json.JSONException;
import org.json.JSONObject;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "bitslib.db";
    private static final String[] TABLES = {"communications","newsfeed","noticeboard","book_of_the_month","lfms", "infobits"};
    private static final String[][] COLUMNS = {{"bitsid","category","name","topic","date","time","admins","talk","cat","status"},
                                                {"news_type","title","url","date","added_by","newspaper","keywords","pages"},
                                                {"image","link"},
                                                {"title","author","image"},
                                                {"particulars","brand","date","time","status"},
                                                {"pic1", "pic2", "pic3", "pic4",
                                                        "type1", "type2", "type3", "type4",
                                                        "url1", "url2", "url3", "url4",
                                                        "jpic1", "jpic2", "jpic3", "jpic4",
                                                        "jtype1", "jtype2", "jtype3", "jtype4",
                                                        "jurl1", "jurl2", "jurl3", "jurl4"}};
    private static final String[][] TYPES = {{"varchar(12)","varchar(20)","varchar(40)","text","varchar(10)","varchar(10)","text","text","varchar(10)","varchar(10)"},
                                             {"varchar(5)","text","text","date","varchar(10)","text","text","varchar(20)"},
                                             {"text","text"},
                                             {"text","text","text"},
                                             {"varchar(50)","varchar(20)","varchar(10)","varchar(10)","varchar(1)"},
                                             {"text", "text", "text", "text",
                                                     "text", "text", "text", "text",
                                                     "text", "text", "text", "text",
                                                     "text", "text", "text", "text",
                                                     "text", "text", "text", "text",
                                                     "text", "text", "text", "text"}};
    private static final int[][] VAR_COLUMNS = {{6,7,9},{},{},{},{4},{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23}};

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "";
        for (int i = 0; i < TABLES.length; i++){
            for(int j = 0; j < COLUMNS[i].length; j++){
                query += COLUMNS[i][j] + " " + TYPES[i][j];
                if(j != COLUMNS[i].length - 1){
                    query += ", ";
                }
            }
            query = "CREATE TABLE IF NOT EXISTS "+ TABLES[i] + "(id integer primary_key, " +
                    query + ")";
            db.execSQL(query);
            query = "";
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = 0; i < TABLES.length; i++) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLES[i]);
        }
        onCreate(db);
    }

    public void addData(int table,String[] data){
        ContentValues values = new ContentValues();
        values.put("id",data[0]);
        for(int i = 1; i <= COLUMNS[table].length; i++){
            values.put(COLUMNS[table][i-1], data[i]);
        }
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLES[table], null, values);
        db.close();
    }

    public void updateData(int table,String[] data,int id){
        ContentValues values = new ContentValues();
        for(int i = 0; i < VAR_COLUMNS[table].length; i++){
            values.put(COLUMNS[table][VAR_COLUMNS[table][i]], data[i]);
        }
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLES[table], values, "id = " + id, null);
        db.close();
    }

    public void deleteData(int table, int id){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLES[table] + " WHERE " + id + " = '" + id + "';");
        db.close();
    }

    public int getNum(int table, String sql){
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT id from " + TABLES[table] + " where " + sql, null);
        int count = c.getCount();
        db.close();
        return count;
    }

    public JSONObject selectData(int table, String sql){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLES[table] + " WHERE " + sql;
        Cursor c = db.rawQuery(query, null);
        JSONObject json = manageData(c, table);
        db.close();
        return json;
    }

    public JSONObject manageData(Cursor c, int table){
        c.moveToFirst();
        JSONObject json = new JSONObject();
        JSONObject data = new JSONObject();
        while(!c.isAfterLast()){
            for(int i = 0; i < COLUMNS[table].length; i++){
                try {
                    data.put(COLUMNS[table][i], c.getString(c.getColumnIndex(COLUMNS[table][i])));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            try {
                json.put(c.getString(c.getColumnIndex("id")), data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            c.moveToNext();
            data = new JSONObject();
        }
        return json;
    }

    public boolean checkTable(int table) {

        SQLiteDatabase db = getWritableDatabase();
        Cursor cur2 = db.rawQuery("select name from sqlite_master where name='"
                + TABLES[table] + "'", null);

        if (cur2.getCount() != 0) {
            if (!cur2.isClosed())
                cur2.close();
            return true;
        } else {
            if (!cur2.isClosed())
                cur2.close();
            return false;
        }
    }

    public JSONObject getData(int table){
        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT * FROM " + TABLES[table];
        //String query = "SELECT * FROM " + TABLES[table] + " WHERE " + sql;
        Cursor c = db.rawQuery(query, null);
        JSONObject json = manageData(c, table);
        db.close();
        return json;
    }

}
