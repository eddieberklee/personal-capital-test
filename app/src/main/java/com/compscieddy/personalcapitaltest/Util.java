package com.compscieddy.personalcapitaltest;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;

/**
 * Created by elee on 6/8/17.
 */

public class Util {

  public static int dpToPx(int dp) {
    return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
  }

  public static boolean isNetworkConnected(Context context) {
    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    return networkInfo != null && networkInfo.isConnected();
  }

  public static int getScreenWidth(Context context) {
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    int widthPixels = displayMetrics.widthPixels;
    return widthPixels;
  }

  public static int getScreenHeight(Context context) {
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    int heightPixels = displayMetrics.heightPixels;
    return heightPixels;
  }

}
