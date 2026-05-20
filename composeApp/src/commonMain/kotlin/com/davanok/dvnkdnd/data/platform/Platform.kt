package com.davanok.dvnkdnd.data.platform

interface Platform {
    val name: String
    val version: String
    val model: String
    val extra: Map<String, String>

    companion object
}

expect fun Platform.Companion.currentPlatform(): Platform