import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.serialization") version libs.versions.kotlin.get()
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
    alias(libs.plugins.buildConfig)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true

            linkerOpts.add("-lsqlite3")
        }
    }

    jvm("desktop")

    sourceSets {
        val desktopMain by getting
        commonMain.dependencies {
            implementation(libs.napier)

            implementation(libs.androidx.datastore.datastore)
            implementation(libs.androidx.datastore.preferences)

            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.viewmodel)
            implementation(libs.koin.navigation)

            implementation(libs.supabase.gotrue)
            implementation(libs.supabase.postgrest)
            implementation(libs.supabase.storage)

            implementation(libs.slf4j)

            implementation(libs.serialization.json)

            implementation(libs.ktor.core)

            implementation(libs.filekit.core)
            implementation(libs.filekit.compose)

//            implementation(libs.androidx.compose.material3.adaptive)
//            implementation(libs.androidx.compose.material3.adaptive.layout)
//            implementation(libs.androidx.compose.material3.adaptive.navigation)

            implementation(compose.material3AdaptiveNavigationSuite)
//            implementation(libs.window.size)

            implementation(libs.coil.compose)
            implementation(libs.coil.network)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
        }
        androidMain.dependencies {
            implementation(libs.androidx.window)
            implementation(libs.koin.android)

            implementation(libs.ktor.okhttp)
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        desktopMain.dependencies {
            implementation(libs.ktor.okhttp)
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
        iosMain.dependencies {
            implementation(libs.ktor.darwin)
        }
    }
}

android {
    namespace = "com.davanok.dvnkdnd"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.davanok.dvnkdnd"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
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

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "com.davanok.dvnkdnd.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.davanok.dvnkdnd"
            packageVersion = "1.0.0"

            linux {
                modules("jdk.security.auth")
            }
        }
    }
}

dependencies {
    listOf(
        "kspAndroid",
        "kspIosSimulatorArm64",
        "kspIosX64",
        "kspIosArm64",
        "kspDesktop"
    ).forEach {
        add(it, libs.androidx.room.compiler)
    }
    implementation(libs.androidx.ui.android)
    kspCommonMainMetadata(libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

buildConfig {
    val properties = Properties()
    packageName = "com.davanok.dvnkdnd"
    project.rootProject.file("config.properties").inputStream().use {
        properties.load(it)
    }
    buildConfigField("PACKAGE_NAME", packageName)
    buildConfigField("BUNDLE_ID", "com.davanok.dvnkdnd.DVNKDnD")
    properties.forEach { property ->
        buildConfigField(property.key.toString(), property.value.toString())
    }

}

