package com.example.linanw.newsie;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.support.v4.content.ContextCompat.startActivity;

public class NewsListViewAdapter extends RecyclerView.Adapter <NewsListViewAdapter.ViewHolder> {
    private ArrayList<JSONArray> _newsFeedPages;
    private int _pageSize;
    private int _itemCount;
    private RecyclerView _recyclerView;
    //private NativeAdsManager _nativeAdsManager;
    private static ArrayList<LinearLayout> nativeAdBufferViewHolders = new ArrayList<>();

    private final static int AD_INTERVAL = 1;
    private final static int NEWS_ITEM = 0;
    private final static int AD_ITEM = 1;
    private final static int LOADING_ITEM = 2;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;
        ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public static class NewsViewHolder extends  NewsListViewAdapter.ViewHolder {
        View container;
        TextView text_view;// = holder.view.findViewById(R.id.text_view_title);
        TextView text_view_des;// = holder.view.findViewById(R.id.text_view_description);
        ImageView imageView;// = holder.view.findViewById(R.id.image_view);

        NewsViewHolder(View view) {
            super(view);
            View.OnClickListener onClickListener= new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(view.getTag(R.id.LINK_URL).toString()));
                    startActivity(view.getContext(), browserIntent, null);
                }
            };

            container = view;
            text_view = view.findViewById(R.id.native_ad_title);
            text_view_des = view.findViewById(R.id.text_view_description);
            imageView = view.findViewById(R.id.native_ad_media);
            view.setOnClickListener(onClickListener);
            imageView.setOnClickListener(onClickListener);
        }

        void bindView(JSONObject objectNews) {
            String title = "";
            String description = "";
            String imageUrl = "";
            String url = "";
            String publishAt = "";

            try {
                title = objectNews.getString("title");
                description = objectNews.getString("description");
                imageUrl = (objectNews.getString("urlToImage"));
                url = (objectNews.getString("url"));
                publishAt = (objectNews.getString("publishedAt"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(description.equals("null")) description = "";
            if(title.equals("null")) title = description;
            if(title.equals("null")) title = "The article is not available anymore";
            text_view.setText(title);//+ " i:" + index+ " of:" + offset + " p:" + page);
            text_view_des.setText(description);
            view.setTag(R.id.LINK_URL, url);
            if (imageUrl!=null &&
                !imageUrl.equals("") &&
                (imageUrl.startsWith("http") || (imageUrl.startsWith("//"))
                )) //linanw learning: invalid url generate exceptions.
            {
                int widthDp = 150;
                int heightDp = 90;
                if(imageUrl.startsWith("//")) imageUrl = "http:" + imageUrl;
                imageView.setMinimumWidth(dpToPx(widthDp)); //linanw to-do: move to string.xml
                imageView.setMinimumHeight(dpToPx(heightDp));
                imageView.setTag(R.id.LINK_URL, imageUrl);
                Glide
                        .with(imageView.getContext())
                        .load(imageUrl)
                        .apply(RequestOptions.overrideOf(dpToPx(widthDp), dpToPx(heightDp)).centerCrop())
                        .into(imageView);
            }
            else
            {
                assert imageUrl != null;
                if(imageUrl.startsWith("/") && !imageUrl.startsWith("//")) Toast.makeText(imageView.getContext(), "url: /", Toast.LENGTH_SHORT).show();
                imageView.setImageDrawable(null);
                imageView.setMinimumWidth(0);
                imageView.setMinimumHeight(0);
                imageView.setTag(R.id.LINK_URL, "http://bing.com");
            }
        }
    }

    public static class AdViewHolder extends  NewsListViewAdapter.ViewHolder{
        ViewGroup _container;
//        Context _context;
//
//        // Create native UI using the ad metadata.
//        ImageView nativeAdIcon;
//        TextView nativeAdTitle;
//        MediaView nativeAdMedia;
//        TextView nativeAdSocialContext;
//        TextView nativeAdBody;
//        Button nativeAdCallToAction;
//        LinearLayout adChoicesContainer;

        AdViewHolder(View view) {
            super(view);
            _container = (ViewGroup) view;
//            _context = view.getContext();
//
//            // Add the Ad view into the ad container.
//            LayoutInflater inflater = LayoutInflater.from(_context);
//            // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
//            LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, _container, false);
//            _container.addView(adView);
//
//            // Create native UI using the ad metadata.
//            nativeAdIcon = (ImageView) adView.findViewById(R.id.native_ad_icon);
//            nativeAdTitle = (TextView) adView.findViewById(R.id.native_ad_title);
//            nativeAdMedia = (MediaView) adView.findViewById(R.id.native_ad_media);
//            nativeAdSocialContext = (TextView) adView.findViewById(R.id.native_ad_social_context);
//            nativeAdBody = (TextView) adView.findViewById(R.id.native_ad_body);
//            nativeAdCallToAction = (Button) adView.findViewById(R.id.native_ad_call_to_action);
//            adChoicesContainer = (LinearLayout) _container.findViewById(R.id.ad_choices_container);
//
//            if (_nativeAd != null)
//                _nativeAd.unregisterView(); //linanw learning: not unregisterView generation warnings.
//
//            _nativeAd = nativeAd;
//            // Set the Text.
//            nativeAdTitle.setText(_nativeAd.getAdTitle());
//            nativeAdSocialContext.setText(_nativeAd.getAdSocialContext());
//            nativeAdBody.setText(_nativeAd.getAdBody());
//            nativeAdCallToAction.setText(_nativeAd.getAdCallToAction());
//
//            // Register the Title and CTA button to listen for clicks.
//            List<View> clickableViews = new ArrayList<>(); //linanw to-do: stick to to current native_ad_layout
//            clickableViews.add(nativeAdTitle);
//            clickableViews.add(nativeAdCallToAction);
//            _nativeAd.registerViewForInteraction(_container, clickableViews);
//
//            // Download and display the ad icon.
//            NativeAd.Image adIcon = _nativeAd.getAdIcon();
//            NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);
//
//            // Download and display the cover image.
//            nativeAdMedia.setNativeAd(_nativeAd); //linanw to-do: havily in loading
//
//            // Add the AdChoices icon
//            AdChoicesView adChoicesView = new AdChoicesView(_context, _nativeAd, true);
//            adChoicesContainer.removeAllViews();
//            adChoicesContainer.addView(adChoicesView);
        }

        void bindView(int position) {
            if(NewsListViewAdapter.nativeAdBufferViewHolders.size()>0) {
                int index = position/(AD_INTERVAL+1)% nativeAdBufferViewHolders.size();
                LinearLayout nasvh = nativeAdBufferViewHolders.get(index);
                _container.removeAllViews();
                if(nasvh.getParent()!=null) ((ViewGroup)nasvh.getParent()).removeAllViews();
                _container.addView(nasvh);
            }
//            if (_nativeAd != null)
//                _nativeAd.unregisterView(); //linanw learning: not unregisterView generation warnings.
//
//            _nativeAd = nativeAd;
//            // Set the Text.
//            nativeAdTitle.setText(_nativeAd.getAdTitle());
//            nativeAdSocialContext.setText(_nativeAd.getAdSocialContext());
//            nativeAdBody.setText(_nativeAd.getAdBody());
//            nativeAdCallToAction.setText(_nativeAd.getAdCallToAction());
//
//            // Register the Title and CTA button to listen for clicks.
//            List<View> clickableViews = new ArrayList<>(); //linanw to-do: stick to to current native_ad_layout
//            clickableViews.add(nativeAdTitle);
//            clickableViews.add(nativeAdCallToAction);
//            _nativeAd.registerViewForInteraction(_container, clickableViews);
//
//            // Download and display the ad icon.
//            NativeAd.Image adIcon = _nativeAd.getAdIcon();
//            NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);
//
//            // Download and display the cover image.
//            nativeAdMedia.setNativeAd(_nativeAd); //linanw to-do: havily in loading
//
//            // Add the AdChoices icon
//            AdChoicesView adChoicesView = new AdChoicesView(_context, _nativeAd, true);
//            adChoicesContainer.removeAllViews();
//            adChoicesContainer.addView(adChoicesView);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    NewsListViewAdapter(ArrayList<JSONArray> newsFeedPages, final NativeAdsManager nativeAdsManager) {
        _newsFeedPages = newsFeedPages;
        _pageSize = 20;

        if(nativeAdsManager!=null)
        {
            nativeAdsManager.setListener(new NativeAdsManager.Listener() {
                @Override
                public void onAdsLoaded() {
                    nativeAdBufferViewHolders.clear();
                    for(int i=0;i<nativeAdsManager.getUniqueNativeAdCount();i++) {
//                        NativeAd nativeAd = nativeAdsManager.nextNativeAd();
//                        //prepare nativeAdContainer
//                        LinearLayout nativeAdContainer = (LinearLayout)LayoutInflater.from(_recyclerView.getContext())
//                                .inflate(R.layout.native_ad_container, (ViewGroup)_recyclerView.getRootView() , false);
//
//                        // Add the Ad view into the ad container.
//                        LayoutInflater inflater = LayoutInflater.from(nativeAdContainer.getContext());
//                        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
//                        LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, nativeAdContainer, false);
//                        // Create native UI using the ad metadata.
//                        ImageView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
//                        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
//                        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
//                        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
//                        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
//                        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);
//                        // Set the Text.
//                        nativeAdTitle.setText(String.format(Locale.ENGLISH, "%d/%d %s", i, nativeAdsManager.getUniqueNativeAdCount(), nativeAd.getAdTitle()));
//                        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
//                        nativeAdBody.setText(nativeAd.getAdBody());
//                        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
//
//                        // Download and display the ad icon.
//                        NativeAd.Image adIcon = nativeAd.getAdIcon();
//                        NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);
//
//                        // Download and display the cover image.
//                        nativeAdMedia.setNativeAd(nativeAd); //linanw mark: heavily in loading
//                        nativeAdContainer.addView(adView);
//
//                        // Register the Title and CTA button to listen for clicks.
//                        List<View> clickableViews = new ArrayList<>(); //linanw to-do: stick to to current native_ad_layout
//                        clickableViews.add(nativeAdTitle);
//                        clickableViews.add(nativeAdCallToAction);
//                        nativeAd.registerViewForInteraction(nativeAdContainer, clickableViews);
//
//                        //prepare adChoiceContainer
//                        LinearLayout adChoicesContainer = nativeAdContainer.findViewById(R.id.ad_choices_container);
//                        // Add the AdChoices icon
//                        AdChoicesView adChoicesView = new AdChoicesView(nativeAdContainer.getContext(), nativeAd, true);
//                        adChoicesContainer.addView(adChoicesView);
//
//                        nativeAdBufferViewHolders.add(nativeAdContainer);
                        NativeAd nativeAd = nativeAdsManager.nextNativeAd();
                        //prepare nativeAdContainer
                        LinearLayout nativeAdContainer = (LinearLayout)LayoutInflater.from(_recyclerView.getContext())
                                .inflate(R.layout.native_ad_container, (ViewGroup)_recyclerView.getRootView() , false);

                        // Add the Ad view into the ad container.
                        LayoutInflater inflater = LayoutInflater.from(nativeAdContainer.getContext());
                        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
                        ConstraintLayout adView = (ConstraintLayout) inflater.inflate(R.layout.native_ad_layout_custom, nativeAdContainer, false);
                        // Create native UI using the ad metadata.
                        ImageView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
                        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
                        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
                        //TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
                        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
                        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);
                        // Set the Text.
                        //nativeAdTitle.setText(String.format(Locale.ENGLISH, "%d/%d %s", i, nativeAdsManager.getUniqueNativeAdCount(), nativeAd.getAdTitle()));
                        nativeAdTitle.setText(nativeAd.getAdTitle());
                        //nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                        nativeAdBody.setText(nativeAd.getAdBody());
                        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());

                        // Download and display the ad icon.
                        NativeAd.Image adIcon = nativeAd.getAdIcon();
                        //NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

                        // Download and display the cover image.
                        nativeAdMedia.setNativeAd(nativeAd); //linanw mark: heavily in loading
                        nativeAdContainer.addView(adView);

                        // Register the Title and CTA button to listen for clicks.
                        List<View> clickableViews = new ArrayList<>(); //linanw to-do: stick to to current native_ad_layout
                        clickableViews.add(nativeAdTitle);
                        clickableViews.add(nativeAdBody);
                        clickableViews.add(nativeAdMedia);
                        clickableViews.add(nativeAdCallToAction);
                        nativeAd.registerViewForInteraction(nativeAdContainer, clickableViews);

                        //prepare adChoiceContainer
                        LinearLayout adChoicesContainer = nativeAdContainer.findViewById(R.id.ad_choices_container);
                        // Add the AdChoices icon
                        AdChoicesView adChoicesView = new AdChoicesView(nativeAdContainer.getContext(), nativeAd, true);
                        adChoicesContainer.addView(adChoicesView);

                        nativeAdBufferViewHolders.add(nativeAdContainer);
                    }

                    //NewsListViewAdapter.this.notifyAll();
                    NewsListViewAdapter.this.notifyDataSetChanged();
                }

                @Override
                public void onAdError(AdError adError) {

                }
            });
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NewsListViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        View v;
        switch (viewType) {
            case AD_ITEM:
                    v = new LinearLayout(parent.getContext());
                    v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return new AdViewHolder(v);
            case LOADING_ITEM:
                v = new TextView(parent.getContext());
                ((TextView)v).setHeight(200);
                ((TextView)v).setWidth(1000);
                ((TextView)v).setHint("Loading more articles...");
                //((TextView)v).setText("Text"); //linanw to-do, move to string.xml
                ((TextView) v).setGravity(Gravity.CENTER);
                return new ViewHolder(v);
            case NEWS_ITEM:
                // create a new view
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.news_list_item, parent, false);
                return new NewsViewHolder(v);
            default:
                return null;

        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch(viewType) {
            case AD_ITEM:
//                if (//!((AdViewHolder)holder).isNativeAdBound() &&
//                    _nativeAdsManager != null &&
//                    _nativeAdsManager.isLoaded()) {
//                    ((AdViewHolder)holder).bindView(_nativeAdsManager.nextNativeAd());
//                }
                ((AdViewHolder)holder).bindView(position);
                break;
            case NEWS_ITEM:
                int index = position - position/(AD_INTERVAL+1);
                // - get element from your dataset at this position
                // - replace the contents of the view with that element
                NewsViewHolder newsViewHolder = (NewsViewHolder)holder;

                int page = index/_pageSize;
                int offset = index%_pageSize;
                try {
                    JSONObject objectNews = _newsFeedPages.get(page).getJSONObject(offset);
                    newsViewHolder.bindView(objectNews);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case LOADING_ITEM:
                break;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (_newsFeedPages==null||_newsFeedPages.size()==0 || _newsFeedPages.get(0).length() == 0) _itemCount = 0;
        else {
            int feedItemCount = 0;
            for(int i=0;i<_newsFeedPages.size();i++) feedItemCount +=_newsFeedPages.get(i).length();
            _itemCount = feedItemCount + feedItemCount/ AD_INTERVAL;
        }
        return _itemCount + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position >= _itemCount) return LOADING_ITEM;
        else if ((position + 1) % (AD_INTERVAL + 1) == 0) return AD_ITEM;
        else return NEWS_ITEM;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        _recyclerView = recyclerView;
    }

    private static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

//    private static int pxToDp(int px)
//    {
//        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
//    }
}