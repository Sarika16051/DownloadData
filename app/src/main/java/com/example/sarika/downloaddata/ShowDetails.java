package com.example.sarika.downloaddata;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class ShowDetails extends AppCompatActivity {

    private TextView showTitle;
    private TextView showDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarShow);
        setSupportActionBar(toolbar);

        //For enabliing Hierarchical Navigation
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        showTitle = (TextView) findViewById(R.id.titleView);
        showDetail = (TextView) findViewById(R.id.viewDetail);

        String titleDetail = "";
        //getting Title from main activity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            titleDetail = bundle.getString("Data");
        }

        String detail = getDetail();

        //setting values in the textView
        showTitle.setText(titleDetail);
        showDetail.setText(detail);

    }

    //this method will return one paragraph from the downloaded file to display on activity and the complete content is printed on console
    public String getDetail() {
        File file = new File("/sdcard/about.txt");
        StringBuilder sb = new StringBuilder();
        StringBuilder sb1 = new StringBuilder();
        String result = "";
        String text = "";
        //Log.d(TAG, "File : " + file);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = "";

            int countLine = 0;

            int p = 0;

            int counts = 0;

            while ((line = br.readLine()) != null) {

                String trial = line;
                String checkLine = line.replaceAll("<[^>]*>", "");
                if (!checkLine.equals("")) {
                    sb.append(line + "");
                    counts++;
                    if (line.contains("Indraprastha Institute of Information Technology, Delhi (")) {
                        p = 1;
                    }

                    if (p == 1) {
                        sb1.append(line);
                        if (line.contains("far")) {
                            p = 0;
                        }
                    }

                }

            }

            result = sb.toString().replaceAll("<[^>]*>", "");
            text = sb1.toString().replaceAll("<[^>]*>", "");


            Log.d("TAG", result);
            br.close();

        } catch (IOException e) {
            Log.d("MainActivity", "Error occured.... " + e);
        }
        return text;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
