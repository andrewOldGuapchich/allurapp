plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.allur_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.allur_app"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("org.projectlombok:lombok:1.18.22")
    implementation("androidx.core:core:1.12.0")
    annotationProcessor("org.projectlombok:lombok:1.18.22")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.5")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.jakewharton.threetenabp:threetenabp:1.2.4")
}