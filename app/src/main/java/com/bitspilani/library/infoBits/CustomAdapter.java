package com.bitspilani.library.infoBits;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitspilani.library.infoBits.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class CustomAdapter extends BaseAdapter{

    String [] result;
    Context context;
    String[] links;
    String [] imageUrl;
    File dir;
    LayoutInflater inflater;
    Integer pos;

    public CustomAdapter(Context c, String[] prgmNameList, String[] prgmImages, String[] prgmLinks) {
        // TODO Auto-generated constructor stub
        result = prgmNameList;
        for(int i = 0;i<prgmNameList.length;i++)
            System.out.println("NAME: "+i+":"+prgmNameList[i]);
        links = prgmLinks;
        for(int i = 0;i<prgmLinks.length;i++)
            System.out.println("LINKS "+i+":"+prgmLinks[i]);
        context = c;
        imageUrl = prgmImages;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if(result!=null)
          return result.length;
         return 0;
    }

    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder{
        TextView tv;
        ImageView iv;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView;
        dir = context.getFilesDir();
        Holder holder = new Holder();
        inflater = LayoutInflater.from(context);
        rowView = inflater.inflate(R.layout.infobits_customcell, null);
        holder.iv = (ImageView) rowView.findViewById(R.id.bookImage);
        File image = new File(dir, imageUrl[i]);
        FileInputStream fileInput = null;
        pos = i;

        try {
            fileInput = new FileInputStream(image);
            holder.iv.setImageBitmap(BitmapFactory.decodeStream(fileInput));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
       /* rowView.setOnClickListener(new View.OnClickListener(){
            int position = pos;
            @Override
            public void onClick(View view) {
                System.out.println("URL: "+pos+":"+links[pos]);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(links[pos]));
                context.startActivity(browserIntent);
            }
        });*/
        return rowView;
    }


}