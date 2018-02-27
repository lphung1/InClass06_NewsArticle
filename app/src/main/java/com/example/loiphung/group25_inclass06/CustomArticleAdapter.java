package com.example.loiphung.group25_inclass06;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by LoiPhung on 2/26/18.
 */

public class CustomArticleAdapter extends ArrayAdapter<Article> {

    public CustomArticleAdapter(Context context, int resource, ArrayList<Article> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Article article = this.getItem(position);
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.article_row, parent, false);
        TextView titleName = convertView.findViewById(R.id.articleTitleText);
        TextView sourceName = convertView.findViewById(R.id.articleTitleDescription_textview);
        ImageView imageView = convertView.findViewById(R.id.article_image_view);


        sourceName.setText(article.toString());
        titleName.setText(article.getTitle());
        new ImageDownloaderTask(imageView).execute(article.getUrlToImage());
        Log.d("Picture URL", article.getUrlToImage());



        return convertView;
    }


    public static Bitmap getBitmapFromURL(String src) {

        StringBuilder stringBuilder = new StringBuilder();
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String result = null;
        Bitmap image = null;
        try{
            URL url = new URL(src);
            connection = (HttpURLConnection)url.openConnection();
            connection.connect();
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                image = BitmapFactory.decodeStream(connection.getInputStream());
            }
        }
        catch(MalformedURLException e){
            e.printStackTrace();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (connection == null){
                connection.disconnect();
            }
            if (reader != null){
                try{
                    reader.close();
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }

        }

        return image;
    }



    class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {

        private final WeakReference<ImageView> imageViewReference;

        public ImageDownloaderTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadBitmap(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    } else {

                        Drawable placeholder = null;
                        imageView.setImageDrawable(placeholder);
                    }
                }
            }
        }

        private Bitmap downloadBitmap(String url) {
            HttpURLConnection urlConnection = null;
            try {
                URL uri = new URL(url);
                urlConnection = (HttpURLConnection) uri.openConnection();

                final int responseCode = urlConnection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }
            } catch (Exception e) {
                urlConnection.disconnect();
                Log.w("ImageDownloader", "Error Downloading Image " + url);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
    }
}
