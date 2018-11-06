package com.startapp.startappinterstitialexample;

import java.util.ArrayList;

import com.startapp.android.publish.ads.nativead.NativeAdDetails;
import com.startapp.android.publish.ads.nativead.NativeAdPreferences;
import com.startapp.android.publish.ads.nativead.StartAppNativeAd;
import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppAd.AdMode;
import com.startapp.android.publish.adsCommon.StartAppSDK;
import com.startapp.android.publish.adsCommon.VideoListener;
import com.startapp.android.publish.adsCommon.adListeners.AdDisplayListener;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    /**
     * Uses for storing whether gdpr dialog was shown
     */
    private static final String SHARED_PREFS_GDPR_SHOWN = "gdpr_dialog_was_shown";

    /**
     * StartAppAd object declaration
     */
    private StartAppAd startAppAd = new StartAppAd(this);

    /**
     * StartApp Native Ad declaration
     */
    private StartAppNativeAd startAppNativeAd = new StartAppNativeAd(this);

    /**
     * Native Ad Callback
     */
    private AdEventListener nativeAdListener = new AdEventListener() {

        @Override
        public void onReceiveAd(Ad ad) {
            // Get the native ad
            NativeAdDetails nativeAd = null;
            ArrayList<NativeAdDetails> nativeAdsList = startAppNativeAd.getNativeAds();
            if (nativeAdsList.size() > 0) {
                nativeAd = nativeAdsList.get(0);
            }

            // Verify that an ad was retrieved
            if (nativeAd == null) {
                return;
            }

            final LinearLayout layout = findViewById(R.id.nativeAdLayout);
            final ImageView imageView = new ImageView(MainActivity.this);
            imageView.setImageBitmap(nativeAd.getImageBitmap());
            layout.addView(imageView);

            final TextView textView = new TextView(MainActivity.this);
            final StringBuilder builder = new StringBuilder();
            builder
                    .append("Title: ").append(nativeAd.getTitle()).append("\n\n")
                    .append("Description: ").append(nativeAd.getDescription()).append("\n\n")
                    .append("Rating: ").append(nativeAd.getRating()).append("\n\n")
                    .append("ImageUrl: ").append(nativeAd.getImageUrl()).append("\n\n")
                    .append("SecondaryImageUrl: ").append(nativeAd.getSecondaryImageUrl());

            textView.setText(builder);
            layout.addView(textView);

            nativeAd.registerViewForInteraction(layout);
        }

        @Override
        public void onFailedToReceiveAd(Ad ad) {
            // Error occurred while loading the native ad
            final LinearLayout layout = findViewById(R.id.nativeAdLayout);
            final TextView textView = new TextView(MainActivity.this);
            textView.setText("Error while loading Native Ad");
            layout.addView(textView);
        }
    };

    /**
     * Set user consent and initialize the SDK
     */
    private void writePersonalizedAdsConsent(boolean isGranted) {
        StartAppSDK.setUserConsent(this,
                "pas",
                System.currentTimeMillis(),
                isGranted);

        getPreferences(Context.MODE_PRIVATE)
                .edit()
                .putBoolean(SHARED_PREFS_GDPR_SHOWN, true)
                .commit();
    }

    /**
     * Initialize the SDK
     */
    private void initStartAppSdk() {
        StartAppSDK.init(this, "ApplicationID", true); //TODO: Replace with your Application ID
    }

    /**
     * Ask an user for the consent
     */
    private void showGdprDialog(final Runnable callback) {
        final View view = getLayoutInflater().inflate(R.layout.dialog_gdpr, null);
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar);
        dialog.setContentView(view);

        final Typeface medium = Typeface.createFromAsset(getAssets(), "gotham_medium.ttf");
        final Typeface book = Typeface.createFromAsset(getAssets(), "gotham_book.ttf");
        ((TextView) view.findViewById(R.id.title)).setTypeface(medium);
        ((TextView) view.findViewById(R.id.body)).setTypeface(book);

        final Button okBtn = view.findViewById(R.id.okBtn);
        okBtn.setTypeface(medium);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    callback.run();
                }

                writePersonalizedAdsConsent(true);
                dialog.dismiss();
            }
        });

        final Button cancelBtn = view.findViewById(R.id.cancelBtn);
        cancelBtn.setTypeface(medium);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    callback.run();
                }

                writePersonalizedAdsConsent(false);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * Check whether we asked an user about the consent and if not then ask and initialize the SDK
     */
    private void initStartAppSdkAccordingToConsent(final Runnable callback) {
        if (getPreferences(Context.MODE_PRIVATE).getBoolean(SHARED_PREFS_GDPR_SHOWN, false)) {
            initStartAppSdk();
            callback.run();
            return;
        }

        showGdprDialog(new Runnable() {
            @Override
            public void run() {
                initStartAppSdk();
                callback.run();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initStartAppSdkAccordingToConsent(new Runnable() {

            @Override
            public void run() {
                /**
                 * Load Native Ad with the following parameters:
                 * 1. Only 1 Ad
                 * 2. Download ad image automatically
                 * 3. Image size of 150x150px
                 */
                startAppNativeAd.loadAd(
                        new NativeAdPreferences()
                                .setAdsNumber(1)
                                .setAutoBitmapDownload(true)
                                .setPrimaryImageSize(2),
                        nativeAdListener);
            }
        });

        setContentView(R.layout.activity_main);
    }

    /**
     * Method to run when the next activity button is clicked.
     *
     * @param view
     */
    public void btnNextActivityClick(View view) {

        // Show an Ad
        startAppAd.showAd(new AdDisplayListener() {

            /**
             * Callback when Ad has been hidden
             * @param ad
             */
            @Override
            public void adHidden(Ad ad) {

                // Run second activity right after the ad was hidden
                Intent nextActivity = new Intent(MainActivity.this,
                        SecondActivity.class);
                startActivity(nextActivity);
            }

            /**
             * Callback when ad has been displayed
             * @param ad
             */
            @Override
            public void adDisplayed(Ad ad) {

            }

            /**
             * Callback when ad has been clicked
             * @param ad
             */
            @Override
            public void adClicked(Ad arg0) {

            }

            /**
             * Callback when ad not displayed
             * @param ad
             */
            @Override
            public void adNotDisplayed(Ad arg0) {

            }
        });
    }

    /**
     * Method to run when rewarded button is clicked
     *
     * @param view
     */
    public void btnShowRewardedClick(View view) {
        final StartAppAd rewardedVideo = new StartAppAd(this);

        /**
         * This is very important: set the video listener to be triggered after video
         * has finished playing completely
         */
        rewardedVideo.setVideoListener(new VideoListener() {

            @Override
            public void onVideoCompleted() {
                Toast.makeText(MainActivity.this, "Rewarded video has completed - grant the user his reward", Toast.LENGTH_LONG).show();
            }
        });

        /**
         * Load rewarded by specifying AdMode.REWARDED
         * We are using AdEventListener to trigger ad show
         */
        rewardedVideo.loadAd(AdMode.REWARDED_VIDEO, new AdEventListener() {

            @Override
            public void onReceiveAd(Ad arg0) {
                rewardedVideo.showAd();
            }

            @Override
            public void onFailedToReceiveAd(Ad arg0) {
                /**
                 * Failed to load rewarded video:
                 * 1. Check that FullScreenActivity is declared in AndroidManifest.xml:
                 * See https://github.com/StartApp-SDK/Documentation/wiki/Android-InApp-Documentation#activities
                 * 2. Is android API level above 16?
                 */
                Log.e("MainActivity", "Failed to load rewarded video with reason: " + arg0.getErrorMessage());
            }
        });
    }

    /**
     * Part of the activity's life cycle, StartAppAd should be integrated here.
     */
    @Override
    public void onResume() {
        super.onResume();
        startAppAd.onResume();
    }

    /**
     * Part of the activity's life cycle, StartAppAd should be integrated here
     * for the home button exit ad integration.
     */
    @Override
    public void onPause() {
        super.onPause();
        startAppAd.onPause();
    }

    /**
     * Part of the activity's life cycle, StartAppAd should be integrated here
     * for the back button exit ad integration.
     */
    @Override
    public void onBackPressed() {
        startAppAd.onBackPressed();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Allow an user to change his/her mind according to GDPR specifications
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ads_item:
                showGdprDialog(null);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
