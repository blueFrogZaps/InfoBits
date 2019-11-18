package com.bitspilani.library.infobits;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bitspilani.library.infobits.R;

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
        final String[] OnlineDb = {"ACM (Association for Computing Machinery)", "AIP (American Institute of Physics)", "ACS (American Chemical Society)", "APS (American Physical Society)","ASCE (American Society of Civil Engineers)","ASME (American Society of Mechanical Engineers)","Annual Reviews","Bentham Science","Capitaline Plus", "Cambridge University Press","EBSCO Information Services","Emerald Group Publishing","Grammarly","HEDBIB (International Bibliographic Database on Higher Education)","HeySuccess","IEEE (Institute of Electrical and Electronics Engineers)","IndiaStat","IOP (Institute of Physics)","Institute of Mechanical Engineers(IMechE)","JSTOR","MathSciNet","MethodsNow","OUP (Oxford University Press)","Portland Press","Project Euclid","Project Muse","Proquest Online","RSC (Royal Society of Chemistry)", "Springer","Science Direct","SciFinder","SIAM (Society for Industrial and Apllied Mathematics)","Taylor & Francis","Wiley Interscience","Learn More","Open Access Resources"};
        final String[] DbLinks = {"http://portal.acm.org/dl.cfm", "http://www.aip.org", "http://pubs.acs.org/about.html", "http://www.aps.org/", "http://ascelibrary.org/journals/all_journal_titles", "http://asmedigitalcollection.asme.org/journals.aspx", "http://arjournals.annualreviews.org/","http://www.eurekaselect.com", "http://www.capitaline.com/user/framepage.asp?id=1", "http://www.journals.cambridge.org/", "http://search.ebscohost.com/", "http://www.emeraldinsight.com/","https://www.grammarly.com/edu","http://hedbib.iau-aiu.net/","https://www.heysuccess.com", "http://www.ieee.org/ieeexplore/","https://www.indiastat.com","http://www.iop.org/EJ/","http://172.21.1.15/pdf/ime.pdf", "http://www.jstor.org/", "http://www.ams.org/mathscinet/index.html","https://sso.cas.org/as/Jd2pN/resume/as/authorization.ping", "http://www.oxfordjournals.org/en/", "http://www.portlandpress.com/", "http://projecteuclid.org/", "http://muse.jhu.edu/", "http://search.proquest.com/?accountid=81487", "http://www.rsc.org/is/journals/current/ejs.htm", "http://www.springerlink.com/journals/", "http://www.sciencedirect.com/", "http://172.21.1.15/services/SciFinder_registration.php", "http://epubs.siam.org", "http://www.tandfonline.com/", "http://www3.interscience.wiley.com/cgi-bin/home","http://172.21.1.15/pdf/Databases/Complete_List_of_Onlne_Databases.pdf","http://172.21.1.15/services/open_access_links.php"};
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, OnlineDb);
        ListView listView = (ListView) findViewById(R.id.onlineDb);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent defaultIntent;
                defaultIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(DbLinks[position]));
               /* if(DbLinks[position].contains("pdf"))
                    defaultIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(DbLinks[position]));
                else
                    defaultIntent = new Intent(OnlineDb.this,LoadBooks.class).putExtra("url",DbLinks[position]);*/
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
