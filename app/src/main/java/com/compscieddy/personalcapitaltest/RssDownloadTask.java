package com.compscieddy.personalcapitaltest;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by elee on 6/8/17.
 * https://developer.android.com/training/basics/network-ops/connecting.html#download
 * http://www.vogella.com/tutorials/AndroidXML/article.html
 */

public class RssDownloadTask extends AsyncTask<String, Integer, RssFeed> {

  private static final String TAG = RssDownloadTask.class.getSimpleName();
  private Context mContext;
  private RssDownloadCallback mCallback;

  public RssDownloadTask(Context context, RssDownloadCallback callback) {
    super();
    mContext = context;
    mCallback = callback;
  }

  @Override
  protected void onPreExecute() {
    if (!Util.isNetworkConnected(mContext)) {
      mCallback.onRssDownloadComplete(null);
      cancel(true);
    }
  }

  @Override
  protected RssFeed doInBackground(String... urls) {
    RssFeed rssFeed = null;

    if (!isCancelled() && urls != null && urls.length > 0) {
      String urlString = urls[0];
      try {
        rssFeed = parseRssFeed(urlString);
      } catch (Exception e) {
        e.printStackTrace();
        rssFeed.error = e.toString();
      }
    }

    return rssFeed;
  }

  @Override
  protected void onPostExecute(RssFeed rssFeed) {
    if (rssFeed != null) {
      mCallback.onRssDownloadComplete(rssFeed);
    }
  }

  private RssFeed parseRssFeed(String urlString) {

    List<RssItem> rssItems = new ArrayList<>();
    XmlPullParser parser = Xml.newPullParser();
    InputStream stream = null;
    RssFeed rssFeed = new RssFeed();

    try {
      stream = new URL(urlString).openConnection().getInputStream();
      parser.setInput(stream, null);
      int eventType = parser.getEventType();
      boolean finished = false;
      RssItem item = null;

      while (eventType != XmlPullParser.END_DOCUMENT && !finished) {
        String name = null;
        String namespace = null;
        boolean namespaceIncludesMedia = false;
        for (int i = 0; i < parser.getNamespaceCount(parser.getDepth()); i++) {
          if (parser.getNamespacePrefix(i).equalsIgnoreCase("media")) {
            namespaceIncludesMedia = true;
          }
        }

        switch (eventType) {
          case XmlPullParser.START_DOCUMENT:
            // no-op
            break;
          case XmlPullParser.START_TAG:
            name = parser.getName();
            namespace = parser.getNamespace();

            if (item == null && name.equalsIgnoreCase("title")) {
              // we've found the title of the rss feed and not for a specific item/article
              String rssTitle = parser.nextText().trim();
              rssFeed.mainTitleHtml = rssTitle;
            }

            if (name.equalsIgnoreCase("item")) {
              Log.i("New item", "Create new item");
              item = new RssItem();
            } else if (item != null) {
              if (name.equalsIgnoreCase("title")) {
                Log.i("Attribute", "title");
                item.titleHtml = parser.nextText().trim();
              } else if (name.equalsIgnoreCase("description")) {
                Log.i("Attribute", "description");
                item.descriptionHtml = parser.nextText().trim();
              } else if (name.equalsIgnoreCase("link")) {
                Log.i("Attribute", "link");
                item.linkUrl = parser.nextText().trim();
              } else if (name.equalsIgnoreCase("pubDate")) {
                Log.i("Attribute", "pubDate");
                Date date = new Date(parser.nextText().trim());
                item.publishDate = date;
              } else if (name.equalsIgnoreCase("content") && namespaceIncludesMedia) {
                Log.i("Attribute", "media:content");
                String type = parser.getAttributeValue(null, "type");
                if (TextUtils.equals(type, "image/jpeg")
                    || TextUtils.equals(type, "image/png")) {
                  item.imageUrl = parser.getAttributeValue(null, "url");
                  item.imageWidth = Integer.parseInt(parser.getAttributeValue(null, "width"));
                  item.imageHeight = Integer.parseInt(parser.getAttributeValue(null, "height"));
                }
              }
            }
            break;
          case XmlPullParser.END_TAG:
            name = parser.getName();
            Log.i("End tag", name);
            if (name.equalsIgnoreCase("item") && item != null) {
              Log.i("Added", item.toString());
              rssItems.add(item);
              item = null;
            } else if (name.equalsIgnoreCase("channel")) {
              finished = true;
            }
            break;
        }
        eventType = parser.next();
      }

    } catch (Exception e) {
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

    rssFeed.rssItems = rssItems;
    return rssFeed;
  }

}
