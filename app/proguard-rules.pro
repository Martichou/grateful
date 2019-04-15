# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

-keepattributes Signature

-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

-keep class androidx.appcompat.widget.** { *; }
-keep public class * extends androidx.preference. { *; }
-keep class com.google.android.material.textfield.** { *; }

-keepclassmembers class me.martichou.be.grateful.data.model.** { *; }

-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

