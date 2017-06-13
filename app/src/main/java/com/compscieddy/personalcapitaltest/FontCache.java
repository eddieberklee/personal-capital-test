package com.compscieddy.personalcapitaltest;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

/**
 * Created by elee on 1/7/16.
 * A class I use for custom fonts in my other projects.
 */
public class FontCache {

  public static final int AVENIR_NEXT_ULTRALIGHT = 1;
  public static final int AVENIR_NEXT_REGULAR = 3;
  public static final int AVENIR_NEXT_MEDIUM = 4;
  public static final int AVENIR_NEXT_DEMIBOLD = 5;
  public static final int AVENIR_NEXT_BOLD = 6;
  public static final int AVENIR_NEXT_HEAVY = 7;

  private static HashMap<Integer, Typeface> fontCache = new HashMap<>();

  public static Typeface get(Context context, int id) {
    Typeface tf = fontCache.get(id);
    if (tf == null) {
      String path = "";
      switch (id) {
        case AVENIR_NEXT_ULTRALIGHT:
          path = "AvenirNext-UltraLight.otf"; break;
        case AVENIR_NEXT_REGULAR:
          path = "AvenirNext-Regular.otf"; break;
        case AVENIR_NEXT_MEDIUM:
          path = "AvenirNext-Medium.otf"; break;
        case AVENIR_NEXT_DEMIBOLD:
          path = "AvenirNext-DemiBold.otf"; break;
        case AVENIR_NEXT_BOLD:
          path = "AvenirNext-Bold.otf"; break;
        case AVENIR_NEXT_HEAVY:
          path = "AvenirNext-Heavy.otf"; break;
        case -1:
          // default font would have to be
          path = "AvenirNext-Regular.otf";
          break;
      }
      tf = Typeface.createFromAsset(context.getAssets(), path);
      fontCache.put(id, tf);
    }
    return tf;
  }
}
