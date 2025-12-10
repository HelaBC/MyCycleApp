plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "tn.rnu.isi.mycycle"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "tn.rnu.isi.mycycle"
        minSdk = 36
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    
    // AndroidX core - provides AccessibilityDelegateCompat and other support classes
    implementation("androidx.core:core:1.12.0")
    implementation("androidx.core:core-ktx:1.12.0")
    
    // MaterialCalendarView - exclude old support libs
    implementation("com.github.prolificinteractive:material-calendarview:2.0.0") {
        exclude(group = "com.android.support", module = "support-v4")
        exclude(group = "com.android.support", module = "support-compat")
        exclude(group = "com.android.support", module = "support-annotations")
    }
    
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.6")
    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}