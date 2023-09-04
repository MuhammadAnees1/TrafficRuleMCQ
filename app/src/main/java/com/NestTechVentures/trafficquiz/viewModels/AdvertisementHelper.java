package com.NestTechVentures.trafficquiz.viewModels;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.NestTechVentures.trafficquiz.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class AdvertisementHelper {
    private Context context;
    private AdView bannerAdView;
    private InterstitialAd mInterstitialAd;

    public AdvertisementHelper(Context context) {
        this.context = context;
        initializeAds();
    }

    private void initializeAds() {

        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
    }

    public void showInterstitialAd(Activity activity){
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(context, context.getString(R.string.admob_interstitial_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.show(activity);
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                        Log.i("ErrorAd", "FieldCalled");

                    }
                });
    }

    public void showBannerAd(AdView adView) {
        bannerAdView = adView;
        AdRequest adRequest = new AdRequest.Builder().build();
        bannerAdView.loadAd(adRequest);
    }


    public void pauseBannerAd() {
        if (bannerAdView != null) {
            bannerAdView.pause();
        }
    }

    public void resumeBannerAd() {
        if (bannerAdView != null) {
            bannerAdView.resume();
        }
    }

    public void destroyBannerAd() {
        if (bannerAdView != null) {
            bannerAdView.destroy();
        }
    }
}
