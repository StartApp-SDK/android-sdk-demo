package com.startapp.startappinterstitialexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.RelativeLayout;

import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.banner.Banner;

public class SecondActivity extends Activity {
	
	/** StartAppAd object declaration */
	private StartAppAd startAppAd = new StartAppAd(this);
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);
		
		/** Add banner programmatically (within Java code, instead of within the layout xml) **/
		RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.second_layout);
		
		// Create new StartApp banner
		Banner startAppBanner = new Banner(this);
		RelativeLayout.LayoutParams bannerParameters = 
				new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
		bannerParameters.addRule(RelativeLayout.CENTER_HORIZONTAL);
		bannerParameters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		// Add the banner to the main layout
		mainLayout.addView(startAppBanner, bannerParameters);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.second, menu);
		return true;
	}
	
	/**
	 * Part of the activity's life cycle, StartAppAd should be integrated here.
	 */
	@Override
	public void onResume(){
		super.onResume();
		startAppAd.onResume();
	}
	
	/**
	 * Part of the activity's life cycle, StartAppAd should be integrated here
	 * for the home button exit ad integration.
	 */
	@Override
	public void onPause(){
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
