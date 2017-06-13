package com.compscieddy.personalcapitaltest;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();

  private static final String FEED_URL = "https://www.personalcapital.com/blog/feed/?cat=3%2C891%2C890%2C68%2C284";

  private Resources mResources;
  private ImageView mRefreshButton;
  private ObjectAnimator mRefreshAnimator;
  private TextView mTitleText;
  private ViewGroup mRootView;
  private RecyclerView mArticlesRecyclerView;
  private ArticlesRecyclerAdapter mArticlesRecyclerAdapter;
  private List<RssItem> mRssItems;
  private FrameLayout mTopSection;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mResources = getResources();

    LinearLayout rootView = new LinearLayout(MainActivity.this);
    rootView.setOrientation(LinearLayout.VERTICAL);

    initViews(rootView);
    setContentView(rootView);

    queryRSSFeed(); // query for the first time
  }

  private void initViews(ViewGroup rootView) {

    /** I've never structured a *whole* app with views in code,
     * this is the best way I could think to organize it. Beyond using
     * a library like Anko for Kotlin.
     */

    mRootView = rootView;

    mTopSection = new FrameLayout(this);
    addTitleText();
    addRefreshButton();

    mRootView.addView(mTopSection);
    addArticlesRecyclerView();

    int itemMargin = mResources.getDimensionPixelSize(R.dimen.article_item_margin);
    mArticlesRecyclerView.setPadding(itemMargin, itemMargin, itemMargin, itemMargin);

  }

  private void addTitleText() {
    mTitleText = new TextView(this);
    mTitleText.setTextSize(19);
    mTitleText.setTextColor(mResources.getColor(R.color.main_title));
    mTitleText.setTypeface(FontCache.get(this, FontCache.AVENIR_NEXT_DEMIBOLD));
    mTitleText.setId(View.generateViewId());

    FrameLayout.LayoutParams titleParams = new FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
    mTopSection.addView(mTitleText, titleParams);
  }

  private void addRefreshButton() {
    int refreshImageSize = Util.dpToPx(32);
    int refreshImageMargin = Util.dpToPx(10);

    mRefreshButton = new ImageView(this);
    mRefreshButton.setImageDrawable(mResources.getDrawable(R.drawable.ic_refresh));

    FrameLayout.LayoutParams refreshImageParams = new FrameLayout.LayoutParams(refreshImageSize, refreshImageSize);
    refreshImageParams.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
    refreshImageParams.setMargins(refreshImageMargin, refreshImageMargin, refreshImageMargin, refreshImageMargin);
    mRefreshButton.setLayoutParams(refreshImageParams);

    mRefreshButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        queryRSSFeed();
      }
    });

    mTopSection.addView(mRefreshButton);
  }

  private void addArticlesRecyclerView() {

    RelativeLayout.LayoutParams articlesRecyclerViewParams = new RelativeLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    articlesRecyclerViewParams.addRule(RelativeLayout.BELOW, mTitleText.getId());

    mArticlesRecyclerView = new RecyclerView(this);
    mRootView.addView(mArticlesRecyclerView, articlesRecyclerViewParams);

  }

  private void startLoadingAnimation() {
    mRefreshAnimator = ObjectAnimator.ofFloat(mRefreshButton, "rotation", 0, 360);
    mRefreshAnimator.setRepeatCount(ValueAnimator.INFINITE);
    mRefreshAnimator.setDuration(1000);
    mRefreshAnimator.start();
  }

  private void stopLoadingAnimation() {
    mRefreshAnimator.cancel();
    // And rotate back to initial value
    mRefreshButton.animate()
        .rotation(0);
  }

  private void queryRSSFeed() {

    startLoadingAnimation();
    mRefreshButton.setEnabled(false);

    if (!Util.isNetworkConnected(this)) {
      Log.e(TAG, "Network not connected so can't query RSS feed.");
      return;
    }

    RssDownloadCallback rssDownloadCallback = new RssDownloadCallback() {
      @Override
      public void onRssDownloadComplete(@Nullable RssFeed rssFeed) {
        stopLoadingAnimation();
        mRefreshButton.setEnabled(true);
        if (rssFeed != null) {
          initRssFeed(rssFeed);
        }
      }
    };
    RssDownloadTask rssDownloadTask = new RssDownloadTask(this, rssDownloadCallback);
    rssDownloadTask.execute(FEED_URL);

  }

  private void initRssFeed(RssFeed rssFeed) {

    mTitleText.setText(Html.fromHtml(rssFeed.mainTitleHtml));

    mRssItems = rssFeed.rssItems;

    if (mArticlesRecyclerAdapter == null) {
      mArticlesRecyclerAdapter = new ArticlesRecyclerAdapter(this, mRssItems, mArticlesRecyclerView);
      mArticlesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(mArticlesRecyclerAdapter.getNumColumns(), LinearLayoutManager.VERTICAL));
      mArticlesRecyclerView.setAdapter(mArticlesRecyclerAdapter);
    } else {
      mArticlesRecyclerAdapter.notifyDataSetChanged();
    }
  }

}
