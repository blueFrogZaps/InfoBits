package com.bitspilani.library.infobits;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitspilani.library.infobits.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class LibService extends homepage {

    DrawerLayout drawerlayout;
    NavigationView navigationView;
    MenuItem cat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lib_service);
        toolbar = (Toolbar) findViewById(R.id.nav_toolbar);
        drawerlayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        setSupportActionBar(toolbar);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        cat = navigationView.getMenu().getItem(0);
        cat.setChecked(true);
        View v = findViewById(R.id.libricons);
        Integer[] icons = new Integer[]{R.id.imgbt_info, R.id.imgbt_news, R.id.imgbt_service4, R.id.imgbt_service5};
        Integer[] dimens = getDimens();
        Integer height = (dimens[0]/2 - getCorrectPixels(48))/2, width = (dimens[1] - getCorrectPixels(36 * (icons.length/2)))/2;
        Integer dim = Math.min(height, width);
        for(int i = 0; i < icons.length; i++){
            ImageView img = (ImageView) v.findViewById(icons[i]);
            img.setMinimumHeight(dim);
            img.setMinimumWidth(dim);
        }
        View navHeader = navigationView.getHeaderView(0);
        ((TextView) navHeader.findViewById(R.id.brand)).setText(name);
        ((TextView) navHeader.findViewById(R.id.email)).setText(email);
        ((TextView) navHeader.findViewById(R.id.brand)).setText(name);
        ((TextView) navHeader.findViewById(R.id.email)).setText(email);
        File profilepic = new File(dir, avatar);
        try {
            fileInput = new FileInputStream(profilepic);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(fileInput == null){
            ((ImageView) navHeader.findViewById(R.id.profile)).setImageResource(R.mipmap.logo);
        }else{
            ((ImageView) navHeader.findViewById(R.id.profile)).setImageBitmap(BitmapFactory.decodeStream(fileInput));
            setToolBarAvatar(profilepic);
        }
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerlayout,toolbar,R.string.drawer_open,R.string.drawer_close);
        drawerlayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        super.onNavigationItemSelected(item);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    public void onClickLF(View view){
        if(user.isEmpty()){
            LogInToast();
        }
        else{
            Intent i = new Intent(LibService.this, lfmsAllItems.class);
            startActivity(i);
        }
    }

    public void onClickIBB(View view){
        if(user.isEmpty()){
            LogInToast();
        }
        else{
            Intent i = new Intent(LibService.this, infoBitsBulletin.class);
            startActivity(i);
        }
    }

}
