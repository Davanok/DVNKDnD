import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.metro)
}

dependencies {
    implementation(project(":composeApp"))

    implementation(libs.metrox.viewmodel)
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "DVNKDnD"
            packageVersion = libs.versions.project.get()

            linux {
                iconFile.set(project.file("appIcons/LinuxIcon.png"))
                modules("jdk.security.auth")
            }
            windows {
                iconFile.set(project.file("appIcons/WindowsIcon.ico"))
            }
            macOS {
                iconFile.set(project.file("appIcons/MacosIcon.icns"))
                bundleID = "com.davanok.DVNKDnD.desktopApp"
            }
        }
    }
}
