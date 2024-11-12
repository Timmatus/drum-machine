plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}



android {
    namespace = "com.mgke.drummachine"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mgke.drummachine"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    runtimeOnly("com.cloudinary:cloudinary-android-core:3.0.2")
    implementation("com.cloudinary:cloudinary-android-preprocess:3.0.2")
    implementation("com.cloudinary:cloudinary-android:3.0.2")
    implementation("com.cloudinary:cloudinary-android-ui:3.0.2")
    implementation ("com.github.bumptech.glide:glide:4.16.0")

}