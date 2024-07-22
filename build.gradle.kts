buildscript {
    dependencies {
        classpath(libs.google.services)
        classpath("com.thoughtbot:expandablerecyclerview:1.4")
        classpath("com.thoughtbot:expandablecheckrecyclerview:1.4")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
}