package com.example.suryansh.infobits;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.text.method.LinkMovementMethod;
import android.text.Html;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ebooks extends homepage{

    public Toolbar toolbar;
    Dialog dialog;
    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ebooks1);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        File profilepic = new File(dir, avatar);
        try {
            fileInput = new FileInputStream(profilepic);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(fileInput != null){
            setToolBarAvatar(profilepic);
        }
        ListView l = (ListView) findViewById(R.id.ebookList);
        String[] ebooks = {"Pearson Education", "Open Textbook Library", "Springer", "Taylor & Francis eBooks", "Open Access", "Intech", "Science Direct", "EBSCO"};
        Integer[] ebookImages = {R.mipmap.pearson_education1, R.mipmap.opentextbook_library1, R.mipmap.springer, R.mipmap.taylor1, R.mipmap.oapen, R.mipmap.intech1, R.mipmap.science_direct1, R.mipmap.ebsco1};
        TextView click = (TextView) findViewById(R.id.ebscoClick);
        click.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ecm.ebscohost.com/User/Login"));
                startActivity(i);
            }
        });
        Intent[] links = {
                new Intent(ebooks.this, downloadable_links.class).putExtra("reference", "Pearson e-Books"),
                new Intent(Intent.ACTION_VIEW, Uri.parse("http://open.umn.edu/opentextbooks/")),
                new Intent(Intent.ACTION_VIEW, Uri.parse("http://link.springer.com/search/page/3?showAll=false&facet-content-type=%22Book%22")),
                new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.tandfebooks.com/page/openaccess#listofOAtitles")),
                new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.oapen.org/search?sort=year;f1-language=English")),
                new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.intechopen.com/books")),
                new Intent(ebooks.this, downloadable_links.class).putExtra("reference", "Elsevier e-Books"),
                new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.bits-pilani.ac.in:12354/pdf/EBSCO%20eBooks%20Mannual.pdf"))};
        l.setAdapter(new MyAdapter(this, ebooks, ebookImages, links));
    }

    public class MyAdapter extends BaseAdapter {

        private String[] ebook;
        private Integer[] images;
        private Intent[] link;
        public MyAdapter(Context context, String[] ebooks, Integer[] ebook_images, Intent[] links) {
            ebook = ebooks;
            images = ebook_images;
            link = links;
        }

        @Override
        public int getCount() { return ebook.length;}

        @Override
        public long getItemId(int position) { return position;}

        @Override
        public Fragment getItem(int position) {
            return null;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = getLayoutInflater().inflate(R.layout.ebook_item, null);
            final TextView header = (TextView) v.findViewById(R.id.ebook);
            final ImageView image = (ImageView)v.findViewById(R.id.ebook_image);
            header.setText(ebook[position]);
            image.setImageResource(images[position]);
            v.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    startActivity(link[position]);
                }
            });
            return v;
        }
    }

}

