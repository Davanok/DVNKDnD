import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import kotlin.math.pow

val projectVersion = libs.versions.project.get()

plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.davanok.dvnkdnd"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    val versionCodeValue = projectVersion
        .split('.')
        .reversed()
        .withIndex()
        .sumOf { (index, value) ->
            100f.pow(index).toInt() * value.toInt()
        }
    defaultConfig {
        applicationId = "com.davanok.dvnkdnd"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = versionCodeValue
        versionName = projectVersion
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
}

dependencies {
    implementation(project(":composeApp"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.ui.tooling)
}
