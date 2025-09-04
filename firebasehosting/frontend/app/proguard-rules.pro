# Production ProGuard rules for Play Store release

# Keep essential classes for notifications and services
-keep class com.example.m.services.** { *; }
-keep class com.example.m.receivers.** { *; }
-keep class com.example.m.utils.** { *; }

# Keep Firebase classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Keep Retrofit and API classes
-keep class com.example.m.api.** { *; }
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# Keep essential Android components
-keep class androidx.appcompat.** { *; }
-keep class androidx.core.** { *; }
-keep class androidx.cardview.** { *; }
-keep class androidx.recyclerview.** { *; }
-keep class androidx.swiperefreshlayout.** { *; }

# Keep custom views and adapters
-keep class com.example.m.adapters.** { *; }

# Keep string resources
-keep class **.R$string { *; }

# Optimize and obfuscate
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification

# Remove logging in production
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Keep line numbers for crash reporting
-keepattributes SourceFile,LineNumberTable

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep enum values
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Serializable classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}