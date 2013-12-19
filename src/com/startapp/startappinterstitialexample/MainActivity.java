package com.startapp.startappinterstitialexample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.startapp.android.publish.Ad;
import com.startapp.android.publish.AdDisplayListener;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.splash.SplashConfig;
import com.startapp.android.publish.splash.SplashConfig.Theme;

public class MainActivity extends Activity {

	/** StartAppAd object deceleration */
	private StartAppAd startAppAd = new StartAppAd(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StartAppAd.init(this, "DeveloperID", "ApplicationID"); //TODO: Replace with your IDs
		
		/** Create Splash Ad **/
		StartAppAd.showSplash(this, savedInstanceState,
				new SplashConfig()
					.setTheme(Theme.SKY)
					.setLogo(R.drawable.logo360x360)
					.setAppName("StartApp Example")
		);
		setContentView(R.layout.activity_main);
	}

	/**
	 * Method to run when next activity button clicks.
	 * @param view
	 */
	public void btnNextActivityClick(View view) {
		
		// Show an Ad
		startAppAd.showAd(new AdDisplayListener() {
			
			/**
			 * Callback when Ad had been hide
			 * @param ad
			 */
			@Override
			public void adHidden(Ad ad) {
				// Run Second activity right after the ad had been hidden
				Intent nextActivity = new Intent(MainActivity.this,
						SecondActivity.class);
				startActivity(nextActivity);
			}

			/**
			 * Callback when Ad had been displayed
			 * @param ad
			 */
			@Override
			public void adDisplayed(Ad ad) {

			}
		});
	}
	

	/**
	 * part of Activity life cycle Need as part of the StartAppAd
	 * object life cycle
	 */
	@Override
	public void onResume() {
		super.onResume();
		startAppAd.onResume();
	}

	/**
	 * part of Activity life cycle Need as part of the StartAppAd
	 * object life cycle
	 */
	@Override
	public void onPause() {
		super.onPause();
		startAppAd.onPause();
	}

	/**
	 * part of Activity life cycle For Back button implementation
	 */
	@Override
	public void onBackPressed() {
		startAppAd.onBackPressed();
		super.onBackPressed();
	}
}
