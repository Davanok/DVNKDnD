package com.davanok.dvnkdnd.data.platform

object JvmPlatform : Platform {
    override val name: String = "JVM"
    override val version: String = System.getProperty("java.version") ?: "Unknown"
    override val model: String = System.getProperty("os.name") ?: "Desktop"
    override val extra: Map<String, String> = emptyMap()
}

actual fun Platform.Companion.currentPlatform(): Platform = JvmPlatform