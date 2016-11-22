package com.bitspilani.library.infobits;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        newArrivals.setAdapter(new CustomAdapter(this.getContext(), bookNameList, bookImages, bookLinks));
        journals.setAdapter(new CustomAdapter(this.getContext(), journalNameList, journalImages, journalLinks));
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
