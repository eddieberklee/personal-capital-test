package com.compscieddy.personalcapitaltest;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

/**
 * Created by elee on 6/8/17.
 */

public interface ImageDownloadCallback {
  void onImageDownloadCallback(@Nullable Bitmap bitmap);
}
