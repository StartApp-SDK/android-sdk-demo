package com.startapp.startappinterstitialexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.RelativeLayout;

import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.banner.Banner;

public class SecondActivity extends Activity {
	
	/** StartAppAd object deceleration */
	private StartAppAd startAppAd = new StartAppAd(this);
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);
		
		// ****** Adding Banner Within JAVA Code *******
		RelativeLayout mainLayout = (RelativeLayout)findViewById(R.id.second_layout);
		
		// Define StartApp Banner
		Banner startAppBanner = new Banner(this);
		RelativeLayout.LayoutParams bannerParameters = 
				new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
		bannerParameters.addRule(RelativeLayout.CENTER_HORIZONTAL);
		bannerParameters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		// Add to main Layout
		mainLayout.addView(startAppBanner, bannerParameters);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.second, menu);
		return true;
	}
	
	/**
	 * 
	 * part of Activity life cycle
	 * Need as part of the StartAppAd object life cycle
	 */
	@Override
	public void onResume(){
		super.onResume();
		startAppAd.onResume();
	}
	
	/**
	 * part of Activity life cycle
	 * For Home button implementation
	 */
	@Override
	public void onPause(){
		super.onPause();
		startAppAd.onPause();
	}
	
	/**
	 * part of Activity life cycle
	 * For Back button implementation
	 */
	@Override
	public void onBackPressed() {
		startAppAd.onBackPressed();
		super.onBackPressed();
	}
}
