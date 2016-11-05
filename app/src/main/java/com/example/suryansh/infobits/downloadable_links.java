package com.example.suryansh.infobits;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView;
import android.view.View;
import android.net.Uri;
import android.content.Intent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import android.text.Spanned;
import java.util.zip.Inflater;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class downloadable_links extends homepage implements  AdapterView.OnItemClickListener{

    ListView listView ;
    private String[] values;
    private String[] links;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.downloadable_links);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String reference =bundle.getString("reference");
        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        //setSupportActionBar(toolbar1);
        toolbar.setTitle(reference);
        File profilepic = new File(dir, avatar);
        try {
            fileInput = new FileInputStream(profilepic);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(fileInput != null){
            setToolBarAvatar(profilepic);
        }
        listView = (ListView) findViewById(R.id.listView);
        switch (reference){
            case "Pearson e-Books":
                values = new String[]{
                       "Biology (5)" ,
                        "Chemistry (9)",
                        "Computer Science (117)",
                        "Electrical and Electronic Engineering (15)",
                        "Humanities and Social Sciences (26)",
                        "Management (29)"
                };
                links = new String[]{
                        "http://www.bits-pilani.ac.in:12354/pdf/pearson_e-books/Biology.pdf",
                        "http://www.bits-pilani.ac.in:12354/pdf/pearson_e-books/Chemistry.pdf",
                        "http://www.bits-pilani.ac.in:12354/pdf/pearson_e-books/Computer_Science.pdf",
                        "http://www.bits-pilani.ac.in:12354/pdf/pearson_e-books/Electrical_and_Electronic_Engineering.pdf",
                        "http://www.bits-pilani.ac.in:12354/pdf/pearson_e-books/Humanities_and_Social_Sciences.pdf",
                        "http://www.bits-pilani.ac.in:12354/pdf/pearson_e-books/Management.pdf"
                };
                break;
            case "Elsevier e-Books":
                values = new String[]{

                        "Chemical Engineering (10)",
                        "Chemistry (3)",
                        "Mathematics (16)",
                        "Pharmacology, Toxicology and Pharmaceutical Science (13)"

                };
                links = new String[]{
                        "http://www.bits-pilani.ac.in:12354/pdf/Elsevier_e-books/Chemical_Engineering.pdf",
                        "http://www.bits-pilani.ac.in:12354/pdf/Elsevier_e-books/Chemistry.pdf",
                        "http://www.bits-pilani.ac.in:12354/pdf/Elsevier_e-books/Mathematics.pdf",
                        "http://www.bits-pilani.ac.in:12354/pdf/Elsevier_e-books/Pharmacology.pdf",
                };
                toolbar.setLogo(R.mipmap.science_direct1);
                break;
            case "Question Papers":
                values = new String[] {"CLICK HERE", "iPhone", "WindowsMobile",
                        "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                        "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
                        "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
                        "Android", "iPhone", "WindowsMobile" };

                links = new String[] {"http://www.intechopen.com/books", "iPhone", "WindowsMobile",
                        "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                        "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
                        "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
                        "Android", "iPhone", "WindowsMobile" };

                break;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(i<values.length){
            Uri uri = Uri.parse(links[i]);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(links[i]));
            startActivity(intent);
        }
    }

}
