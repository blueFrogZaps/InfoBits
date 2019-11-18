package com.bitspilani.library.infobits;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.bitspilani.library.infobits.R;

public class MyFragment extends Fragment{

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public String [] bookNameList, bookImages, journalNameList, journalImages, bookLinks, journalLinks;
    public GridView journals, newArrivals;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.custom_fragmet, container, false);
        newArrivals = (GridView) v.findViewById(R.id.newArrivalsGrid);
        journals = (GridView) v.findViewById(R.id.journalsGrid);
        for(int i =0;i<bookNameList.length;i++)
            System.out.println("NAMEE: "+bookNameList[i]);
        newArrivals.setAdapter(new CustomAdapter(this.getContext(), bookNameList, bookImages, bookLinks));
        newArrivals.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("POSITION: "+position);
                Intent browserIntent =
                        new Intent(Intent.ACTION_VIEW, Uri.parse(bookLinks[position]));
                        //new Intent(getContext(),LoadBooks.class).putExtra("url",bookLinks[position]);
                startActivity(browserIntent);
            }
        });
        journals.setAdapter(new CustomAdapter(this.getContext(), journalNameList, journalImages, journalLinks));
        journals.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(journalLinks[position]));
                        //new Intent(getContext(),LoadBooks.class).putExtra("url",journalLinks[position]);
                startActivity(browserIntent);
            }
        });
        return v;
    }

    public void setList(String[]journalList, String[]bookList, String[]bookImages, String[]journalImages, String[]bookLinks, String[]journalLinks){
        this.bookNameList = bookList;
        this.bookImages = bookImages;
        this.journalImages = journalImages;
        this.journalNameList = journalList;
        this.bookLinks = bookLinks;
        this.journalLinks = journalLinks;
    }
}
