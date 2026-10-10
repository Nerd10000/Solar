# ============================================================
# Solar - ProGuard
# ============================================================

-dontshrink
-dontoptimize
-dontwarn

# Paper plugin entry point
-keep public class dragon.me.solar.Solar {
    public <init>();
    public void onEnable();
    public void onDisable();
}
-keep class dragon.me.solar.configs.** {
    <fields>;
    <methods>;
    <init>();
}
-keep class dragon.me.solar.database.** {
    <fields>;
    <methods>;
    <init>();
}

-keep class dragon.me.solar.api.** {
    <fields>;
    <methods>;
    <init>();
}

-keep enum dragon.me.solar.commands.args.queues.QueueType {
    <fields>;
    <methods>;
    <init>();
}


# =====
# ============================================================
# Keep shaded dependencies unchanged
# ============================================================

-keep class org.spongepowered.** { *; }
-keep class org.incendo.** { *; }
-keep class com.j256.ormlite.** { *; }
-keep class com.github.benmanes.caffeine.** { *; }
-keep class com.github.benmanes.caffeine.cache.** { *; }

# Runtime metadata
-keepattributes RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations,RuntimeInvisibleParameterAnnotations
-keepattributes AnnotationDefault
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes SourceFile,LineNumberTable
-keepattributes Record
-keepattributes MethodParameters

# Obfuscation
-repackageclasses 'dragon.me.solar'
-allowaccessmodification

# Optimizations
-mergeinterfacesaggressively
-optimizeaggressively


# =========================
# Optimization
# =========================

-optimizationpasses 5
-allowaccessmodification

# Don't optimize these packages initially
-keep,allowoptimization class dragon.me.solar.Solar {
    public <init>();
}
