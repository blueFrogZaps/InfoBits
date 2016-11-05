package com.example.suryansh.infobits;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.suryansh.infobits.Responses.BulletinResponse;
import com.example.suryansh.infobits.Responses.NewsResponse;
import com.example.suryansh.infobits.Responses.Subject;
import com.example.suryansh.infobits.network.VolleySingleton;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class infoBitsBulletin extends homepage {

    private DBHandler dbhandler;
    private TabLayout mTabLayout;
    private ViewPager mPager;
    private MyPagerAdapter mAdapter;
    private ListView listView;
    private TextView mIssue, mMessage;
    Subject current_sub;
    SharedPreferences sp;
    ProgressBar spinner;
    JSONObject internal;
    List<String> updatevalues;
    public String[] tabTitles = {"CHEMICAL", "CIVIL", "EEE", "CS", "MECH", "PHARMA", "BIO", "CHEM", "ECO", "MATHS", "PHY", "HUM", "MAN"};
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infobitsbulletin);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        mIssue = (TextView) findViewById(R.id.issue);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mPager = (ViewPager) findViewById(R.id.pager);
        mMessage = (TextView) findViewById(R.id.message);
        listView = (ListView) findViewById(R.id.listView);
        sp = getSharedPreferences("bulletinMonth", Activity.MODE_PRIVATE);
        dir = getFilesDir();
        JSONObject internal;
        listView.setVisibility(View.INVISIBLE);
        dbhandler = new DBHandler(this, null, null);
        internal = dbhandler.selectData(5, "1 ORDER BY id ASC");
        try {
            if (internal.length() != 0) {
                int bulletinYear = sp.getInt("bulletinYear", 0);
                int bulletinMonth = sp.getInt("bulletinMonth", 0);
                Calendar cal = Calendar.getInstance();
                Date date = new Date();
                cal.setTime(date);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);
                if (bulletinYear < year) {
                    serverCalls();
                } else {
                    if (bulletinMonth <= month) {
                        serverCalls();
                    } else {
                        setFragments();
                    }
                }
            } else {
                if (isConnected()) {
                    serverCalls();
                } else {
                    alertShow("Connect to intranet and try again", "Not Connected to Intranet");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            mPager.setVisibility(View.INVISIBLE);
            mTabLayout.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.INVISIBLE);
            mMessage.setVisibility(View.VISIBLE);
            mMessage.setText(R.string.no_news_today);
            alertShow("Connect to intranet and try again", "Not Connected to Intranet");
        }
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        File profilepic = new File(dir, avatar);
        try {
            fileInput = new FileInputStream(profilepic);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(fileInput != null){
            setToolBarAvatar(profilepic);
        }
    }

    public void alertShow(String message, String title) {
        //  AlertDialog.Builder alertDialog = new AlertDialog.Builder()
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                this);

        // Setting Dialog Title
        alertDialog2.setTitle(title);

        // Setting Dialog Message
        alertDialog2.setMessage(message);

        // Setting Icon to Dialog
        alertDialog2.setIcon(R.drawable.delete);

        // Setting Negative "Yes" Btn
        alertDialog2.setNegativeButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        finish();
                        Toast.makeText(getApplicationContext(),
                                "Closed Bulletin", Toast.LENGTH_SHORT)
                                .show();
                    }
                });

        // Showing Alert Dialog
        alertDialog2.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<Fragment>();

        try {
            internal = dbhandler.selectData(5, "1 ORDER BY id ASC");
            Iterator intiter = internal.keys();
            String key;
            while (intiter.hasNext()) {
                MyFragment fragment = new MyFragment();
                Integer[] count = {0, 0, 0, 0, 0, 0};
                String[] bookNameList = {"", "", "", ""};
                String[] bookImageList = {"", "", "", ""};
                String[] journalNameList = {"", "", "", ""};
                String[] journalImageList = {"", "", "", ""};
                String[] journalLinks = {"", "", "", ""};
                String[] bookLinks = {"", "", "", ""};
                JSONObject data = (JSONObject) internal.get(intiter.next().toString());
                Iterator iter = data.keys();
                while (iter.hasNext()) {
                    key = iter.next().toString();
                    switch (key.substring(0, key.length() - 1)) {
                        case "pic":
                            bookImageList[count[0]] = data.get(key).toString();
                            count[0]++;
                            break;
                        case "url":
                            bookLinks[count[1]] = data.get(key).toString();
                            count[1]++;
                            break;
                        case "type":
                            bookNameList[count[2]] = data.get(key).toString();
                            count[2]++;
                            break;
                        case "jpic":
                            journalImageList[count[3]] = data.get(key).toString();
                            count[3]++;
                            break;
                        case "jurl":
                            journalLinks[count[4]] = data.get(key).toString();
                            count[4]++;
                            break;
                        case "jtype":
                            journalNameList[count[5]] = data.get(key).toString();
                            count[5]++;
                            break;
                    }
                }
                fragment.setList(journalNameList, bookNameList, bookImageList, journalImageList, bookLinks, journalLinks);
                fList.add(fragment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fList;
    }

    public void setFragments() {
        if(isConnected()){
            dailyNewsServerCall();
        }
        else{
            listView.setVisibility(View.GONE);
            mMessage.setVisibility(View.VISIBLE);
            mMessage.setText(R.string.no_news_today);
        }
        int bulletinIssue = sp.getInt("bulletinIssue", 0);
        int bulletinVolume = sp.getInt("bulletinVolume", 0);
        mIssue.setText("Issue: " + bulletinIssue + " | Volume: " + bulletinVolume);
        mAdapter = new MyPagerAdapter(getSupportFragmentManager(), getFragments());
        mPager.setAdapter(mAdapter);
        mTabLayout.setTabsFromPagerAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mPager);
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "infoBitsBulletin Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.suryansh.infobits/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "infoBitsBulletin Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.suryansh.infobits/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> fragments;

        public MyPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
            //return 13;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return tabTitles[position];
        }
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {

        Bitmap bitmap;
        String filename, link, key, type;
        Integer tot;
        protected Bitmap doInBackground(String[] args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
                filename = args[0].substring(args[0].lastIndexOf("/") + 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {
            if(image != null){
                String ext = filename.substring(filename.lastIndexOf("."));
                File file = new File(dir, filename);
                FileOutputStream fileOut;
                try {
                    fileOut = new FileOutputStream(file);
                    if(ext.equals(".jpg") || ext.equals(".jpeg")){
                        image.compress(Bitmap.CompressFormat.JPEG, 50, fileOut);
                    }
                    else{
                        image.compress(Bitmap.CompressFormat.PNG, 50, fileOut);
                    }
                    fileOut.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void serverCalls() {

        //  spinner.setVisibility(View.VISIBLE);
        // Instantiate the RequestQueue.
        final RequestQueue queue = VolleySingleton.getInstance().getRequestQueue();

        String url = apiURL + "bulletin.php?" + "username=" + username + "&password=" + password;
        // Request a string response from the provided URL.

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                SharedPreferences sp = getSharedPreferences("bulletinMonth", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                Date date = new Date();

                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);
                editor.putInt("bulletinMonth", month);
                editor.putInt("bulletinYear", year);

                try {

                    if (response.has("err_message") && !response.get("err_message").toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), response.get("err_message").toString(), Toast.LENGTH_LONG).show();
                    } else {
                        JSONObject data = response.getJSONObject("data");
                        Iterator idata = data.keys();
                        while (idata.hasNext()){
                            String sub = idata.next().toString();
                            if(data.has(sub)) {
                                JSONObject subject = (JSONObject) data.get(sub);
                                JSONObject books = (JSONObject) subject.get("books");
                                JSONObject journals = (JSONObject) subject.get("journals");
                                Iterator b = books.keys();
                                Iterator j = journals.keys();
                                while (j.hasNext()) {
                                    JSONObject book = (JSONObject) books.get(b.next().toString());
                                    JSONObject journal = (JSONObject) journals.get(j.next().toString());
                                    String imageurl = imageApiURL + "bulletin/" + sub + "/";
                                    new LoadImage().execute(imageurl + book.get("pic").toString());
                                    new LoadImage().execute(imageurl + journal.get("pic").toString());
                                }
                            }
                        }
                        int issue = Integer.parseInt(response.get("issue").toString());
                        int volume = Integer.parseInt(response.get("volume").toString());
                        editor.putInt("bulletinIssue", issue);
                        editor.putInt("bulletinVolume", volume);
                        BulletinResponse bulletinResponse = new BulletinResponse(data.toString());
                        saveToDB(bulletinResponse);
                    }
                    // adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    //alertShow("Connect to intranet and try again", "Not Connected to Intranet");
                }
                editor.commit();
                setFragments();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //  spinner.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                setFragments();
                // System.out.println(map);

            }
        });
        // Add the request to the RequestQueue.
        queue.add(jsonObjReq);
    }

    public void saveToDB(BulletinResponse bulletinResponse) {
        for (int i = 0; i < tabTitles.length; i++) {
            switch (i) {
                case 0:
                    current_sub = bulletinResponse.CHEMICAL;
                    break;
                case 1:
                    current_sub = bulletinResponse.CIVIL;
                    break;
                case 2:
                    current_sub = bulletinResponse.EEE;
                    break;
                case 3:
                    current_sub = bulletinResponse.EEE;
                    break;
                case 4:
                    current_sub = bulletinResponse.MECH;
                    break;
                case 5:
                    current_sub = bulletinResponse.PHARMA;
                    break;
                case 6:
                    current_sub = bulletinResponse.BIO;
                    break;
                case 7:
                    current_sub = bulletinResponse.CHEM;
                    break;
                case 8:
                    current_sub = bulletinResponse.ECO;
                    break;
                case 9:
                    current_sub = bulletinResponse.MATH;
                    break;
                case 10:
                    current_sub = bulletinResponse.PHY;
                    break;
                case 11:
                    current_sub = bulletinResponse.HUM;
                    break;
                case 12:
                    current_sub = bulletinResponse.MAN;
                    break;
            }
            updatevalues = new ArrayList<String>(Arrays.asList(new String[]{current_sub.book1.pic, current_sub.book2.pic, current_sub.book3.pic, current_sub.book4.pic,
                    current_sub.book1.type, current_sub.book2.type, current_sub.book3.type, current_sub.book4.type,
                    current_sub.book1.url, current_sub.book2.url, current_sub.book3.url, current_sub.book4.url,
                    current_sub.journal1.pic, current_sub.journal2.pic, current_sub.journal3.pic, current_sub.journal4.pic,
                    current_sub.journal1.type, current_sub.journal2.type, current_sub.journal3.type, current_sub.journal4.type,
                    current_sub.journal1.url, current_sub.journal2.url, current_sub.journal3.url, current_sub.journal4.url}));
            JSONObject check = dbhandler.selectData(1, "id = " + i);
            if (check.length() != 0) {
                dbhandler.updateData(5, updatevalues.toArray(new String[0]), i);
            } else {
                updatevalues.add(0, String.valueOf(i));
                dbhandler.addData(5, updatevalues.toArray(new String[0]));
            }
        }
    }

    public void dailyNewsServerCall() {

        final RequestQueue queue = VolleySingleton.getInstance().getRequestQueue();

        String url = apiURL + "daily_news.php?" + "username=" + username + "&password=" + password + "&action=today";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    if (response.has("err_message") && !response.get("err_message").toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), response.get("err_message").toString(), Toast.LENGTH_LONG).show();
                    }
                    else {
                        if (!response.get("data").toString().equals("[]")) {
                            JSONObject data = (JSONObject) response.get("data");
                            NewsResponse newsResponse = new NewsResponse(data.toString());
                            newsResponse.parseJSON();

                            final ArrayList<String> news = newsResponse.news;
                            final ArrayList<String> links = newsResponse.urls;
                            final ArrayList<String> newspapers = newsResponse.newsPaperAndDate;
                            listView.setVisibility(View.VISIBLE);

                            ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_2, android.R.id.text1, news) {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View view = super.getView(position, convertView, parent);
                                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                                    text1.setTextColor(Color.BLACK);
                                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                                    text2.setTextColor(Color.DKGRAY);
                                    text1.setText(news.get(position).toString());
                                    text2.setText(newspapers.get(position).toString());
                                    return view;
                                }
                            };
                            listView.setAdapter(adapter);
                            listView.setClickable(true);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                                    Uri uri = Uri.parse(links.get(position)); // missing 'http://' will cause crashed
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            listView.setVisibility(View.GONE);
                            mMessage.setVisibility(View.VISIBLE);
                            mMessage.setText(response.get("message").toString());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //alertShow("Connect to intranet and try again", "Not Connected to Intranet");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listView.setVisibility(View.GONE);
                mMessage.setVisibility(View.VISIBLE);
                mMessage.setText(R.string.no_news_today);
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonObjReq);
    }
}



