# [Start.io](https://start.io) Android SDK Demo

This project provides an example of the [Start.io SDK integration for Android](https://support.start.io/hc/en-us/articles/360014774799).

Legacy example project is available in branch [legacy](https://github.com/StartApp-SDK/android-sdk-demo/tree/legacy).

## Project structure

- [app-compose](app-compose) - contains a demo project with Kotlin 2.0, [Flows](https://developer.android.com/kotlin/flow) and [Jetpack Compose](https://developer.android.com/develop/ui/compose/documentation).
- [app-java](app-java) - contains a demo project with Java, [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) and [XML layout](https://developer.android.com/develop/ui/views/layout/declaring-layout).

## Ad formats

- Interstitial ad
- Rewarded video ad
- Banner ad
- Native ad

## Gradle dependency

```groovy
dependencies {
    // noinspection GradleDynamicVersion
    implementation 'com.startapp:inapp-sdk:5.+'
}
```

## SDK initialization

In the examples below make sure to use your own App ID from the [portal.start.io](https://portal.start.io).

For testing purposes you can use demo App ID: `205489527`.

**Kotlin**

```kotlin
StartAppSDK.initParams(applicationContext, "YOUR_APP_ID")
    .setReturnAdsEnabled(false)
    .setCallback { /* ready to request ads */ }
    .init()
```

**Java**

```java
StartAppSDK.initParams(this, "YOUR_APP_ID")
    .setReturnAdsEnabled(false)
    .setCallback(new Runnable() {
        public void run () {
            // ready to request ads        
        }
    })
    .init();
```

## Edit AndroidManifest.xml

Add the following permissions for better ad targeting.

```xml
<manifest>
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.BLUETOOTH" />
</manifest>
```

## Set up test ad

Always use test ads during development and testing.

**Kotlin**

```kotlin
// TODO make sure to remove this line in production
StartAppSDK.setTestAdsEnabled(true)
```

**Java**

```java
// TODO make sure to remove this line in production
StartAppSDK.setTestAdsEnabled(true);
```

## Interstitial ad

Create an instance of `StartAppAd`, then call `loadAd()` with the `AdEventListener`.

Then only after `onReceiveAd()` has been called, it's safe to call `interstitialAd.showAd()`.

**Kotlin**

```kotlin
fun loadInterstittialAd() {
    StartAppAd(this).let { interstitialAd ->
        interstitialAd.loadAd(object : AdEventListener {
            override fun onReceiveAd(ad: Ad) {
                // TODO save interstitialAd somewehre, then call interstitialAd.showAd()
            }
    
            override fun onFailedToReceiveAd(ad: Ad?) {
                // TODO handle error
            }
        })
    }
}
```

**Java**

```java
void loadInterstitialAd() {
    StartAppAd interstitialAd = new StartAppAd(this);
    interstitialAd.loadAd(new AdEventListener() {
        @Override
        public void onReceiveAd (@NonNull Ad ad){
            // TODO save interstitialAd somewehre, then call interstitialAd.showAd()
        }
    
        @Override
        public void onFailedToReceiveAd (@Nullable Ad ad){
            // TODO handle error
        }
    });
}
```

## Rewarded Video ad

Same as Interstitial ad, but the parameter `AdMode.REWARDED_VIDEO` is required for method `loadAd()`.

**Kotlin**

```kotlin
fun loadRewardedVideoAd() {
    StartAppAd(this).let { rewardedAd ->
        rewardedAd.loadAd(AdMode.REWARDED_VIDEO, object : AdEventListener {
            override fun onReceiveAd(ad: Ad) {
                // TODO save rewardedAd somewehre, then call rewardedAd.showAd()
            }

            override fun onFailedToReceiveAd(ad: Ad?) {
                // TODO handle error
            }
        })
    }
}
```

**Java**

```java
void loadRewardedVideoAd() {
    StartAppAd rewardedAd = new StartAppAd(this);
    rewardedAd.loadAd(AdMode.REWARDED_VIDEO, new AdEventListener() {
        @Override
        public void onReceiveAd(@NonNull Ad ad) {
            // TODO save rewardedAd somewehre, then call rewardedAd.showAd()
        }

        @Override
        public void onFailedToReceiveAd(@Nullable Ad ad) {
            // TODO handle error
        }
    });
}
```

Then you must implement `VideoListener` callback before calling `showAd()` in order to reward a user once the ad has been viewed.

**Kotlin**

```kotlin
fun showRewardedVideoAd() {
    rewardedAd.setVideoListener {
        // TODO user gained a reward
    }

    rewardedAd.showAd()
}
```

**Java**

```java
void showRewardedVideoAd() {
    rewardedAd.setVideoListener(new VideoListener() {
        @Override
        public void onVideoCompleted() {
            // TODO user gained a reward
        }
    });

    rewardedAd.showAd();
}
```

## Banner ad

### The easiest integration

**Kotlin Compose**

```kotlin
import com.startapp.sdk.ads.banner.Banner

// ...

@Composable
fun BannerAdView() {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = ::Banner,
    )
}
```

**XML layout**

```xml
<com.startapp.sdk.ads.banner.Banner
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```

### Advanced integration

This method is well optimized from the resources usage perspective and it's recommended to use.

**Kotlin**

```kotlin
fun loadBanner() {
    BannerRequest(applicationContext)
        .setAdFormat(BannerFormat.BANNER)
        .load { creator: BannerCreator?, error: String? ->
            if (creator != null) {
                val adView = creator.create(applicationContext, null)
                // TODO save the adView and refresh the compose UI
            } else {
                // TODO handle error
            }
        }
}

@Composable
fun AdView() {
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { adView },
    )
}
```

**Java**

```java
void loadBanner() {
    new BannerRequest(getApplicationContext())
        .setAdFormat(BannerFormat.BANNER)
        .load((creator, error) -> {
            if (creator != null) {
                View adView = creator.create(getApplicationContext(), null);
                
                // TODO add to ViewGroup
                ViewGroup container = findViewById(R.id.banner_container);
                container.addView(adView);
            } else {
                // TODO handle error
            }
        });
}
```

## Native ad

**Kotlin**

```kotlin
fun loadNativeAd() {
    val adPreferences = NativeAdPreferences()
    adPreferences.setAutoBitmapDownload(true)

    val nativeAd = StartAppNativeAd(applicationContext)
    nativeAd.setPreferences(adPreferences)
    nativeAd.loadAd(object : AdEventListener {
        override fun onReceiveAd(ad: Ad) {
            val nativeAds = nativeAd.nativeAds
            if (nativeAds != null && nativeAds.isNotEmpty()) {
                val nativeAdDetails = nativeAds[0]
                if (nativeAdDetails != null) {
                    // TODO save the nativeAdDetails and refresh the compose UI
                } else {
                    // TODO handle error
                }
            } else {
                // TODO handle error
            }
        }

        override fun onFailedToReceiveAd(ad: Ad?) {
            // TODO handle error
        }
    })
}
```

See complete example of [NativeAdLayout](app-compose/src/main/kotlin/com/startapp/demo/compose/NativeAdLayout.kt) for more details.

```kotlin
@Composable
fun NativeAdLayout(ad: NativeAdDetails) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            // NOTE extremely important to specify .height(IntrinsicSize.Max) in order to make the native ad clickable
            .height(IntrinsicSize.Max)
    ) {
        // TODO build compose layout of ad

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                val result = View(context)

                // NOTE mandatory call of registerViewForInteraction()
                ad.registerViewForInteraction(result)
                return@AndroidView result
            },
        )
    }
}
```

**Java**

```java
private void loadNative(@NonNull View view) {
    NativeAdPreferences adPreferences = new NativeAdPreferences();
    adPreferences.setAutoBitmapDownload(true);

    StartAppNativeAd nativeAd = new StartAppNativeAd(getApplicationContext());
    nativeAd.setPreferences(adPreferences);
    nativeAd.loadAd(new AdEventListener() {
        @Override
        public void onReceiveAd(@NonNull Ad ad) {
            ArrayList<NativeAdDetails> nativeAds = nativeAd.getNativeAds();
            if (nativeAds != null && !nativeAds.isEmpty()) {
                NativeAdDetails nativeAdDetails = nativeAds.get(0);
                if (nativeAdDetails != null) {
                    View adView = /* TODO create a view */;

                    // TODO bind the data from nativeAdDetails to the adView

                    // NOTE mandatory call of registerViewForInteraction()
                    nativeAdDetails.registerViewForInteraction(adView);

                    ViewGroup container = /* TODO get reference to a ViewGroup */;

                    // NOTE ad view to the layout
                    container.addView(adView);
                } else {
                    // TODO handle error
                }
            } else {
                // TODO handle error
            }
        }

        @Override
        public void onFailedToReceiveAd(@Nullable Ad ad) {
            // TODO handle error
        }
    });
}
```

## Support

Visit [support.start.io](https://support.start.io) to access the full documentation.

For any question or assistance, please contact us at [support@start.io](mailto:support@start.io).
