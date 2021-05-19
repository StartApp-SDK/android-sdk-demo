# [StartApp][1] SDK Example

This project provides an example of the [StartApp SDK integration for Android][2].

The example application contains the following ads:

- Banner
- Interstitial ad
- Rewarded video
- RecyclerView with Banner
- RecyclerView with NativeAd

## Add dependency

[**app/build.gradle**](app/build.gradle#L16)

```groovy
repositories {
    mavenCentral()
}

dependencies {
    // noinspection GradleDynamicVersion
    implementation 'com.startapp:inapp-sdk:4.8.+'
}
```

## Update [AndroidManifest.xml](app/src/main/AndroidManifest.xml#L21)

```xml
<!-- Add these permissions for better ad targeting -->
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.BLUETOOTH" />

<application>
    <!-- TODO replace YOUR_APP_ID with actual value -->
    <meta-data
        android:name="com.startapp.sdk.APPLICATION_ID"
        android:value="YOUR_APP_ID" />
</application>
```

## Set up test ad

[**MainActivity.java**](app/src/main/java/com/example/app/MainActivity.java#L24)

```java
@Override
protected void onCreate(Bundle state) {
    super.onCreate(state);

    // NOTE always use test ads during development and testing
    StartAppSDK.setTestAdsEnabled(BuildConfig.DEBUG);

    setContentView(R.layout.main);
}
```

## Add Banner into [xml layout](app/src/main/res/layout/main.xml#L52)

```xml
<com.startapp.sdk.ads.banner.Banner
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```

## [Show interstitial ad](app/src/main/java/com/example/app/MainActivity.java#L32)

```java
public void someMethod() {
    // start your next activity
    startActivity(new Intent(this, OtherActivity.class));

    // and show interstitial ad
    StartAppAd.showAd(this);
}
```

## [Show rewarded video](app/src/main/java/com/example/app/MainActivity.java#L36)

```java
public void showRewardedVideo() {
    final StartAppAd rewardedVideo = new StartAppAd(this);

    rewardedVideo.setVideoListener(new VideoListener() {
        @Override
        public void onVideoCompleted() {
            // Grant the reward to user
        }
    });

    rewardedVideo.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
        @Override
        public void onReceiveAd(Ad ad) {
            rewardedVideo.showAd();
        }

        @Override
        public void onFailedToReceiveAd(Ad ad) {
            // Can't show rewarded video
        }
    });
}
```

For any question or assistance, please contact us at support@start.io

 [1]: https://start.io
 [2]: https://support.start.io/hc/en-us/articles/360014774799
