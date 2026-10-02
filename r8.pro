# ============================================================
# Solar R8 configuration
# ============================================================

# Keep the Paper plugin entry point.
-keep public class dragon.me.solar.Solar {
    public <init>();
}

# Keep runtime annotations.
-keepattributes *Annotation*

# Keep generic signatures.
-keepattributes Signature

# Keep nested-class information.
-keepattributes InnerClasses
-keepattributes EnclosingMethod