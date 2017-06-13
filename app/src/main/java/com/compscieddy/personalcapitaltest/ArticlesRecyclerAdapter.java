package com.compscieddy.personalcapitaltest;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by elee on 6/10/17.
 */

public class ArticlesRecyclerAdapter extends RecyclerView.Adapter {

  private List<Article> mArticles;
  private Context mContext;
  private RecyclerView mArticlesRecyclerView;
  private final Resources mResources;

  public ArticlesRecyclerAdapter(Context context, List<RssItem> rssItems, RecyclerView articlesRecyclerView) {
    super();
    mContext = context;
    mArticlesRecyclerView = articlesRecyclerView;
    mResources = mContext.getResources();
    mArticles = new ArrayList<>();
    for (RssItem rssItem : rssItems) {
      mArticles.add(new Article(rssItem));
    }

    /** I considered calculating an inSampleSize to downsize the images we're loading
     * but decided to assume that the images would be reasonable sizes
     * (seems to range from 600-800px). If I had time would implement.
     */
    // int inSampleSize = calculateInSampleSize();
  }

  /**
   * This is interesting - I've never implemented a RecyclerView without
   * a layout. I'm wondering if this is the best way to use the ViewHolder
   * with views created in code. I guess the good thing about views written
   * in code is being able to avoid the costly findViewById() which is the
   * reason ViewHolder as a pattern exists...
   */

  private class ArticleViewHolder extends RecyclerView.ViewHolder {
    public TextView titleText;
    public TextView descriptionText;
    public ImageView image;
    public LinearLayout textContainer;
    public View loadingImageProgress;
    public ObjectAnimator loadingImageProgressAnimator;

    public ArticleViewHolder(View itemView) {
      super(itemView);
    }
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    FrameLayout itemView = new FrameLayout(context);
    ArticleViewHolder holder = new ArticleViewHolder(itemView);

    holder.titleText = createTitleTextView(context);
    holder.descriptionText = createDescriptionTextView(context);
    holder.image = createImageView(context);
    holder.textContainer = createTextContainer(context);
    holder.loadingImageProgress = createLoadingImageProgressView(context);

    holder.loadingImageProgressAnimator = ObjectAnimator.ofFloat(holder.loadingImageProgress, "rotation", 0, 360);
    holder.loadingImageProgressAnimator.setRepeatCount(ValueAnimator.INFINITE);
    holder.loadingImageProgressAnimator.setDuration(500);
    holder.loadingImageProgressAnimator.start();

    LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    textParams.gravity = Gravity.CENTER_HORIZONTAL;
    holder.textContainer.addView(holder.titleText, textParams);
    holder.textContainer.addView(holder.descriptionText, textParams);

    itemView.addView(holder.image);
    itemView.addView(holder.textContainer);
    itemView.addView(holder.loadingImageProgress);

    itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        int position = mArticlesRecyclerView.getChildLayoutPosition(v);
        Article article = mArticles.get(position);
        String url = article.rssItem.linkUrl + "?displayMobileNavigation=0";
        String titleHtml = article.rssItem.titleHtml;
        Intent webIntent = new Intent(mContext, WebViewActivity.class);
        webIntent.putExtra(WebViewActivity.EXTRA_URL, url);
        webIntent.putExtra(WebViewActivity.EXTRA_TITLE_HTML, titleHtml);
        webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(webIntent);
      }
    });

    return holder;
  }

  private TextView createTitleTextView(Context context) {
    Resources resources = context.getResources();

    TextView titleText = new TextView(context);
    titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.article_title_size)); // this could have just been a programmatic constant
    titleText.setTextColor(resources.getColor(R.color.article_text));
    titleText.setTypeface(FontCache.get(context, FontCache.AVENIR_NEXT_DEMIBOLD));
    titleText.setEllipsize(TextUtils.TruncateAt.END);
    titleText.setLineSpacing(0, 0.9f);
    titleText.setShadowLayer(Util.dpToPx(2), 0, Util.dpToPx(1), resources.getColor(R.color.article_text_shadow));
    titleText.setMaxWidth(Util.dpToPx(400));

    return titleText;
  }

  private TextView createDescriptionTextView(Context context) {
    Resources resources = context.getResources();

    TextView descriptionText = new TextView(context);
    descriptionText.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.article_description_size));
    descriptionText.setTextColor(resources.getColor(R.color.article_text));
    descriptionText.setTypeface(FontCache.get(context, FontCache.AVENIR_NEXT_REGULAR));
    descriptionText.setEllipsize(TextUtils.TruncateAt.END);
    descriptionText.setMaxWidth(Util.dpToPx(500));

    return descriptionText;
  }

  private ImageView createImageView(Context context) {
    ImageView imageView = new ImageView(context);
    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    return imageView;
  }

  private LinearLayout createTextContainer(Context context) {
    Resources resources = context.getResources();

    int startColor = resources.getColor(R.color.article_text_background_shadow_start);
    int endColor = resources.getColor(R.color.article_text_background_shadow_end);

    LinearLayout textContainer = new LinearLayout(context);
    textContainer.setOrientation(LinearLayout.VERTICAL);
    GradientDrawable shadowDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
        new int[] { startColor, endColor });
    textContainer.setBackground(shadowDrawable);

    int paddingSides = mResources.getDimensionPixelSize(R.dimen.title_padding_sides);
    int paddingTop = Util.dpToPx(16);
    int paddingBottom = mResources.getDimensionPixelSize(R.dimen.title_padding_bottom);
    textContainer.setPadding(paddingSides, paddingTop, paddingSides, paddingBottom);

    FrameLayout.LayoutParams textContainerParams = new FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
    textContainer.setLayoutParams(textContainerParams);

    return textContainer;
  }

  private View createLoadingImageProgressView(Context context) {
    ImageView loadingImage = new ImageView(context);
    loadingImage.setImageDrawable(context.getResources().getDrawable(R.drawable.loading_arc));

    int loadingImageSize = Util.dpToPx(20);
    FrameLayout.LayoutParams loadingParams = new FrameLayout.LayoutParams(
        loadingImageSize, loadingImageSize, Gravity.CENTER);

    loadingImage.setLayoutParams(loadingParams);
    return loadingImage;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

    Article article = mArticles.get(position);
    RssItem rssItem = article.rssItem;
    final ArticleViewHolder holder = (ArticleViewHolder) viewHolder;

    int itemViewHeight;
    if (position == 0) {
      itemViewHeight = Util.dpToPx(200);
    } else {
      itemViewHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    StaggeredGridLayoutManager.LayoutParams holderParams = new StaggeredGridLayoutManager.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, itemViewHeight);
    int itemMargin = mResources.getDimensionPixelSize(R.dimen.article_item_margin);
    holderParams.setMargins(itemMargin, itemMargin, itemMargin, itemMargin);

    if (position == 0) { // Visual emphasis for the first item
      setFirstArticleStyling(holder, holderParams);
    } else {
      setOtherArticlesStyling(holder, holderParams);
    }

    holder.itemView.setLayoutParams(holderParams);

    holder.titleText.setText(Html.fromHtml(rssItem.titleHtml));
    holder.descriptionText.setText(Html.fromHtml(rssItem.descriptionHtml));

    String imageUrl = rssItem.imageUrl;

    Bitmap bitmap = article.bitmap;
    holder.image.setImageBitmap(bitmap);
    if (bitmap == null) {
      showImageLoading(holder);
      loadImage(position, holder, imageUrl);
    } else {
      hideImageLoading(holder);
    }
  }

  private void loadImage(final int position, final ArticleViewHolder holder, String imageUrl) {
    holder.itemView.setHasTransientState(true);
    // This seems dangerous to do, callbacks in recyclerview is definitely something I need better guidance on in terms of best practice
    ImageDownloadCallback imageDownloadCallback = new ImageDownloadCallback() {
      @Override
      public void onImageDownloadCallback(@Nullable Bitmap bitmap) {
        holder.itemView.setHasTransientState(false);
        mArticles.get(position).bitmap = bitmap;
        notifyItemChanged(position);
      }
    };
    ImageDownloadTask imageDownloadTask = new ImageDownloadTask(mContext, imageDownloadCallback);
    imageDownloadTask.execute(imageUrl);
  }

  private void showImageLoading(ArticleViewHolder holder) {
    holder.loadingImageProgress.setVisibility(View.VISIBLE);
    holder.loadingImageProgress.animate()
        .alpha(1.0f);
    holder.loadingImageProgressAnimator.start();
  }

  private void hideImageLoading(final ArticleViewHolder holder) {
    holder.loadingImageProgressAnimator.cancel();
    holder.loadingImageProgress.animate()
        .alpha(0)
        .withEndAction(new Runnable() {
          @Override
          public void run() {
            holder.loadingImageProgress.setVisibility(View.INVISIBLE);
          }
        });
  }

  private void setOtherArticlesStyling(ArticleViewHolder holder, StaggeredGridLayoutManager.LayoutParams holderParams) {
    holderParams.setFullSpan(false);
    holder.titleText.setMaxLines(2);
    holder.descriptionText.setVisibility(View.GONE);
  }

  private void setFirstArticleStyling(ArticleViewHolder holder, StaggeredGridLayoutManager.LayoutParams holderParams) {
    holderParams.setFullSpan(true);
    holder.titleText.setMaxLines(1);
    holder.descriptionText.setVisibility(View.VISIBLE);
    holder.descriptionText.setMaxLines(2);
  }

  @Override
  public int getItemCount() {
    return mArticles.size();
  }

  public int getNumColumns() {
    boolean isTablet = mResources.getBoolean(R.bool.is_tablet);
    return isTablet ? 3 : 2;
  }

}
