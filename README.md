[StartApp][] InApp SDK Example Project
======================================

*Updated to InApp SDK version 2.1.1*

This Android application project provides an example of the [StartApp][] InApp SDK integration.
It is compiled with Android 2.2 (API level 8) and supports any device running this Android version or higher.

The example application contains the following ads:
* Splash ad (when entering the application)
* Interstitial ad when navigating from the MainActivity to the SecondActivity by clicking the "Next Activity" button
* Banners - the application contains an integration of the banner both in the layout XML file ([/res/layout/activity_main.xml](/res/layout/activity_main.xml)) and programatically ([/src/com/startapp/startappinterstitialexample/SecondActivity.java](/src/com/startapp/startappinterstitialexample/SecondActivity.java))
* Exit ad for exiting the application with the back button
* Exit ad for exiting the application with the home button

When integrating the SDK with your application, please make sure to use the latest SDK version, which can be downloaded from our [developers portal](https://developers.startapp.com).
Please also follow the integration manual which comes with the SDK.
Don't forget to use your developers id and application id when initializing the SDK.


For any question or assistance, please contact us at support@startapp.com.

[StartApp]: http://www.startapp.com