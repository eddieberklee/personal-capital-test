package com.compscieddy.personalcapitaltest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by elee on 6/8/17.
 * https://developer.android.com/training/basics/network-ops/connecting.html#download
 */

public class ImageDownloadTask extends AsyncTask<String, Integer, Bitmap> {

  private static final String TAG = ImageDownloadTask.class.getSimpleName();
  private Context mContext;
  private ImageDownloadCallback mCallback;

  public ImageDownloadTask(Context context, ImageDownloadCallback callback) {
    super();
    mContext = context;
    mCallback = callback;
  }

  @Override
  protected void onPreExecute() {
    if (!Util.isNetworkConnected(mContext)) {
      mCallback.onImageDownloadCallback(null);
      cancel(true);
    }
  }

  @Override
  protected Bitmap doInBackground(String... urls) {
    Bitmap bitmap = null;

    if (!isCancelled() && urls != null && urls.length > 0) {
      String urlString = urls[0];
      try {
        if (urlString != null) {
          bitmap = parseBitmap(urlString);
        }
      } catch (Exception e) {
        Log.e(TAG, "Error downloading image from url: " + urlString);
        e.printStackTrace();
      }
    }

    return bitmap;
  }

  @Override
  protected void onPostExecute(Bitmap bitmap) {
    if (bitmap != null) {
      mCallback.onImageDownloadCallback(bitmap);
    }
  }

  private Bitmap parseBitmap(String urlString) {

    Bitmap bitmap = null;
    InputStream stream = null;

    try {
      stream = new URL(urlString).openConnection().getInputStream();
      bitmap = BitmapFactory.decodeStream(stream);
    } catch (Exception e) {
      Log.e(TAG, " urlString: " + urlString);
      throw new RuntimeException(e);
    } finally {
      if (stream != null) {
        try {
          stream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    return bitmap;
  }

}
