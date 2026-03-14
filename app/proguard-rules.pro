####################################################################
# Room Database (Entities, DAOs, Converters, Generated Classes)
####################################################################

# Keep all @Entity annotated classes (tables)
-keep @androidx.room.Entity class * { *; }

# Keep all @Dao interfaces
-keep @androidx.room.Dao class * { *; }

# Keep your RoomDatabase and its generated implementation
-keep class * extends androidx.room.RoomDatabase

# Keep all fields annotated with Room annotations
-keepclassmembers class * {
    @androidx.room.PrimaryKey <fields>;
    @androidx.room.ColumnInfo <fields>;
    @androidx.room.Embedded <fields>;
    @androidx.room.Relation <fields>;
}

# Keep all methods annotated with @TypeConverter
-keepclassmembers class * {
    @androidx.room.TypeConverter <methods>;
}

# Keep TypeConverters classes themselves
-keep @androidx.room.TypeConverters class * { *; }

####################################################################
# Kotlin Metadata (important for Room + coroutines)
####################################################################
-keepclassmembers class kotlin.Metadata {
    public <fields>;
}

####################################################################
# Keep annotations (Room uses reflection on them)
####################################################################
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses

####################################################################
# SQLite Support (needed by Room’s generated code)
####################################################################
-keep class androidx.sqlite.db.** { *; }
-keep interface androidx.sqlite.db.** { *; }

####################################################################
# Firebase Analytics & Crashlytics
####################################################################
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Firebase Crashlytics
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
-keep class com.google.firebase.crashlytics.** { *; }
-keepattributes *Annotation*

# Firebase Firestore
-keep class com.google.firebase.firestore.** { *; }
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <fields>;
    @com.google.firebase.firestore.PropertyName <methods>;
}

# Keep app Firestore DTO models and member names used by sync/parsing
-keep class com.fiscal.compass.data.remote.model.** { *; }
-keepclassmembernames class com.fiscal.compass.data.remote.model.** {
    <fields>;
    <methods>;
}

####################################################################
# Hilt (Dagger)
####################################################################
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ComponentSupplier { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Keep Hilt generated components
-keep class **_HiltModules { *; }
-keep class **_HiltModules$* { *; }
-keep class **_GeneratedInjector { *; }
-keep class **Hilt_* { *; }
-keep class *_Factory { *; }
-keep class *_MembersInjector { *; }

# Keep classes annotated with Hilt annotations
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }
-keep @dagger.Module class * { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }

# Dagger
-dontwarn dagger.internal.codegen.**
-keepclassmembers,allowobfuscation class * {
    @javax.inject.* *;
    @dagger.* *;
    <init>();
}
-keep class dagger.* { *; }
-keep class javax.inject.* { *; }

####################################################################
# Gson
####################################################################
-keepattributes Signature
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Keep model classes used with Gson (adjust package name as needed)
-keep class com.fiscal.compass.data.model.** { *; }
-keep class com.fiscal.compass.domain.model.** { *; }

####################################################################
# Jetpack Compose
####################################################################
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep Compose Runtime
-keep class androidx.compose.runtime.** { *; }

# Keep Compose UI
-keep class androidx.compose.ui.** { *; }

# Keep Compose Material3
-keep class androidx.compose.material3.** { *; }

# Keep Compose Animation
-keep class androidx.compose.animation.** { *; }

# Keep Compose Foundation
-keep class androidx.compose.foundation.** { *; }

# Keep @Composable functions metadata
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# Compose Navigation
-keep class androidx.navigation.** { *; }
-keep class androidx.hilt.navigation.compose.** { *; }

####################################################################
# Coil (Image Loading)
####################################################################
-keep class coil.** { *; }
-keep class coil3.** { *; }
-dontwarn coil.**
-dontwarn coil3.**

# OkHttp (used by Coil)
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep class okio.** { *; }

####################################################################
# DataStore Preferences
####################################################################
-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**
# Protobuf classes used internally by DataStore (if present)
-dontwarn com.google.protobuf.**

####################################################################
# Kotlin Coroutines
####################################################################
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

####################################################################
# Kotlin Serialization
####################################################################
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

# Keep @Serializable classes and their generated serializers
-keep @kotlinx.serialization.Serializable class * { *; }

-keepclassmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep the companion object serializer() method
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep serialization-related metadata
-keep class kotlinx.serialization.** { *; }
-dontwarn kotlinx.serialization.**

####################################################################
# AndroidX Lifecycle
####################################################################
-keep class androidx.lifecycle.** { *; }
-keepclassmembers class * implements androidx.lifecycle.LifecycleObserver {
    <init>(...);
}
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keepclassmembers class androidx.lifecycle.Lifecycle$State { *; }
-keepclassmembers class androidx.lifecycle.Lifecycle$Event { *; }

####################################################################
# Charts Library (io.github.dautovicharis:charts)
####################################################################
-keep class io.github.dautovicharis.charts.** { *; }
-dontwarn io.github.dautovicharis.charts.**

####################################################################
# General Android Rules
####################################################################
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

####################################################################
# R8 Full Mode Rules
####################################################################
-allowaccessmodification
-repackageclasses
