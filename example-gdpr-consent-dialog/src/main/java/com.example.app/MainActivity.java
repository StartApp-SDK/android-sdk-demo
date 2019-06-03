package com.example.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.startapp.android.publish.ads.banner.Banner;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;
import com.startapp.consentdialog.ConsentDialogFragment;
import com.startapp.consentdialog.ConsentDialogListener;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainActivity extends AppCompatActivity implements ConsentDialogListener {
    @Override
    protected void onCreate(@Nullable Bundle state) {
        super.onCreate(state);

        if (ConsentDialogFragment.isUserDecisionSaved(this)) {
            // allow return ads if we already have user decision
            StartAppSDK.init(this, "ApplicationID", true);
        } else {
            // otherwise we don't allow return ads and turn off splash
            StartAppSDK.init(this, "ApplicationID", false);
            StartAppAd.disableSplash();
        }

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // if we have user decision, then we can safely show banner
        if (ConsentDialogFragment.isUserDecisionSaved(this)) {
            FrameLayout container = findViewById(R.id.banner_container);
            if (container != null && container.getChildCount() < 1) {
                container.addView(new Banner(this), new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER));
            }
        }
    }

    // this callback is configured from xml
    // it represents any action, where interstitial ad is applicable
    public void onSomethingHappened(View view) {
        if (ConsentDialogFragment.isUserDecisionSaved(this)) {
            // if we have user decision, just show an ads, no matter what user choose
            StartAppAd.showAd(this);
        } else {
            // otherwise, we need to ask user before the very first time showing ad
            ConsentDialogFragment.show(this);
        }
    }

    // this callback is configured from xml
    public void onShowConsentDialogExplicitly(View view) {
        // just allow user to see this dialog at any time (e.g. via settings)
        ConsentDialogFragment.show(this);
    }

    // this callback is optional
    @Override
    public void onConsentDialogDismissed() {
        StartAppAd.showAd(this);
    }
}
