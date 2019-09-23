package com.startapp.startappinterstitialexample;

import android.app.Activity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.startapp.android.publish.ads.nativead.NativeAdPreferences;
import com.startapp.android.publish.ads.nativead.StartAppNativeAd;
import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;


public class RecyclerViewActivity extends Activity {

    private NativeAdsAdapter mAdapter;

    private StartAppNativeAd mStartAppNativeAd = new StartAppNativeAd(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        mAdapter = new NativeAdsAdapter();
        recyclerView.setAdapter(mAdapter);

        mStartAppNativeAd.loadAd(
                new NativeAdPreferences()
                        .setAdsNumber(10)
                        .setAutoBitmapDownload(true)
                        .setPrimaryImageSize(2),
                        mNativeAdListener);
    }

    /**
     * Native Ad Callback
     */
    private AdEventListener mNativeAdListener = new AdEventListener() {

        @Override
        public void onReceiveAd(Ad ad) {
            // Get the native ads
            mAdapter.setItems(mStartAppNativeAd.getNativeAds());
        }

        @Override
        public void onFailedToReceiveAd(Ad ad) {
        }
    };
}
