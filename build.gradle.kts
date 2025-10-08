// このビルドスクリプトブロックをファイルの先頭に追加します。
// これにより、Gradleが 'com.google.gms.google-services' プラグインの
// 場所を見つけられるようになります。
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // google-services プラグインのクラスパスを定義
        classpath("com.google.gms:google-services:4.4.3")
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.gms.google-services") version "4.4.3" apply false
}
