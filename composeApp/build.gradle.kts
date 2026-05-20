import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
    alias(libs.plugins.buildConfig)
    alias(libs.plugins.metro)
}

kotlin {
    android {
        namespace = "com.davanok.dvnkdnd"
        minSdk = libs.versions.android.minSdk.get().toInt()
        compileSdk = libs.versions.android.targetSdk.get().toInt()
        androidResources.enable = true
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvm()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        all {
            languageSettings.optIn("kotlin.uuid.ExperimentalUuidApi")
        }
        commonMain.dependencies {
            implementation(libs.bundles.navigation)

            implementation(libs.markdown.parser.core)
            implementation(libs.markdown.parser.m3)

            implementation(libs.toaster)

            implementation(libs.kermit)

            implementation(libs.androidx.datastore.datastore)
            implementation(libs.androidx.datastore.preferences)

            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)

            api(libs.metrox.viewmodel)
            api(libs.metrox.viewmodel.compose)

            implementation(libs.bundles.supabase)

            implementation(libs.serialization.json)

            implementation(libs.ktor.core)
            implementation(libs.ktor.cio)

            implementation(libs.bundles.filekit)

            implementation(libs.coil.compose)
            implementation(libs.coil.network)

            api(libs.bundles.compose)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(libs.androidx.paging.common)
            implementation(libs.androidx.paging.compose)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
        iosMain.dependencies {

        }
    }

    targets
        .withType<KotlinNativeTarget>()
        .matching { it.konanTarget.family.isAppleFamily }
        .configureEach {
            binaries {
                framework {
                    baseName = "ComposeApp"
                    isStatic = true

                    linkerOpts.add("-lsqlite3")
                }
            }
        }
    sourceSets.commonTest.dependencies {
        implementation(kotlin("test"))
    }
}

dependencies {
    listOf(
        "kspAndroid",
        "kspJvm",
        "kspIosSimulatorArm64",
        "kspIosX64",
        "kspIosArm64"
    ).forEach {
        add(it, libs.androidx.room.compiler)
    }
    kspCommonMainMetadata(libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

buildConfig {
    packageName = "com.davanok.dvnkdnd"
    buildConfigField("APP_VERSION_NAME", libs.versions.project.get())
    buildConfigField("PACKAGE_NAME", packageName)
    buildConfigField("BUNDLE_ID", "com.davanok.dvnkdnd.DVNKDnD")

    val properties = Properties()
    project.rootProject.file("config.properties").inputStream().use {
        properties.load(it)
    }
    properties.forEach { property ->
        buildConfigField(property.key.toString(), property.value.toString())
    }
}

compose {
    resources {
        generateResClass = always
    }
}