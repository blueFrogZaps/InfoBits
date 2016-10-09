package com.example.suryansh.infobits;

import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CommForms extends ConnectWithLibrary {

    int[] Forms = {R.layout.breco, R.layout.dlost, R.layout.database, R.layout.service, R.layout.breview, R.layout.feedback};
    int[] brecos = {R.id.recommendation1, R.id.recommendation2, R.id.recommendation3, R.id.recommendation4, R.id.recommendation5};
    int[] headers = {R.id.header1, R.id.header2, R.id.header3, R.id.header4, R.id.header5};
    int cat;
    int reconum = 0;
    ProgressBar progress;
    String urlString  = apiURL + "newConv.php?username=" + username + "&password=" + password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        cat = b.getInt("cat");
        setContentView(Forms[cat - 1]);
        setTitle(catnames[cat - 1]);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (cat == 2) {
            RadioButton books = (RadioButton) findViewById(R.id.radioB);
            books.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        findViewById(R.id.journalLayout).setVisibility(View.GONE);
                        findViewById(R.id.bookLayout).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.bookLayout).setVisibility(View.GONE);
                        findViewById(R.id.journalLayout).setVisibility(View.VISIBLE);
                    }
                }
            });
            DatePicker datePicker = (DatePicker) findViewById(R.id.monthYear);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
                if (daySpinnerId != 0) {
                    View daySpinner = datePicker.findViewById(daySpinnerId);
                    if (daySpinner != null) {
                        daySpinner.setVisibility(View.GONE);
                    }
                }
            } else {
                try {
                    Field f[] = datePicker.getClass().getDeclaredFields();
                    for (Field field : f) {
                        if (field.getName().equals("mDaySpinner") || field.getName().equals("mDayPicker")) {
                            field.setAccessible(true);
                            Object dayPicker = field.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date today = new Date();
            Date last = new Date(0);
            try {
                last = df.parse("1900-01-01");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            datePicker.setMaxDate(today.getTime() + 19800000);
            datePicker.setMinDate(last.getTime());
        }
        if (cat == 1) {
            FloatingActionButton add = (FloatingActionButton) findViewById(R.id.add);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (reconum < 4) {
                        reconum++;
                        adjustreco();
                    } else {
                        Toast.makeText(CommForms.this, "Maximum 5 recommendations can be given.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            FloatingActionButton delete = (FloatingActionButton) findViewById(R.id.delete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (reconum > 0) {
                        reconum--;
                        adjustreco();
                        if (reconum == 0) {
                            findViewById(brecos[0]).setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(CommForms.this, "Minimum 1 recommendation is required.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void adjustreco(){
        TextView tv;
        ScrollView tb;
        for(int i = 0; i < headers.length; i++){
            tv = (TextView) findViewById(headers[i]);
            tb = (ScrollView) findViewById(brecos[i]);
            if(reconum >= i){
                tv.setVisibility(View.VISIBLE);
            }
            else{
                tv.setVisibility(View.GONE);
                tb.setVisibility(View.GONE);
            }
        }
    }

    public void showbrecoform(View view){
        TextView tv = (TextView) view;
        String s = tv.getText().toString();
        int i = Integer.parseInt(s.substring(s.length() - 1, s.length()));
        ScrollView tb;
        for(int j = 0; j < brecos.length; j++){
            tb = (ScrollView) findViewById(brecos[j]);
            if(j == i-1){
                if(tb.getVisibility() == View.GONE){
                    tb.setVisibility(View.VISIBLE);
                }
                else if(reconum > 0){
                    tb.setVisibility(View.GONE);
                }
            }
            else{
                tb.setVisibility(View.GONE);
            }
        }
    }

    public void newConv(View view){
        if(cat == 1) {
            findViewById(R.id.add).setClickable(false);
            findViewById(R.id.delete).setClickable(false);
        }
        ArrayList<String> inputArray = new ArrayList<>();
        String url = "";
        switch(cat) {
            case 1:
                String title, author, edition, pub, year;
                int[] titles = {R.id.title1, R.id.title2, R.id.title3, R.id.title4, R.id.title5};
                int[] authors = {R.id.author1, R.id.author2, R.id.author3, R.id.author4, R.id.author5};
                int[] editions = {R.id.edition1, R.id.edition2, R.id.edition3, R.id.edition4, R.id.edition5};
                int[] publishers = {R.id.publisher1, R.id.publisher2, R.id.publisher3, R.id.publisher4, R.id.publisher5};
                int[] years = {R.id.year1, R.id.year2, R.id.year3, R.id.year4, R.id.year5};
                url = urlString + "&cat=" + cat;
                for(int i = 0; i <= reconum; i++){
                    title = ((EditText) findViewById(titles[i])).getText().toString();
                    author = ((EditText) findViewById(authors[i])).getText().toString();
                    edition = ((EditText) findViewById(editions[i])).getText().toString();
                    pub = ((EditText) findViewById(publishers[i])).getText().toString();
                    year = ((EditText) findViewById(years[i])).getText().toString();
                    if(title.isEmpty()){
                        ((EditText) findViewById(titles[i])).setError("Title can't be empty");
                        url = "";
                        i = reconum;
                    }
                    else if(author.isEmpty()){
                        ((EditText) findViewById(authors[i])).setError("Author can't be empty");
                        url = "";
                        i = reconum;
                    }
                    else if(edition.isEmpty()){
                        ((EditText) findViewById(editions[i])).setError("Edition can't be empty");
                        url = "";
                        i = reconum;
                    }
                    else if(pub.isEmpty()){
                        ((EditText) findViewById(publishers[i])).setError("Publisher can't be empty");
                        url = "";
                        i = reconum;
                    }
                    else if(year.isEmpty()){
                        ((EditText) findViewById(years[i])).setError("Year can't be empty");
                        url = "";
                        i = reconum;
                    }
                    else{
                        try {
                            inputArray.add(5 * i, URLEncoder.encode(title, "UTF-8"));
                            inputArray.add(5 * i + 1, URLEncoder.encode(author, "UTF-8"));
                            inputArray.add(5 * i + 2, URLEncoder.encode(edition, "UTF-8"));
                            inputArray.add(5 * i + 3, URLEncoder.encode(pub, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            //Toast.makeText(CommForms.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        inputArray.add(5 * i + 4, year);
                        url = url + "&inputArray[" + 5*i + "]=" + inputArray.get(5*i) + "&inputArray[" + (5*i + 1) + "]=" + inputArray.get(5*i + 1) + "&inputArray[" + (5*i + 2) + "]=" + inputArray.get(5*i + 2) + "&inputArray[" + (5*i + 3) + "]=" + inputArray.get(5*i + 3) + "&inputArray[" + (5*i + 4) +"]=" + inputArray.get(5*i + 4);
                    }
                }
                break;
            case 2:
                String doc = ((RadioButton) findViewById(((RadioGroup) findViewById(R.id.documentLost)).getCheckedRadioButtonId())).getText().toString();
                inputArray.add(0,doc);
                if(doc.equals("Books")) {
                    title = ((EditText) findViewById(R.id.title)).getText().toString();
                    author = ((EditText) findViewById(R.id.author)).getText().toString();
                    String accno = ((EditText) findViewById(R.id.accno)).getText().toString();
                    if(title.isEmpty()){
                        ((EditText) findViewById(R.id.title)).setError("Title can't be empty");
                    }
                    else if(author.isEmpty()){
                        ((EditText) findViewById(R.id.author)).setError("Author can't be empty");
                    }
                    else if(accno.isEmpty()){
                        ((EditText) findViewById(R.id.accno)).setError("Accession No. can't be empty");
                    }
                    else {
                        try {
                            inputArray.add(1, URLEncoder.encode(title, "UTF-8"));
                            inputArray.add(2, URLEncoder.encode(author, "UTF-8"));
                            inputArray.add(3, URLEncoder.encode(accno, "UTF-8"));
                            url = urlString + "&cat=" + cat + "&inputArray[0]=" + inputArray.get(0) + "&inputArray[1]=" + inputArray.get(1) + "&inputArray[2]=" + inputArray.get(2) + "&inputArray[3]=" + inputArray.get(3);
                        } catch (UnsupportedEncodingException e) {
                            //Toast.makeText(CommForms.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    String month = String.valueOf(((DatePicker) findViewById(R.id.monthYear)).getMonth());
                    String jour = ((EditText) findViewById(R.id.journal)).getText().toString();
                    year = String.valueOf(((DatePicker) findViewById(R.id.monthYear)).getYear());
                    if(jour.isEmpty()){
                        ((EditText) findViewById(R.id.journal)).setError("Journal can't be empty");
                    }
                    else {
                        try {
                            inputArray.add(1, URLEncoder.encode(jour, "UTF-8"));
                            inputArray.add(2, URLEncoder.encode(new DateFormatSymbols().getMonths()[Integer.parseInt(month)], "UTF-8"));
                            inputArray.add(3, URLEncoder.encode(year, "UTF-8"));
                            url = urlString + "&cat=" + cat + "&inputArray[0]=" + inputArray.get(0) + "&inputArray[1]=" + inputArray.get(1) + "&inputArray[2]=" + inputArray.get(2) + "&inputArray[3]=" + inputArray.get(3);
                        } catch (UnsupportedEncodingException e) {
                            //Toast.makeText(CommForms.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case 3:
                String dat = ((EditText) findViewById(R.id.database)).getText().toString();
                String jour = ((EditText) findViewById(R.id.journal)).getText().toString();
                String loc = ((EditText) findViewById(R.id.location)).getText().toString();
                if(dat.isEmpty()){
                    ((EditText) findViewById(R.id.database)).setError("Database can't be empty");
                }
                else if(jour.isEmpty()){
                    ((EditText) findViewById(R.id.journal)).setError("Journal can't be empty");
                }
                else if(loc.isEmpty()){
                    ((EditText) findViewById(R.id.location)).setError("Location can't be empty");
                }
                else {
                    try {
                        inputArray.add(0, URLEncoder.encode(dat, "UTF-8"));
                        inputArray.add(1, URLEncoder.encode(jour, "UTF-8"));
                        inputArray.add(2, URLEncoder.encode(loc, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        //Toast.makeText(CommForms.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    url = urlString + "&cat=" + cat + "&inputArray[0]=" + inputArray.get(0) + "&inputArray[1]=" + inputArray.get(1) + "&inputArray[2]=" + inputArray.get(2);
                }
                break;
            case 4:
                String serv = ((RadioButton) findViewById(((RadioGroup) findViewById(R.id.services)).getCheckedRadioButtonId())).getText().toString();
                if(!serv.isEmpty()) {
                    String grieve = ((EditText) findViewById(R.id.grievance)).getText().toString();
                    if(!grieve.isEmpty()) {
                        try {
                            inputArray.add(0, URLEncoder.encode(serv, "UTF-8"));
                            inputArray.add(1, URLEncoder.encode(grieve, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            //Toast.makeText(CommForms.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        url = urlString + "&cat=" + cat + "&inputArray[0]=" + inputArray.get(0) + "&inputArray[1]=" + inputArray.get(1);
                    }
                    else{
                        ((EditText) findViewById(R.id.grievance)).setError("Grievance can't be empty");
                    }
                }
                else{
                    Toast.makeText(CommForms.this,"Please select a feedback type",Toast.LENGTH_LONG).show();
                }
                break;
            case 5:
                title = ((EditText) findViewById(R.id.title)).getText().toString();
                author = ((EditText) findViewById(R.id.author)).getText().toString();
                String review = ((EditText) findViewById(R.id.review)).getText().toString();
                if(title.isEmpty()){
                    ((EditText) findViewById(R.id.title)).setError("Title can't be empty");
                }
                else if(author.isEmpty()){
                    ((EditText) findViewById(R.id.author)).setError("Author can't be empty");
                }
                else if(review.isEmpty()){
                    ((EditText) findViewById(R.id.review)).setError("Review can't be empty");
                }
                else{
                    try {
                        inputArray.add(0,URLEncoder.encode(title, "UTF-8"));
                        inputArray.add(1,URLEncoder.encode(author, "UTF-8"));
                        inputArray.add(2,URLEncoder.encode(review, "UTF-8"));
                    }catch(UnsupportedEncodingException e){
                        //Toast.makeText(CommForms.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    url = urlString + "&cat=" + cat + "&inputArray[0]=" + inputArray.get(0) + "&inputArray[1]=" + inputArray.get(1) + "&inputArray[2]=" + inputArray.get(2);
                }
                break;
            case 6:
                String feed = ((RadioButton) findViewById(((RadioGroup) findViewById(R.id.feedbackType)).getCheckedRadioButtonId())).getText().toString();
                if(!feed.isEmpty()) {
                    String back = ((EditText) findViewById(R.id.feedback)).getText().toString();
                    if(!back.isEmpty()) {
                        inputArray.add(0, feed);
                        try {
                            inputArray.add(1, URLEncoder.encode(back, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            //Toast.makeText(CommForms.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        url = urlString + "&cat=" + cat + "&inputArray[0]=" + inputArray.get(0) + "&inputArray[1]=" + inputArray.get(1);
                    }
                    else{
                        ((EditText) findViewById(R.id.feedback)).setError("Feedback can't be empty");
                    }
                }
                else{
                    Toast.makeText(CommForms.this,"Please select a feedback type",Toast.LENGTH_LONG).show();
                }
                break;
        }
        if(isConnected()){
            if(cat == 1){
                findViewById(R.id.commMenu).setVisibility(View.GONE);
            }
            findViewById(R.id.CommForm).setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            if(!url.isEmpty()) {
                new APICall().execute(url);
            }
        }else{
            Toast.makeText(CommForms.this, "Not Connected to BITS Intranet!", Toast.LENGTH_LONG).show();
        }
        inputArray.clear();
        if(cat == 1) {
            findViewById(R.id.add).setClickable(true);
            findViewById(R.id.delete).setClickable(true);
        }
    }

    private class APICall extends AsyncTask<String,Integer,String> {

        String err;
        @Override
        protected String doInBackground(String[] params) {
            String urlString= params[0];
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);
            } catch (Exception e ) {
                err = "Network Error! Ensure you're connected to BITS Intranet";
            }
            return responseStrBuilder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            if(!result.isEmpty()) {
                JSONObject json = null;
                try {
                    json = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                updateInternalData(json, "new");
//                Intent resultIntent = new Intent(CommForms.this,ConnectWithLibrary.class);
//                resultIntent.putExtra("update", "yes");
//                setResult(Activity.RESULT_OK, resultIntent);
                finishActivity(Activity.RESULT_OK);
            }else{
                if(cat == 1){
                    findViewById(R.id.commMenu).setVisibility(View.VISIBLE);
                }
                findViewById(R.id.CommForm).setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                if(!err.isEmpty()){
                    Toast.makeText(CommForms.this,err,Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
