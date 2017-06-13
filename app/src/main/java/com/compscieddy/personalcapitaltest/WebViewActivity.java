package com.compscieddy.personalcapitaltest;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by elee on 6/12/17.
 */

public class WebViewActivity extends AppCompatActivity {

  private static final String TAG = WebViewActivity.class.getSimpleName();

  public static final String EXTRA_URL = "extra_url";
  public static final String EXTRA_TITLE_HTML = "extra_title_html";

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Resources resources = getResources();

    if (getIntent() == null || getIntent().getExtras() == null) {
      Log.e(TAG, "This activity needs a url passed in.");
      return;
    }

    Bundle extras = getIntent().getExtras();
    String url = extras.getString(EXTRA_URL);
    String titleHtml = extras.getString(EXTRA_TITLE_HTML);

    LinearLayout rootView = new LinearLayout(this);
    rootView.setOrientation(LinearLayout.VERTICAL);

    initViews(resources, url, titleHtml, rootView);

    setContentView(rootView);
  }

  private void initViews(Resources resources, String url, String titleHtml, LinearLayout rootView) {

    final ProgressDialog progressDialog = ProgressDialog.show(this, "", "Loading...",true);

    TextView titleTextView = createTitleTextView(resources, titleHtml);

    WebView webView = new WebView(this);
    webView.loadUrl(url);
    webView.setWebViewClient(new WebViewClient() {
      @Override
      public void onPageFinished(WebView view, String url) {
        if (progressDialog != null && progressDialog.isShowing()) {
          progressDialog.dismiss();
        }
      }
    });
    webView.getSettings().setJavaScriptEnabled(true);
    webView.getSettings().setSupportZoom(true);
    webView.getSettings().setBuiltInZoomControls(true);

    rootView.addView(titleTextView);
    rootView.addView(webView);
  }

  private TextView createTitleTextView(Resources resources, String titleHtml) {
    TextView titleTextView = new TextView(this);
    titleTextView.setText(Html.fromHtml(titleHtml));
    titleTextView.setTextSize(13);
    titleTextView.setTextColor(resources.getColor(R.color.white));
    titleTextView.setMaxLines(1);
    titleTextView.setEllipsize(TextUtils.TruncateAt.END);
    titleTextView.setBackgroundColor(resources.getColor(R.color.black));
    titleTextView.setTypeface(FontCache.get(this, FontCache.AVENIR_NEXT_DEMIBOLD));

    int paddingHoriz = Util.dpToPx(12);
    int paddingVert = Util.dpToPx(5);
    titleTextView.setPadding(paddingHoriz, paddingVert, paddingHoriz, paddingVert);

    return titleTextView;
  }


}
