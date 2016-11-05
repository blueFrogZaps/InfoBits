package com.example.suryansh.infobits;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class OnlineDb extends homepage {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_db);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final String[] OnlineDb = {"ACM (Association for Computing Machinery)", "AIP (American Institute of Physics)", "ACS (American Chemical Society)", "APS (American Physical Society)","ASCE (American Society of Civil Engineers)","ASME (American Society of Mechanical Engineers)","Annual Reviews","Capitaline Plus", "Cambridge University Press","EBSCO Information Services","Emerald Group Publishing","HEDBIB (International Bibliographic Database on Higher Education)","IEEE (Institute of Electrical and Electronics Engineers)","IME (Institution of Mechanical Engineers)","ISID (Institute for Studies in Industrial Development)","IOP (Institute of Physics)","Thomson Reuters Web of Science", "J-Gate","JSTOR","MathSciNet","OUP (Oxford University Press)","Portland Press","Project Euclid","Project Muse","Proquest Online","RSC (Royal Society of Chemistry)", "Springer","Science Direct","SciFinder","SIAM (Society for Industrial and Apllied Mathematics)","Taylor & Francis","Wiley Interscience"};
        final String[] DbLinks = {"http://portal.acm.org/dl.cfm", "http://www.aip.org", "http://pubs.acs.org/about.html", "http://www.aps.org/", "http://ascelibrary.org/journals/all_journal_titles", "http://asmedigitalcollection.asme.org/journals.aspx", "http://arjournals.annualreviews.org/", "http://www.capitaline.com/user/framepage.asp?id=1", "http://www.journals.cambridge.org/", "http://search.ebscohost.com/", "http://www.emeraldinsight.com/", "http://hedbib.iau-aiu.net/", "http://www.ieee.org/ieeexplore/", "http://www.bits-pilani.ac.in:12354/services/ime.php", "http://111.93.33.222/login3.asp", "http://www.iop.org/EJ/", "http://www.isiknowledge.com/", "http://jgateplus.com/search/", "http://www.jstor.org/", "http://www.ams.org/mathscinet/index.html", "http://www.oxfordjournals.org/en/", "http://www.portlandpress.com/", "http://projecteuclid.org/", "http://muse.jhu.edu/", "http://search.proquest.com/?accountid=81487", "http://www.rsc.org/is/journals/current/ejs.htm", "http://www.springerlink.com/journals/", "http://www.sciencedirect.com/", "http://www.bits-pilani.ac.in:12354/services/SciFinder_registration.php", "http://epubs.siam.org/", "http://www.tandfonline.com/", "http://www3.interscience.wiley.com/cgi-bin/home"};
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, OnlineDb);
        ListView listView = (ListView) findViewById(R.id.onlineDb);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent defaultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(DbLinks[position]));
                startActivity(defaultIntent);
            }
        });
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
}
