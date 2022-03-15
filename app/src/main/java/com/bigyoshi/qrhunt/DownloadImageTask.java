package com.bigyoshi.qrhunt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private final static String TAG = DownloadImageTask.class.getSimpleName();
    private ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urlToDownload = urls[0];
        Bitmap bm = null;
        try {
            InputStream in = new java.net.URL(urlToDownload).openStream();
            bm = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return bm;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}