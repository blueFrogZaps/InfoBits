package com.example.suryansh.infobits;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import android.content.SharedPreferences;

public class homepage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    ViewPager viewPager;
    Swipe_Adapter adapter;
    DrawerLayout drawerlayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    DBHandler dbhandler;
    JSONObject internal;
    ProgressBar resPBar;
    Button reserveB;
    ArrayList<RadioButton> pagination = new ArrayList<>();
    ArrayList<String> urls = new ArrayList<>();
    ArrayList<Bitmap> images = new ArrayList<>();
    MenuItem cat;
    RadioGroup pagin;
    SharedPreferences login_info;
    SharedPreferences.Editor edit_login_info;
    Map<String, ?> user;
    public static String username, name, password, usercat, email, avatar;
    public static FileInputStream fileInput = null;
    public final static String apiURL = "http://172.21.1.15/apis/";
    public final static String imageApiURL = "http://172.21.1.15/uploads/";
//    public final static String apiURL = "http://192.168.3.10:80/infoBITS/apis/";
//    public final static String imageApiURL = "http://192.168.3.10:80/infoBITS/uploads/";
    public final static String openURL = "http://universe.bits-pilani.ac.in:12354/";
    File dir;
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        login_info = getSharedPreferences("login_info", Context.MODE_PRIVATE);
        edit_login_info = login_info.edit();
        user = login_info.getAll();
        if (!user.isEmpty()) {
            username = user.get("username").toString();
            name = user.get("name").toString();
            password = user.get("password").toString();
            usercat = user.get("category").toString();
            email = user.get("email").toString();
            avatar = user.get("avatar").toString();
        }
        View v = findViewById(R.id.homeicons);
        Integer[] icons = new Integer[]{R.id.os, R.id.cwl, R.id.dn, R.id.libs, R.id.libr, R.id.opac};
        Integer[] dimens = getDimens();
        Integer height = dimens[0]/4 - getCorrectPixels(24), width = (dimens[1] - getCorrectPixels(24 * (icons.length/2)))/3;
        Integer dim = Math.min(height, width);
        for(int i = 0; i < icons.length; i++){
            ImageView img = (ImageView) v.findViewById(icons[i]);
            img.setMinimumHeight(dim);
            img.setMinimumWidth(dim);
        }
        toolbar = (Toolbar) findViewById(R.id.nav_toolbar);
        drawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);
        dir = getFilesDir();
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        drawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerlayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerlayout.setDrawerListener(actionBarDrawerToggle);
        drawerlayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        cat = navigationView.getMenu().getItem(0);
        cat.setChecked(true);
        navigationView.setItemIconTintList(null);
        View navHeader = navigationView.getHeaderView(0);
        if(navHeader.findViewById(R.id.brand) != null) {
            if (user.isEmpty()) {
                ((TextView) navHeader.findViewById(R.id.brand)).setText(R.string.guest_user);
                ((ImageView) navHeader.findViewById(R.id.profile)).setImageResource(R.mipmap.logo);
            } else {
                ((TextView) navHeader.findViewById(R.id.brand)).setText(name);
                ((TextView) navHeader.findViewById(R.id.email)).setText(email);
                File profilepic = new File(dir, avatar);
                try {
                    fileInput = new FileInputStream(profilepic);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (fileInput == null) {
                    ((ImageView) navHeader.findViewById(R.id.profile)).setImageResource(R.mipmap.logo);
                } else {
                    ((ImageView) navHeader.findViewById(R.id.profile)).setImageBitmap(BitmapFactory.decodeStream(fileInput));
                    setToolBarAvatar(profilepic);
                }

            }
        }
        dbhandler = new DBHandler(this, null, null);
        internal = dbhandler.selectData(2, "1 ORDER BY id ASC");
        if (viewPager.getAdapter() == null)
            getNotices();
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        if (user.isEmpty()) {
            menuInflater.inflate(R.menu.menu_no_user, menu);
        } else {
            menuInflater.inflate(R.menu.menu_user, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = null;
        switch (item.getItemId()) {
            case R.id.user:
                i = new Intent(homepage.this, user_settings.class);
                i.putExtra("previous", this.getClass());
                startActivityForResult(i, 0);
                break;
            case R.id.login:
                i = new Intent(getApplicationContext(), login.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
//            case R.id.signup:
//                Intent i13 = new Intent(homepage.this, signup.class);
//                startActivity(i13);
//                break;
            case R.id.logout:
                edit_login_info.clear();
                edit_login_info.apply();
                i = new Intent(getApplicationContext(), homepage.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (user.isEmpty()) {
            LogInToast();
        } else {
            Intent i = null;
            if (id == R.id.os_id) {
                i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://search.ebscohost.com/login.aspx?authtype=uid&user=bits2015&password=pilani&profile=eds"));
            } else if (id == R.id.comm_id) {
                i = new Intent(homepage.this, ConnectWithLibrary.class);
            } else if (id == R.id.news_id) {
                i = new Intent(homepage.this, DailyNews.class);
            } else if (id == R.id.ibb_id) {
                i = new Intent(homepage.this, infoBitsBulletin.class);
            } else if (id == R.id.lf_id) {
                i = new Intent(homepage.this, lfmsAllItems.class);
            } /*else if (id == R.id.qp_id) {
            Intent qpI = new Intent(homepage.this, downloadable_links.class);
            qpI.putExtra("title", "Question Papers");
            qpI.putExtra("reference", "Question Papers");
            startActivity(qpI);
        }*/ else if (id == R.id.eb_id) {
                i = new Intent(homepage.this, ebooks.class);
            } else if (id == R.id.od_id) {
                i = new Intent(homepage.this, OnlineDb.class);
            } else if (id == R.id.opac_id) {
                i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://172.21.1.37"));
            }
            startActivity(i);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    //Methods to handle button clicks on homescreen

    public void onClickLibr(View view) {
        if (user.isEmpty()) {
            LogInToast();
        } else {
            Intent i = new Intent(homepage.this, LibRes.class);
            startActivity(i);
        }
    }

    public void onClickLibs(View view) {
        if (user.isEmpty()) {
            LogInToast();
        } else {
            Intent i = new Intent(homepage.this, LibService.class);
            startActivity(i);
        }
    }

    public void onClickOs(View view) {
        if (user.isEmpty()) {
            LogInToast();
        } else {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://search.ebscohost.com/login.aspx?authtype=uid&user=bits2015&password=pilani&profile=eds"));
            startActivity(browserIntent);
        }
    }

    public void onClickDN(View view) {
        if (user.isEmpty()) {
            LogInToast();
        } else {
            Intent i = new Intent(homepage.this, DailyNews.class);
            startActivity(i);
        }
    }

    public void onClickOPAC(View view) {
        if (user.isEmpty()) {
            LogInToast();
        } else {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://172.21.1.37"));
            startActivity(browserIntent);
        }
    }

    public void onClickCWL(View view) {
        if (user.isEmpty()) {
            LogInToast();
        } else {
            Intent i = new Intent(homepage.this, ConnectWithLibrary.class);
            startActivity(i);
        }
    }

    @Override
    public void onBackPressed() {
        if (this.drawerlayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerlayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void getNotices() {
        urls.clear();
        images.clear();
        JSONObject botw = dbhandler.selectData(3, "1");
        JSONObject newsjson = dbhandler.selectData(1, "1 ORDER BY id DESC LIMIT 0,3");
        Iterator botwiter = botw.keys();
        JSONObject botwjson;
        String[] botwarr = {"", "", ""}, news = {"", "", ""};
        internal = dbhandler.selectData(2, "1 ORDER BY id ASC");
        Iterator iter = internal.keys();
        Iterator newsiter = newsjson.keys();
        File image;
        FileInputStream fileInput;
        try {
            String url = "";
            if (botwiter.hasNext()) {
                String botwkey = botwiter.next().toString();
                botwjson = (JSONObject) botw.get(botwkey);
                botwarr[0] = botwjson.get("title").toString();
                botwarr[1] = botwjson.get("author").toString();
                botwarr[2] = botwjson.get("image").toString();
            }
            image = new File(dir, botwarr[2]);
            fileInput = new FileInputStream(image);
            images.add(BitmapFactory.decodeStream(fileInput));
            fileInput.close();
            while (iter.hasNext()) {
                String key = iter.next().toString();
                JSONObject data = (JSONObject) internal.get(key);
                image = new File(dir, data.get("image").toString());
                fileInput = new FileInputStream(image);
                images.add(BitmapFactory.decodeStream(fileInput));
                try {
                    url = URLDecoder.decode(data.get("link").toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Boolean m = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$").matcher(url).find();
                if (!url.isEmpty()) {
                    if (url.contains("http://")) {
                        if (!url.contains("www.")) {
                            if (!m) {
                                url = "http://www." + url.substring(url.indexOf("http://") + 7);
                            }
                        }
                    } else {
                        if (url.contains("www.") || m) {
                            url = "http://" + url;
                        } else {
                            url = "http://www." + url;
                        }
                    }
                }
                urls.add(url);
                fileInput.close();
            }
            int i = 0;
            while (newsiter.hasNext()) {
                JSONObject newsitem = (JSONObject) newsjson.get(newsiter.next().toString());
                news[i] = newsitem.get("title").toString() + "," + newsitem.get("newspaper").toString() + ", pg. " + newsitem.get("pages").toString();
                i++;
            }
            if (internal.length() == 0 && botw.length() == 0) {
                findViewById(R.id.view_pager).setVisibility(View.GONE);
                findViewById(R.id.no_notice).setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //((TextView) findViewById(R.id.textView14)).setText(news[0]);
        adapter = new Swipe_Adapter(this, images, urls, botwarr, news);
        if (adapter.getCount() == 0) {
            findViewById(R.id.no_notice).setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
            findViewById(R.id.pagination).setVisibility(View.GONE);
        } else {
            findViewById(R.id.no_notice).setVisibility(View.GONE);
            viewPager.setAdapter(adapter);
            viewPager.setVisibility(View.VISIBLE);
            pagin = (RadioGroup) findViewById(R.id.paginationGroup);
            pagin.clearCheck();
            for (int i = 0; i < adapter.getCount(); i++) {
                RadioButton rbtn = new RadioButton(this);
                rbtn.setText("");
                if (Build.VERSION.SDK_INT >= 21)
                    rbtn.setButtonTintList(this.getResources().getColorStateList(R.color.colorPrimaryDark));
                else if (Build.VERSION.SDK_INT >= 23)
                    rbtn.setButtonTintList(this.getResources().getColorStateList(R.color.colorPrimaryDark, this.getTheme()));
                rbtn.setChecked(false);
                rbtn.setClickable(false);
                pagin.addView(rbtn, i, pagin.getLayoutParams());
                pagination.add(i, rbtn);
            }
            pagination.get(0).setChecked(true);
            findViewById(R.id.pagination).setVisibility(View.VISIBLE);
            viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }

                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    pagin.clearCheck();
                    pagination.get(position).setChecked(true);
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "homepage Page",
                Uri.parse("http://host/path"),
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
                Action.TYPE_VIEW,
                "homepage Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.example.suryansh.infobits/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public class Swipe_Adapter extends PagerAdapter {
        private ArrayList<Bitmap> image_resources = new ArrayList<>();
        private ArrayList<String> urls = new ArrayList<>();
        private String[] botw;
        private String[] news;
        private Context ctx;
        private LayoutInflater layoutinflator;

        public Swipe_Adapter(Context ctx, ArrayList<Bitmap> images, ArrayList<String> urls, String[] botw, String[] news) {
            this.ctx = ctx;
            this.image_resources = images;
            this.urls = urls;
            this.botw = botw;
            this.news = news;
        }

        @Override
        public int getCount() {
            if (news[0].isEmpty()) {
                return image_resources.size();
            } else {
                return image_resources.size() + 1;
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == (RelativeLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            layoutinflator = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View item_view;
            if (botw[0].isEmpty()) {
                if (news[0].isEmpty()) {
                    item_view = layoutinflator.inflate(R.layout.swipe_image, container, false);
                    ImageView imageView = (ImageView) item_view.findViewById(R.id.image_view);
                    imageView.setImageBitmap(image_resources.get(position));
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = urls.get(viewPager.getCurrentItem() - 1);
                            if (!url.isEmpty()) {
                                Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                                startActivity(browserIntent);
                            }
                        }
                    });
                } else {
                    if (position == image_resources.size()) {
                        item_view = layoutinflator.inflate(R.layout.daily_news_notice, container, false);
                        TextView[] newsviews = {(TextView) findViewById(R.id.news1), (TextView) findViewById(R.id.news2), (TextView) findViewById(R.id.news3)};
                        for (int i = 0; i < 3; i++) {
                            newsviews[i].setText(news[i]);
                        }
                        item_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (user.isEmpty()) {
                                    LogInToast();
                                } else {
                                    Intent i = new Intent(homepage.this, DailyNews.class);
                                    startActivity(i);
                                }
                            }
                        });
                    } else {
                        item_view = layoutinflator.inflate(R.layout.swipe_image, container, false);
                        ImageView imageView = (ImageView) item_view.findViewById(R.id.image_view);
                        imageView.setImageBitmap(image_resources.get(position));
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String url = urls.get(viewPager.getCurrentItem() - 1);
                                if (!url.isEmpty()) {
                                    Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                                    startActivity(browserIntent);
                                }
                            }
                        });
                    }
                }
            } else {
                if (news[0].isEmpty()) {
                    if (position == 0) {
                        item_view = layoutinflator.inflate(R.layout.book_of_the_week, container, false);
                        ImageView imageView = (ImageView) item_view.findViewById(R.id.book);
                        imageView.setImageBitmap(image_resources.get(position));
                        resPBar = (ProgressBar) item_view.findViewById(R.id.progressBar);
                        ((TextView) item_view.findViewById(R.id.title)).setText("Title: " + botw[0]);
                        ((TextView) item_view.findViewById(R.id.author)).setText("Author: " + botw[1]);
                        reserveB = (Button) item_view.findViewById(R.id.reserve);
                        reserveB.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (user.isEmpty()) {
                                    Toast.makeText(homepage.this, "Please Log In to Reserve Book!", Toast.LENGTH_LONG).show();
                                } else {
                                    try {
                                        if (isConnected()) {
                                            String urlS = apiURL + "reserve.php?username=" + username + "&password=" + password + "&title=" + URLEncoder.encode(botw[0], "utf-8") + "&author=" + URLEncoder.encode(botw[1], "utf-8");
                                            new APICall().execute(urlS);
                                            resPBar.setVisibility(View.VISIBLE);
                                            reserveB.setClickable(false);
                                        } else {
                                            Toast.makeText(homepage.this, "Not Connected to BITS Intranet!", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    } else {
                        item_view = layoutinflator.inflate(R.layout.swipe_image, container, false);
                        ImageView imageView = (ImageView) item_view.findViewById(R.id.image_view);
                        imageView.setImageBitmap(image_resources.get(position));
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String url = urls.get(viewPager.getCurrentItem() - 1);
                                if (!url.isEmpty()) {
                                    Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                                    startActivity(browserIntent);
                                }
                            }
                        });
                    }
                } else {
                    if (position == 0) {
                        item_view = layoutinflator.inflate(R.layout.book_of_the_week, container, false);
                        ImageView imageView = (ImageView) item_view.findViewById(R.id.book);
                        imageView.setImageBitmap(image_resources.get(position));
                        ((TextView) item_view.findViewById(R.id.title)).setText("Title: " + botw[0]);
                        ((TextView) item_view.findViewById(R.id.author)).setText("Author: " + botw[1]);
                        resPBar = (ProgressBar) item_view.findViewById(R.id.progressBar);
                        reserveB = (Button) item_view.findViewById(R.id.reserve);
                        reserveB.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (user.isEmpty()) {
                                    Toast.makeText(homepage.this, "Please Log In to Reserve Book!", Toast.LENGTH_LONG).show();
                                } else {
                                    try {
                                        if (isConnected()) {
                                            String urlS = apiURL + "reserve.php?username=" + username + "&password=" + password + "&title=" + URLEncoder.encode(botw[0], "utf-8") + "&author=" + URLEncoder.encode(botw[1], "utf-8");
                                            new APICall().execute(urlS);
                                            resPBar.setVisibility(View.VISIBLE);
                                            reserveB.setClickable(false);
                                        } else {
                                            Toast.makeText(homepage.this, "Not Connected to BITS Intranet!", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    } else if (position == image_resources.size()) {
                        item_view = layoutinflator.inflate(R.layout.daily_news_notice, container, false);
                        TextView[] newsviews = {(TextView) item_view.findViewById(R.id.news1), (TextView) item_view.findViewById(R.id.news2), (TextView) item_view.findViewById(R.id.news3)};
                        for (int i = 0; i < 3; i++) {
                            String news_text = "\u2022" + news[i];
                            newsviews[i].setText(news_text);
                        }
                        item_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (user.isEmpty()) {
                                    LogInToast();
                                } else {
                                    Intent i = new Intent(homepage.this, DailyNews.class);
                                    startActivity(i);
                                }
                            }
                        });
                    } else {
                        item_view = layoutinflator.inflate(R.layout.swipe_image, container, false);
                        ImageView imageView = (ImageView) item_view.findViewById(R.id.image_view);
                        imageView.setImageBitmap(image_resources.get(position));
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String url = urls.get(viewPager.getCurrentItem() - 1);
                                if (!url.isEmpty()) {
                                    Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                                    startActivity(browserIntent);
                                }
                            }
                        });
                    }
                }
            }
            container.addView(item_view);
            return item_view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);
        }
    }

    private class APICall extends AsyncTask<String, Integer, String> {

        String err;
        String urls;

        @Override
        protected String doInBackground(String[] params) {
            String urlString = params[0];
            urls = urlString;
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);
            } catch (Exception e) {
                err = "Network Error! Ensure you're connected to BITS Intranet";
            }
            return responseStrBuilder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            resPBar.setVisibility(View.GONE);
            reserveB.setClickable(true);
            if (!result.isEmpty()) {
                try {
                    JSONObject json = new JSONObject(result);
                    if (json.get("err_message").toString().isEmpty()) {
                        if (!json.get("message").toString().isEmpty()) {
                            Toast.makeText(homepage.this, json.get("message").toString(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(homepage.this, json.get("err_message").toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(homepage.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else {
                if (!err.isEmpty()) {
                    Toast.makeText(homepage.this, err, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            Toast.makeText(homepage.this, "Not Connected to BITS Intranet!", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void LogInToast() {
        Toast.makeText(this, "Please Login to Access!", Toast.LENGTH_LONG).show();
    }

    public Integer[] getDimens(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        return new Integer[]{height, width};
    }

    public int getCorrectPixels(float dips){
        float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) ((int) dips * scale);
        return pixels;
    }

    public void setToolBarAvatar(File profilepic){
        Drawable d = Drawable.createFromPath(profilepic.getAbsolutePath());
        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
        Integer h = bitmap.getHeight();
        Integer w = bitmap.getWidth();
        if(h > w){
            w = w * 150/h;
            h = 150;
        }
        else if(h < w){
            h = h * 150/w;
            w = 150;
        }
        else{
            h = w = 150;
        }
        Drawable dr = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, w, h, true));
        toolbar.setOverflowIcon(dr);
    }
}
