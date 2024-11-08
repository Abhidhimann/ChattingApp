# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interfacapksigner verify path/to/your-release.apke.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# Add this global rule
-keepattributes Signature

# This rule will properly ProGuard all the model classes in
# the package com.example.models. Modify to fit the structure
# of your app.
-keepclassmembers class com.example.chattingApp.data.remote.dto.** {
  *;
}
# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
 -keep,allowobfuscation,allowshrinking interface retrofit2.Call
 -keep,allowobfuscation,allowshrinking class retrofit2.Response

 # With R8 full mode generic signatures are stripped for classes that are not
 # kept. Suspend functions are wrapped in continuations where the type argument
 # is used.
 -keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
 # Remove all Log calls in release build
 -assumenosideeffects class android.util.Log {
     public static *** d(...);
     public static *** v(...);
     public static *** i(...);
     public static *** w(...);
     public static *** e(...);
 }
