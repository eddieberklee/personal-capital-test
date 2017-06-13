package com.compscieddy.personalcapitaltest;

import android.support.annotation.Nullable;

/**
 * Created by elee on 6/8/17.
 */

public interface RssDownloadCallback {
  void onRssDownloadComplete(@Nullable RssFeed urlContent);
}
