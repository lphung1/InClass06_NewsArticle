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
import java.util.List;

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
        final ListView listView = findViewById(R.id.ListView);
        final ProgressBar pb = findViewById(R.id.progressBar);

        CustomArticleAdapter adapter = new CustomArticleAdapter(MainActivity.this, R.layout.article_row, articlesArrayList);
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
                            new GetDataAsync(MainActivity.this,listView, pb).execute("https://newsapi.org/v2/top-headlines?country=us&category=" + category + "&apiKey=b2c985005ade4ecdab27c1abace702a8");
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


