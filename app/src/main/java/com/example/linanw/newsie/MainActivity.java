package com.example.linanw.newsie;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.facebook.ads.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Date backToExitFirstClick;
    private Toast pressAgainToExit;
    private Toast backToTop;
    private RecyclerView newsListRecyclerView;
    private RecyclerView.Adapter news_list_Adapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private LinearLayoutManager newsListLayoutManager;
    //private AdView adBannerView;

    //static objects that keep while Activity recreate while screen rotating etc.
    private static ArrayList<JSONArray> newsFeedPages = new ArrayList<JSONArray>();
    private static NativeAdsManager nativeAdsManager;
    private static String _feedStartingDateTime;

    private final static String FEED_BASE_URL = "https://newsapi.org/v2/everything";
    private final static String NEWSAPI_KEY = "cc187d889f8a422ebccd2b43d53f7f2c";//74ccd180d14a49d48a50f21ecc46fa2c";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();

//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);


        //Init nativeAdManager
        String placement_id = "923586474486663_923589894486321"; //linanw to-do, move to string.xml
        if(nativeAdsManager==null) {
            nativeAdsManager = new NativeAdsManager(this, placement_id, 10);
            nativeAdsManager.loadAds(NativeAd.MediaCacheFlag.ALL);
        }

//        if(newsFeedPages.size()==0) {
//            //load feed
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
//                    (Request.Method.GET, getFeedUrl(0), null, responseListener, responseErrorListener);
//            // Access the RequestQueue through your singleton class.
//            MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
//        }

        newsListLayoutManager = new LinearLayoutManager(this);
        //endless scrolling
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(newsListLayoutManager, newsFeedPages.size()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextFeedPage(page);
            }
        };

        //setup the NewsListView
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        if(newsListRecyclerView!=null)
        {
            String a = "";
        }
        newsListRecyclerView = (RecyclerView) findViewById(R.id.news_list_view);
        newsListRecyclerView.setHasFixedSize(false);
        newsListRecyclerView.setLayoutManager(newsListLayoutManager);
        news_list_Adapter = new NewsListViewAdapter(newsFeedPages, nativeAdsManager);
        newsListRecyclerView.setAdapter(news_list_Adapter);
        // Adds the scroll listener to RecyclerView
        newsListRecyclerView.addOnScrollListener(scrollListener);

        //Ad Banner
        // Instantiate an AdView view

//        adBannerView = new AdView(this, "923586474486663_923696784475632", AdSize.BANNER_HEIGHT_50);
//        //adBannerView = new AdView(this, "923586474486663_923685407810103", AdSize.RECTANGLE_HEIGHT_250);
//
//        // Find the Ad Container
//        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
//
//        // Add the ad view to your activity layout
//        adContainer.addView(adBannerView);
//
//        adBannerView.setAdListener(new AdListener() {
//            @Override
//            public void onError(Ad ad, AdError adError) {
//                // Ad error callback
//                Toast.makeText(MainActivity.this, "Error: " + adError.getErrorMessage(),
//                        Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onAdLoaded(Ad ad) {
//                // Ad loaded callback
//            }
//
//            @Override
//            public void onAdClicked(Ad ad) {
//                // Ad clicked callback
//            }
//
//            @Override
//            public void onLoggingImpression(Ad ad) {
//                // Ad impression logged callback
//            }
//        });
//
//        // Request an ad
//        adBannerView.loadAd();
        hockeyAppCheckForUpdates();
    }

    final Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {

        @Override
        public void onResponse(JSONObject response) {
            try {
                newsFeedPages.add(response.getJSONArray("articles"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int itemCount = news_list_Adapter.getItemCount();
            if (itemCount > 0) news_list_Adapter.notifyItemChanged(itemCount - 1);
        }
    };

    final Response.ErrorListener responseErrorListener = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            //Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            if(scrollListener!=null)scrollListener.tellLastFailed();
        }
    };

    @NonNull
    private String getFeedUrl(int page) {
        if (_feedStartingDateTime == null || _feedStartingDateTime == "") {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            _feedStartingDateTime = sdf.format(new Date());
        }
        StringBuilder url = new StringBuilder();
        url.append(FEED_BASE_URL + "?");
        url.append("to=" + _feedStartingDateTime + "&");
        url.append("sources=" + "bbc-news" + "&");
        url.append("apiKey=" + NEWSAPI_KEY + "&");
        url.append("page=" + (page + 1));
        return url.toString();
    }

    private void loadNextFeedPage(int page) {
        final int previousItemCount = news_list_Adapter.getItemCount();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, getFeedUrl(page), null, responseListener, responseErrorListener);
        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        int currentScrollPostion = newsListLayoutManager.findFirstVisibleItemPosition();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (currentScrollPostion != 0) {
            //if (currentScrollPostion > 10) newsListRecyclerView.scrollToPosition(10);
            newsListRecyclerView.scrollToPosition(0);
            backToTop = Toast.makeText(getApplicationContext(), "Back to top.", Toast.LENGTH_SHORT);
            backToTop.show();
        } else {
            Calendar cal = null;
            if (backToExitFirstClick != null) {
                cal = Calendar.getInstance();
                cal.setTime(backToExitFirstClick);
                cal.add(Calendar.SECOND, 2);
            }
            if (backToExitFirstClick != null && Calendar.getInstance().getTime().before(cal.getTime())) {
                pressAgainToExit.cancel();
                super.onBackPressed();
            } else {
                backToExitFirstClick = Calendar.getInstance().getTime();
                if(backToTop!=null)backToTop.cancel();
                pressAgainToExit = Toast.makeText(getApplicationContext(), "Press back again to exit app.", Toast.LENGTH_SHORT);
                pressAgainToExit.show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onSettingMenuItemCick(MenuItem item)
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        //EditText editText = (EditText) findViewById(R.id.editText);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        // ... your own onResume implementation
        hockeyAppCheckForCrashes();
    }

    @Override
    public void onPause() {
        super.onPause();
        hockeyAppUnregisterManagers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hockeyAppUnregisterManagers();
    }

    private void hockeyAppCheckForCrashes() {
        CrashManager.register(this);
    }

    private void hockeyAppCheckForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this);
    }

    private void hockeyAppUnregisterManagers() {
        UpdateManager.unregister();
    }

}
