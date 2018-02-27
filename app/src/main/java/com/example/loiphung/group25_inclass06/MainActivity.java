package com.example.loiphung.group25_inclass06;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.transform.Source;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<Article> articlesArrayList = new ArrayList<Article>();
    public static String [] categories= new String [] {"Business", ""};
    String category;
    public static AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        findViewById(R.id.go_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String categoriesArray[] = new String[] {"Business", "Entertainment", "General", "Health", "Sports", "Technology", "Science"};
                builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Select from the list");
                builder.setItems(categoriesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        category = categoriesArray[which];
                        Log.d("category", category);
                        if(isConnected()){
                            new GetDataAsync(MainActivity.this).execute("https://newsapi.org/v2/top-headlines?country=us&category=" + category + "&apiKey=b2c985005ade4ecdab27c1abace702a8");
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Not connected to Internet", Toast.LENGTH_SHORT).show();
                        }
                    } //dialog on click
                });
                builder.show();

            } //go button on click
        });







    } //end on create


    private class GetDataAsync extends AsyncTask<String, Integer, ArrayList<Article>> {

        private ProgressDialog dialog;
        AlertDialog alert;
        AlertDialog.Builder builder;
        ProgressBar pb;

        public GetDataAsync(MainActivity activity) {
            dialog = new ProgressDialog(activity);
            //alert = new AlertDialog(activity);
            pb = findViewById(R.id.progressBar);
            pb.setProgress(0);
            pb.setMax(100);

        }

        @Override
        protected void onPreExecute() {
            //dialog.setMessage("Loading sources");
            //dialog.show();



        }


        @Override
        protected ArrayList<Article> doInBackground(String... params) {
            HttpURLConnection connection = null;
            ArrayList<Article> result = new ArrayList<Article>();
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");


                    JSONObject root = new JSONObject(json);

                    JSONArray articles = root.optJSONArray("articles");

                    for (int i = 0; i < articles.length(); i++) {
                        JSONObject articlesJSONObject = articles.optJSONObject(i);
                        JSONObject sourceJsonObject = articlesJSONObject.optJSONObject("source");

                        Article a = new Article();

                        a.setDescription(articlesJSONObject.getString("description"));
                        a.setUrl(articlesJSONObject.optString("url"));
                        a.setTitle(articlesJSONObject.optString("title"));
                        a.setAuthor(articlesJSONObject.optString("author"));
                        if(articlesJSONObject.optString("urlToImage").startsWith("https"))
                        {
                            a.setUrlToImage(articlesJSONObject.optString("urlToImage"));
                        }
                        a.setDate(articlesJSONObject.optString("publishedAt"));
                        Log.d("JsonName", "" + sourceJsonObject.getString("name"));
                        Log.d("Jsonid", "" + sourceJsonObject.getString("id"));


                        result.add(a);
                        pb.setProgress(pb.getProgress() + 1);
                        publishProgress(pb.getProgress());
                    }
                }
            } catch (Exception e) {
                //Handle Exceptions
            } finally {
                //Close the connections
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            pb.setProgress(values[0]);


        }

        protected void onPostExecute(ArrayList<Article> result) {

            if (pb.getProgress() != pb.getMax()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pb.setProgress(pb.getMax());
            }


            final ArrayList<Article> thisSource = result;
            MainActivity.articlesArrayList = result;


            Log.d("news Array List", ""+ result);

            final ListView listView = findViewById(R.id.ListView);
            CustomArticleAdapter adapter = new CustomArticleAdapter(MainActivity.this, R.layout.article_row, result);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String url = MainActivity.articlesArrayList.get(position).getUrl();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });



        }
    }



    private boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }


}//end main activity


