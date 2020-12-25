package com.timbuchalka.top10downloader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView listView;
    private String feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
    private int feedLimit=10;
    private String check = "invalidation";
    private static final String Save_URL="abc";
    private static final String Save_limit="10";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null)
        {
            feedURL=savedInstanceState.getString(Save_URL);
            feedLimit=savedInstanceState.getInt(Save_limit);
        }
        setContentView(R.layout.activity_main);
        listView=(ListView) findViewById(R.id.XmlListView);
        downloadURl(String.format(feedURL,feedLimit));
        Log.d(TAG, "onCreate: done");

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feed_menu,menu);
        if(feedLimit == 10) {
            menu.findItem(R.id.mnuTop10).setChecked(true);
        } else {
            menu.findItem(R.id.mnuTop25).setChecked(true);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.mnuFree:
                feedURL="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.mnuPaid:
                feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.mnuSongs:
                feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.mnuTop10:
            case R.id.mnuTop25:
                if(!item.isChecked())
                {
                    item.setChecked(true);
                    feedLimit = 35 - feedLimit ;
                    downloadURl(String.format(feedURL,feedLimit));
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " setting feedLimit to " + feedLimit);
                }else{
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " feedLimit unchanged");
                }
                break;
            case R.id.mnuRefresh:
                check = "invalidation";
                break;
            default: return super.onOptionsItemSelected(item);
        }
        downloadURl(String.format(feedURL,feedLimit));
        return true;
    }
    private void downloadURl(String s)
    {
        if(!s.equalsIgnoreCase(check)) {
            Log.d(TAG, "downloadURl: starting Asynctask");
            DownloadData downloadData = new DownloadData();
            downloadData.execute(s);
            Log.d(TAG, "downloadURl: downloaded completed");
            check = s;
        }else
        {
            Log.d(TAG, "downloadURl: url does not change");
        }
    }

    private class DownloadData extends AsyncTask<String, Void, String> {
        private static final String TAG = "DownloadData";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: parameter is " + s);
            ParseApplications parseApplications = new ParseApplications();
            parseApplications.parse(s);
//            ArrayAdapter<FeedEntry> arrayAdapter=new ArrayAdapter<FeedEntry>(MainActivity.this,R.layout.list_item,parseApplications.getApplications());
//            listView.setAdapter(arrayAdapter);
            FeedAdaptor<FeedEntry> feedAdaptor = new FeedAdaptor<>(MainActivity.this,R.layout.list_record,parseApplications.getApplications());
            listView.setAdapter(feedAdaptor);
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: starts with " + strings[0]);
            String rssFeed = downloadXML(strings[0]);
            if(rssFeed == null) {
                Log.e(TAG, "doInBackground: Error downloading");
            }
            return rssFeed;
        }

        private String downloadXML(String urlPath) {
            StringBuilder xmlResult = new StringBuilder();

            try {
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(TAG, "downloadXML: The response code was " + response);
//                InputStream inputStream = connection.getInputStream();
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                BufferedReader reader = new BufferedReader(inputStreamReader);
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                int charsRead;
                char[] inputBuffer = new char[500];
                while(true) {
                    charsRead = reader.read(inputBuffer);
                    if(charsRead < 0) {
                        break;
                    }
                    if(charsRead > 0) {
                        xmlResult.append(String.copyValueOf(inputBuffer, 0, charsRead));
                    }
                }
                reader.close();
                return xmlResult.toString();
            } catch(MalformedURLException e) {
                Log.e(TAG, "downloadXML: Invalid URL " + e.getMessage());
            } catch(IOException e) {
                Log.e(TAG, "downloadXML: IO Exception reading data: " + e.getMessage());
            } catch(SecurityException e) {
                Log.e(TAG, "downloadXML: Security Exception.  Needs permisson? " + e.getMessage());
//                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
      outState.putString(Save_URL,feedURL);
      outState.putInt(Save_limit,feedLimit);
        super.onSaveInstanceState(outState);
    }
}


















