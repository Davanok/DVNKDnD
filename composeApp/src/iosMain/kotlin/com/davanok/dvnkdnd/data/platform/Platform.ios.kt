package com.davanok.dvnkdnd.data.platform

import platform.UIKit.UIDevice

object IosPlatform : Platform {
    override val name: String = "iOS"
    override val version: String = UIDevice.currentDevice.systemVersion
    override val model: String = UIDevice.currentDevice.model
    override val extra: Map<String, String> = emptyMap()
}

actual fun Platform.Companion.currentPlatform(): Platform = IosPlatform