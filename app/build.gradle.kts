plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.smartdispenser"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.smartdispenser"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // 引入MySQL JDBC驱动
    implementation("mysql:mysql-connector-java:5.1.46")
    // 引入Json库
    implementation("com.google.code.gson:gson:2.8.6")
    // 引入okHTTP
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    // 引入WebSocket
    // implementation("org.java-websocket:Java-WebSocket:1.5.3")

    // 引入animated-bottom-bar
    implementation("nl.joery.animatedbottombar:library:1.1.0")
    // 引入circular-progress-button
    implementation("com.github.dmytrodanylyk:circular-progress-button:1.4")
    // 引入room
    var room_version = "2.2.5"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    // 引入启动界面的splashscreen
    implementation ("androidx.core:core-splashscreen:1.0.1")
    //引入glide图片加载工具
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("com.github.yalantis:ucrop:2.2.6")
}