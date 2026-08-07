# Add project-specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\buggm\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.kts.

# Keep kotlinx.serialization classes
-keepattributes *Annotation*, EnclosingMethod, Signature
-keep,allowobfuscation,allowshrinking class kotlinx.serialization.json.JsonObject { *; }
-keepclassmembers class com.solerforge.lumeria.data.** {
    *** Companion;
    *** $serializer;
}

# Keep PlayerData specifically as it is critical for saves
-keep class com.solerforge.lumeria.data.PlayerData { *; }
-keep class com.solerforge.lumeria.data.Quest { *; }

# Keep data classes in models and database if accessed via reflection
-keep class com.solerforge.lumeria.models.** { *; }
-keep class com.solerforge.lumeria.database.** { *; }
