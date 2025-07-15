plugins {
    id("com.android.application")
}

android {
    namespace = "com.startapp.demo.java"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.startapp.demo.java"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    // noinspection GradleDynamicVersion
    implementation("com.startapp:inapp-sdk:5.+")

    implementation("androidx.appcompat:appcompat:1.7.1")
}
