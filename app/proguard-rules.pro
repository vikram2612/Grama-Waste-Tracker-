# Add rules for Firebase, Maps, Hilt
-keepattributes Signature
-keepattributes *Annotation*

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Gson
-keep class com.grama.wastetracker.data.model.** { *; }
-keepclassmembers class com.grama.wastetracker.data.model.** { *; }

# Hilt
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }
