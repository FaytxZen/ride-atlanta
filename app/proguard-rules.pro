# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\faytx\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

## ButterKnife Rules
# Retain generated class which implement Unbinder.
-keep public class * implements butterknife.Unbinder { public <init>(**, android.view.View); }

# Prevent obfuscation of types which use ButterKnife annotations since the simple name
# is used to reflectively look up the generated ViewBinding.
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }

## OkHttp
-dontnote okhttp3.internal.Platform
-dontnote com.squareup.okhttp.internal.Platform
-dontwarn okio.**
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault

## Okio
# java.nio.file.* usage which cannot be used at runtime. Animal sniffer annotation.
-dontwarn okio.Okio
# JDK 7-only method which is @hide on Android. Animal sniffer annotation.
-dontwarn okio.DeflaterSink

## Kotlin
-keepattributes *Annotation*

-keep class kotlin.** { *; }
-keep class org.jetbrains.** { *; }

## Kotlin Reflect
-dontwarn kotlin.reflect.jvm.internal.**

# Retrofit 2.X
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

## Project
-keep class com.andrewvora.apps.rideatlanta.** { *; }
-dontwarn com.google.errorprone.annotations.**
-dontnote org.apache.http.**
-dontnote android.net.http.*