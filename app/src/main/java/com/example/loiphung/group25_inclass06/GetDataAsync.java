package com.example.loiphung.group25_inclass06;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by LoiPhung on 2/26/18.
 */

public class GetDataAsync extends AsyncTask<String, Integer, ArrayList<Article>> {

    private ProgressDialog dialog;
    AlertDialog alert;
    AlertDialog.Builder builder;
    ProgressBar pb;
    ListView listview;
    Context context;

    public GetDataAsync(MainActivity activity, ListView listView, ProgressBar pb) {
        dialog = new ProgressDialog(activity);
        //alert = new AlertDialog(activity);
        pb.setProgress(0);
        pb.setMax(100);
        this.pb = pb;
        this.listview = listView;
        context = activity;

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

        MainActivity.articlesArrayList = result;
        CustomArticleAdapter adapter = new CustomArticleAdapter(context, R.layout.article_row, result);
        listview.setAdapter(adapter);


        Log.d("result", ""+ result);
        Log.d("articleArrayList", " "+ MainActivity.articlesArrayList);





    }
}