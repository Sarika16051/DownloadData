package com.example.sarika.downloaddata;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;


public class MainActivity extends AppCompatActivity {

    private static String urlString = "https://www.iiitd.ac.in/about";
    private static String TAG = "DownloadFile";
    private Button startDownload;
    private Button showData;
    private URL url;
    private String text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        startDownload = (Button) findViewById(R.id.startDownload);
        showData = (Button) findViewById(R.id.showData);

        try {
            url = new URL(urlString);
        } catch (Exception e) {
            Log.d("MAINACTIVITY", "error getting url");
        }

        startDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean connectionAvailable = getConnectionAvailabilty();
                if (connectionAvailable == false) {
                    noConnectionToast();
                } else {
                    displayToast();
                    new DownloadFile().execute(url);
                }
            }
        });

        showData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = getDetail();
                if (text.equalsIgnoreCase("")) {
                    displayNullToast();
                } else {
                    Intent in = new Intent(MainActivity.this, ShowDetails.class);
                    in.putExtra("Data", text);
                    startActivity(in);
                }
            }
        });

    }

    public boolean getConnectionAvailabilty() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void displayToast() {
        Toast.makeText(this, "Starting download", Toast.LENGTH_SHORT).show();
    }

    public void noConnectionToast() {
        Toast.makeText(this, "Internet Connection not available", Toast.LENGTH_SHORT).show();
    }

    public void displayNullToast() {
        Toast.makeText(this, "No data downloaded", Toast.LENGTH_SHORT).show();
    }

    public String getDetail() {
        File file = new File("/sdcard/about.txt");
        StringBuilder sb = new StringBuilder();
        StringBuilder sb1 = new StringBuilder();
        String result = "";
        String text = "";

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
                    if (counts == 1) {
                        sb1.append(line);
                    }

                }

            }

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
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    class DownloadFile extends AsyncTask<URL, Integer, Long> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Long doInBackground(URL... params) {

            long totalSize = 0;

            try {
                URLConnection urlConnection = params[0].openConnection();
                urlConnection.connect();
                int LengthOfData = urlConnection.getContentLength();
                int i = 0;

                InputStream input = new BufferedInputStream(params[0].openStream(), 8192);

                OutputStream output = new FileOutputStream("/sdcard/about.txt");

                byte data[] = new byte[1024];

                int count = 0;

                int successful = 0;
                while ((count = input.read(data)) != -1) {
                    totalSize = totalSize + count;

                    publishProgress((int) ((totalSize / (float) LengthOfData) * 100));

                    output.write(data, 0, count);

                    successful = 1;
                    if (isCancelled()) {
                        successful = 0;
                        break;
                    }

                }

                if (successful == 1) {
                    Log.d(TAG, "Successful completion");
                } else {
                    Log.d(TAG, "Unsuccessful Completion");
                }
                output.flush();

                output.close();
                input.close();
            } catch (Exception e) {
                System.out.println("Error occured..." + e);
            }

            return totalSize;
        }

        @Override
        protected void onPostExecute(Long totalSize) {
            File file = new File("/sdcard/about.txt");

            Log.d(TAG, "File : " + file);
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                int countLine = 0;

                while ((line = br.readLine()) != null) {
                    countLine++;
                    if (countLine == 1) {
                        text = line.replaceAll("<[^>]*>", "");
                        if (text.equals("")) {
                            countLine = 0;
                        }

                    }
                    Log.d(TAG, line.replaceAll("<[^>]*>", ""));
                }
                br.close();
                Toast.makeText(MainActivity.this, "Download Completed", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.d(TAG, "Error occured.... " + e);
            }

        }

    }

}


