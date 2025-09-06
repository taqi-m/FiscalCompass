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
# (Optional) If you use Paging with Room
####################################################################
-keep class androidx.paging.DataSource
-keep class androidx.paging.PositionalDataSource
