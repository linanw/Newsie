package com.example.linanw.newsie;

import android.app.Application;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

import org.json.JSONObject;

import java.util.ArrayList;

public class App extends Application {
    static ArrayList<JSONObject> _NewFeedsByType = new ArrayList<JSONObject>();

    //Audience Network Interstitial Ad
    private InterstitialAd interstitialAd;

    @Override
    public void onCreate() {
        super.onCreate();

        // Instantiate an InterstitialAd object
        interstitialAd = new InterstitialAd(this, "923586474486663_923681894477121");

        // Set listeners for the Interstitial Ad
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial displayed callback
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Toast.makeText(getApplicationContext(), "Error: " + adError.getErrorMessage(),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Show the ad when it's done loading.
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
//        interstitialAd.loadAd();
    }

    @Override
    public void onTerminate() {
        //this method actually will been call in production android device.
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }super.onTerminate();
    }
}
