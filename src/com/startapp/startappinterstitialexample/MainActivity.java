package com.startapp.startappinterstitialexample;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.startapp.android.publish.Ad;
import com.startapp.android.publish.AdDisplayListener;
import com.startapp.android.publish.AdEventListener;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;
import com.startapp.android.publish.nativead.NativeAdDetails;
import com.startapp.android.publish.nativead.NativeAdPreferences;
import com.startapp.android.publish.nativead.NativeAdPreferences.NativeAdBitmapSize;
import com.startapp.android.publish.nativead.StartAppNativeAd;
import com.startapp.android.publish.splash.SplashConfig;
import com.startapp.android.publish.splash.SplashConfig.Theme;

public class MainActivity extends Activity {
	
	/** StartAppAd object declaration */
	private StartAppAd startAppAd = new StartAppAd(this);
	
	/** StartApp Native Ad declaration */
	private StartAppNativeAd startAppNativeAd = new StartAppNativeAd(this);
	private NativeAdDetails nativeAd = null;
	
	private ImageView imgFreeApp = null;
	private TextView txtFreeApp = null;
	
	/** Native Ad Callback */
	private AdEventListener nativeAdListener = new AdEventListener() {
		
		@Override
		public void onReceiveAd(Ad ad) {
			
			// Get the native ad
			ArrayList<NativeAdDetails> nativeAdsList = startAppNativeAd.getNativeAds();
			if (nativeAdsList.size() > 0){
				nativeAd = nativeAdsList.get(0);
			}
			
			// Verify that an ad was retrieved
			if (nativeAd != null){
				
				// When ad is received and displayed - we MUST send impression
				nativeAd.sendImpression(MainActivity.this);
				
				if (imgFreeApp != null && txtFreeApp != null){
					
					// Set button as enabled
					imgFreeApp.setEnabled(true);
					txtFreeApp.setEnabled(true);
					
					// Set ad's image
					imgFreeApp.setImageBitmap(nativeAd.getImageBitmap());
					
					// Set ad's title
					txtFreeApp.setText(nativeAd.getTitle());
				}
			}
		}
		
		@Override
		public void onFailedToReceiveAd(Ad ad) {
			
			// Error occurred while loading the native ad
			if (txtFreeApp != null) {
				txtFreeApp.setText("Error while loading Native Ad");
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StartAppSDK.init(this, "DeveloperID", "ApplicationID", true); //TODO: Replace with your IDs
		
		/** Create Splash Ad **/
		StartAppAd.showSplash(this, savedInstanceState,
				new SplashConfig()
					.setTheme(Theme.GLOOMY)
					.setLogo(R.drawable.logo)
					.setAppName("StartApp Example")
		);
		
		setContentView(R.layout.activity_main);
		
		/** Add Slider **/
		StartAppAd.showSlider(this);
		
		/** Initialize Native Ad views **/
		imgFreeApp = (ImageView) findViewById(R.id.imgFreeApp);
		txtFreeApp = (TextView) findViewById(R.id.txtFreeApp);
		if (txtFreeApp != null) {
			txtFreeApp.setText("Loading Native Ad...");
		}
		
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
					.setImageSize(NativeAdBitmapSize.SIZE150X150),
				nativeAdListener);
	}

	/**
	 * Method to run when the next activity button is clicked.
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
		});
	}

	/**
	 * Runs when the native ad is clicked (either the image or the title).
	 * @param view
	 */
	public void freeAppClick(View view){
		if (nativeAd != null){
			nativeAd.sendClick(this);
		}
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
}
