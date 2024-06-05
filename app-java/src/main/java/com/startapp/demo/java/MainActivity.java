package com.startapp.demo.java;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.startapp.sdk.ads.banner.BannerFormat;
import com.startapp.sdk.ads.banner.BannerListener;
import com.startapp.sdk.ads.banner.BannerRequest;
import com.startapp.sdk.ads.nativead.NativeAdDetails;
import com.startapp.sdk.ads.nativead.NativeAdPreferences;
import com.startapp.sdk.ads.nativead.StartAppNativeAd;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.startapp.sdk.adsbase.model.AdPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    enum AdState {
        IDLE,
        LOADING,
        VISIBLE,
    }

    private static final MutableLiveData<Boolean> initialized = new MutableLiveData<>(null);

    private final MutableLiveData<Pair<StartAppAd, AdState>> interstitialLiveData = new MutableLiveData<>();
    private final MutableLiveData<Pair<StartAppAd, AdState>> rewardedLiveData = new MutableLiveData<>();
    private final MutableLiveData<Pair<View, AdState>> bannerLiveData = new MutableLiveData<>();
    private final MutableLiveData<Pair<View, AdState>> mrecLiveData = new MutableLiveData<>();
    private final MutableLiveData<Pair<View, AdState>> nativeLiveData = new MutableLiveData<>();

    private ViewGroup bannerContainer;
    private ViewGroup mrecContainer;
    private ViewGroup nativeContainer;

    @Override
    protected void onCreate(@Nullable Bundle state) {
        super.onCreate(state);

        setTitle("Start.io SDK " + StartAppSDK.getVersion());

        setContentView(R.layout.activity_main);

        // region UI initialization

        View loadInterstitial = findViewById(R.id.load_interstitial);
        View showInterstitial = findViewById(R.id.show_interstitial);
        View loadRewarded = findViewById(R.id.load_rewarded);
        View showRewarded = findViewById(R.id.show_rewarded);
        View loadBanner = findViewById(R.id.load_banner);
        View showBanner = findViewById(R.id.show_banner);
        View hideBanner = findViewById(R.id.hide_banner);
        View loadMrec = findViewById(R.id.load_mrec);
        View showMrec = findViewById(R.id.show_mrec);
        View hideMrec = findViewById(R.id.hide_mrec);
        View loadNative = findViewById(R.id.load_native_small);
        View showNative = findViewById(R.id.show_native_small);
        View hideNative = findViewById(R.id.hide_native_small);
        bannerContainer = findViewById(R.id.banner_container);
        mrecContainer = findViewById(R.id.mrec_container);
        nativeContainer = findViewById(R.id.native_container);

        loadInterstitial.setOnClickListener(this::loadInterstitial);
        showInterstitial.setOnClickListener(this::showInterstitial);
        loadRewarded.setOnClickListener(this::loadRewarded);
        showRewarded.setOnClickListener(this::showRewarded);
        loadBanner.setOnClickListener(this::loadBanner);
        showBanner.setOnClickListener(this::showBanner);
        hideBanner.setOnClickListener(this::hideBanner);
        loadMrec.setOnClickListener(this::loadMrec);
        showMrec.setOnClickListener(this::showMrec);
        hideMrec.setOnClickListener(this::hideMrec);
        loadNative.setOnClickListener(this::loadNative);
        showNative.setOnClickListener(this::showNative);
        hideNative.setOnClickListener(this::hideNative);

        interstitialLiveData.observe(this, pair -> {
            loadInterstitial.setEnabled(isLoadButtonEnabled(pair));
            showInterstitial.setEnabled(isShowButtonEnabled(pair));
        });

        rewardedLiveData.observe(this, pair -> {
            loadRewarded.setEnabled(isLoadButtonEnabled(pair));
            showRewarded.setEnabled(isShowButtonEnabled(pair));
        });

        bannerLiveData.observe(this, pair -> {
            loadBanner.setEnabled(isLoadButtonEnabled(pair));
            showBanner.setEnabled(isShowButtonEnabled(pair));
            hideBanner.setEnabled(isHideButtonVisible(pair));
            bannerContainer.setVisibility(isHideButtonVisible(pair) ? View.VISIBLE : View.GONE);
        });

        mrecLiveData.observe(this, pair -> {
            loadMrec.setEnabled(isLoadButtonEnabled(pair));
            showMrec.setEnabled(isShowButtonEnabled(pair));
            hideMrec.setEnabled(isHideButtonVisible(pair));
            mrecContainer.setVisibility(isHideButtonVisible(pair) ? View.VISIBLE : View.GONE);
        });

        nativeLiveData.observe(this, pair -> {
            loadNative.setEnabled(isLoadButtonEnabled(pair));
            showNative.setEnabled(isShowButtonEnabled(pair));
            hideNative.setEnabled(isHideButtonVisible(pair));
            nativeContainer.setVisibility(isHideButtonVisible(pair) ? View.VISIBLE : View.GONE);
        });

        // endregion

        initialized.observe(this, value -> {
            if (value == null) {
                initialized.setValue(false);

                // TODO make sure to remove this line in production
                StartAppSDK.setTestAdsEnabled(true);

                // TODO make sure to use your own App ID from the https://portal.start.io
                // NOTE for the testing purposes you can use demo App ID: 205489527
                StartAppSDK.initParams(this, "205489527")
                        .setReturnAdsEnabled(false)
                        .setCallback(() -> initialized.setValue(true))
                        .init();
            } else if (value) {
                interstitialLiveData.setValue(null);
                rewardedLiveData.setValue(null);
                bannerLiveData.setValue(null);
                mrecLiveData.setValue(null);
                nativeLiveData.setValue(null);
            }
        });
    }

    private static boolean isInitialized() {
        return Boolean.TRUE.equals(initialized.getValue());
    }

    private static <T> boolean isLoadButtonEnabled(@Nullable Pair<T, AdState> pair) {
        return (pair == null || pair.first == null && pair.second != AdState.LOADING) && isInitialized();
    }

    private static <T> boolean isShowButtonEnabled(@Nullable Pair<T, AdState> pair) {
        return pair != null && pair.first != null && pair.second != AdState.VISIBLE;
    }

    private static <T> boolean isHideButtonVisible(@Nullable Pair<T, AdState> pair) {
        return pair != null && pair.second == AdState.VISIBLE;
    }

    // region Banner & Mrec

    private void loadBanner(@NonNull View view) {
        ViewGroup.LayoutParams layoutParams = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT, Gravity.CENTER);
        loadAdView(BannerFormat.BANNER, null, layoutParams, bannerLiveData);
    }

    private void loadMrec(@NonNull View view) {
        ViewGroup.LayoutParams layoutParams = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT, Gravity.CENTER);
        loadAdView(BannerFormat.MREC, null, layoutParams, mrecLiveData);
    }

    private void showBanner(@NonNull View view) {
        showAdView(bannerLiveData, bannerContainer);
    }

    private void showMrec(@NonNull View view) {
        showAdView(mrecLiveData, mrecContainer);
    }

    private void hideBanner(@NonNull View view) {
        hideAdView(bannerLiveData, bannerContainer);
    }

    private void hideMrec(@NonNull View view) {
        hideAdView(mrecLiveData, mrecContainer);
    }

    @SuppressWarnings("SameParameterValue")
    private void loadAdView(@NonNull BannerFormat format, @Nullable String adTag, @NonNull ViewGroup.LayoutParams layoutParams, @NonNull MutableLiveData<Pair<View, AdState>> liveData) {
        AdPreferences adPreferences = new AdPreferences();

        if (adTag != null) {
            adPreferences.setAdTag(adTag);
        }

        new BannerRequest(getApplicationContext())
                .setAdFormat(format)
                .setAdPreferences(adPreferences)
                .load((creator, error) -> {
                    if (creator != null) {
                        View adView = creator.create(getApplicationContext(), new BannerListener() {
                            @Override
                            public void onReceiveAd(View banner) {
                                Log.v(LOG_TAG, "loadAdView: onReceiveAd");
                            }

                            @Override
                            public void onFailedToReceiveAd(View banner) {
                                Log.v(LOG_TAG, "loadAdView: onFailedToReceiveAd");
                            }

                            @Override
                            public void onImpression(View banner) {
                                Log.v(LOG_TAG, "loadAdView: onImpression");
                            }

                            @Override
                            public void onClick(View banner) {
                                Log.v(LOG_TAG, "loadAdView: onClick");
                            }
                        });

                        adView.setLayoutParams(layoutParams);

                        liveData.setValue(new Pair<>(adView, AdState.IDLE));
                    } else {
                        Log.e(LOG_TAG, "loadAdView: error: " + error);

                        liveData.setValue(null);
                    }
                });

        liveData.setValue(new Pair<>(null, AdState.LOADING));
    }

    private void showAdView(@NonNull MutableLiveData<Pair<View, AdState>> liveData, @NonNull ViewGroup container) {
        Pair<View, AdState> pair = liveData.getValue();
        if (pair != null && pair.first != null) {
            container.removeAllViews();
            container.addView(pair.first);
            liveData.setValue(new Pair<>(pair.first, AdState.VISIBLE));
        } else {
            Toast.makeText(this, "AdView is not ready", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideAdView(@NonNull MutableLiveData<Pair<View, AdState>> liveData, @NonNull ViewGroup container) {
        container.removeAllViews();
        liveData.setValue(new Pair<>(null, AdState.IDLE));
    }

    // endregion

    // region Native

    private void loadNative(@NonNull View view) {
        nativeLiveData.setValue(new Pair<>(null, AdState.LOADING));

        NativeAdPreferences adPreferences = new NativeAdPreferences();
        adPreferences.setAutoBitmapDownload(true);

        StartAppNativeAd nativeAd = new StartAppNativeAd(getApplicationContext());
        nativeAd.setPreferences(adPreferences);
        nativeAd.loadAd(new AdEventListener() {
            @Override
            public void onReceiveAd(@NonNull Ad ad) {
                Log.v(LOG_TAG, "loadNative: onReceiveAd");

                ArrayList<NativeAdDetails> nativeAds = nativeAd.getNativeAds();
                if (nativeAds != null && !nativeAds.isEmpty()) {
                    NativeAdDetails nativeAdDetails = nativeAds.get(0);
                    if (nativeAdDetails != null) {
                        View adView = getLayoutInflater().inflate(R.layout.native_ad, nativeContainer, false);

                        ImageView imageView = adView.findViewById(R.id.image);
                        TextView titleView = adView.findViewById(R.id.title);
                        TextView ratingView = adView.findViewById(R.id.rating);
                        TextView categoryView = adView.findViewById(R.id.category);
                        TextView descriptionView = adView.findViewById(R.id.description);
                        Button callToActionButton = adView.findViewById(R.id.call_to_action);

                        Bitmap image = nativeAdDetails.getImageBitmap();
                        if (image != null) {
                            imageView.setImageBitmap(image);
                        } else {
                            imageView.setVisibility(View.GONE);
                        }

                        titleView.setText(nativeAdDetails.getTitle());
                        ratingView.setText(String.format(Locale.ROOT, "Rating: %.1f⭐", nativeAdDetails.getRating()));
                        categoryView.setText(String.format(Locale.ROOT, "Category: %s", nativeAdDetails.getCategory()));
                        descriptionView.setText(nativeAdDetails.getDescription());

                        List<View> clickableViews;

                        String callToAction = nativeAdDetails.getCallToAction();
                        if (callToAction.isEmpty()) {
                            callToActionButton.setVisibility(View.GONE);
                            clickableViews = null;
                        } else {
                            callToActionButton.setText(callToAction);
                            clickableViews = Collections.singletonList(callToActionButton);
                        }

                        nativeAdDetails.registerViewForInteraction(adView, clickableViews);

                        nativeLiveData.setValue(new Pair<>(adView, AdState.IDLE));
                    } else {
                        nativeLiveData.setValue(null);
                    }
                } else {
                    nativeLiveData.setValue(null);
                }
            }

            @Override
            public void onFailedToReceiveAd(@Nullable Ad ad) {
                Log.v(LOG_TAG, "loadNative: onFailedToReceiveAd: " + (ad != null ? ad.getErrorMessage() : null));

                nativeLiveData.setValue(null);
            }
        });
    }

    private void showNative(@NonNull View view) {
        Pair<View, AdState> pair = nativeLiveData.getValue();
        if (pair != null && pair.first != null) {
            nativeContainer.removeAllViews();
            nativeContainer.addView(pair.first);
            nativeLiveData.setValue(new Pair<>(pair.first, AdState.VISIBLE));
        } else {
            Toast.makeText(this, "Native is not ready", Toast.LENGTH_SHORT).show();
            nativeLiveData.setValue(null);
        }
    }

    private void hideNative(@NonNull View view) {
        nativeContainer.removeAllViews();
        nativeLiveData.setValue(null);
    }

    // endregion

    // region Interstitial

    private void loadInterstitial(@NonNull View view) {
        interstitialLiveData.setValue(new Pair<>(null, AdState.LOADING));

        StartAppAd interstitialAd = new StartAppAd(this);
        interstitialAd.loadAd(new AdEventListener() {
            @Override
            public void onReceiveAd(@NonNull Ad ad) {
                Log.v(LOG_TAG, "loadInterstitial: onReceiveAd");

                interstitialLiveData.setValue(new Pair<>(interstitialAd, AdState.IDLE));
            }

            @Override
            public void onFailedToReceiveAd(@Nullable Ad ad) {
                Log.v(LOG_TAG, "loadInterstitial: onFailedToReceiveAd: " + (ad != null ? ad.getErrorMessage() : null));

                interstitialLiveData.setValue(null);
            }
        });
    }

    public void showInterstitial(@NonNull View view) {
        Pair<StartAppAd, AdState> pair = interstitialLiveData.getValue();
        if (pair != null && pair.first != null) {
            interstitialLiveData.setValue(null);

            pair.first.showAd(new AdDisplayListener() {
                @Override
                public void adHidden(Ad ad) {
                    Log.v(LOG_TAG, "showInterstitial: adHidden");
                }

                @Override
                public void adDisplayed(Ad ad) {
                    Log.v(LOG_TAG, "showInterstitial: adDisplayed");
                }

                @Override
                public void adClicked(Ad ad) {
                    Log.v(LOG_TAG, "showInterstitial: adClicked");
                }

                @Override
                public void adNotDisplayed(Ad ad) {
                    Log.v(LOG_TAG, "showInterstitial: adNotDisplayed");
                }
            });
        } else {
            Toast.makeText(this, "Interstitial is not ready", Toast.LENGTH_SHORT).show();
        }
    }

    // endregion

    // region Rewarded

    private void loadRewarded(@NonNull View view) {
        rewardedLiveData.setValue(new Pair<>(null, AdState.LOADING));

        StartAppAd rewardedAd = new StartAppAd(this);
        rewardedAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
            @Override
            public void onReceiveAd(@NonNull Ad ad) {
                Log.v(LOG_TAG, "loadRewarded: onReceiveAd");

                rewardedLiveData.setValue(new Pair<>(rewardedAd, AdState.IDLE));
            }

            @Override
            public void onFailedToReceiveAd(@Nullable Ad ad) {
                Log.v(LOG_TAG, "loadRewarded: onFailedToReceiveAd: " + (ad != null ? ad.getErrorMessage() : null));

                rewardedLiveData.setValue(null);
            }
        });
    }

    public void showRewarded(@NonNull View view) {
        Pair<StartAppAd, AdState> pair = rewardedLiveData.getValue();
        if (pair != null && pair.first != null) {
            rewardedLiveData.setValue(null);

            pair.first.setVideoListener(() -> Toast.makeText(getApplicationContext(), "User gained a reward", Toast.LENGTH_SHORT).show());
            pair.first.showAd(new AdDisplayListener() {
                @Override
                public void adHidden(Ad ad) {
                    Log.v(LOG_TAG, "showRewarded: adHidden");
                }

                @Override
                public void adDisplayed(Ad ad) {
                    Log.v(LOG_TAG, "showRewarded: adDisplayed");
                }

                @Override
                public void adClicked(Ad ad) {
                    Log.v(LOG_TAG, "showRewarded: adClicked");
                }

                @Override
                public void adNotDisplayed(Ad ad) {
                    Log.v(LOG_TAG, "showRewarded: adNotDisplayed");
                }
            });
        } else {
            Toast.makeText(this, "Rewarded is not ready", Toast.LENGTH_SHORT).show();
        }
    }

    // endregion
}
