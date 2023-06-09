# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class com.kakao.sdk.**.model.* { <fields>; }
-keep class * extends com.google.gson.TypeAdapter
-keep class com.google.googlesignin.** { *; }
-keep class com.google.firebase.** { *; }
-keep class com.google.firebase.auth.** { *; }
-keep class com.google.firebase.database.** { *; }
-keep class com.google.firebase.firestore.** { *; }
-keep class com.google.firebase.messaging.** { *; }
-keep class com.google.firebase.storage.** { *; }
-keepattributes InnerClasses
-keepclassmembers class com.google.firebase.database.** {
    *;
}
-keepclassmembers class com.abtwin.banlancegameex.** {
    *;
}
-keepattributes Signature