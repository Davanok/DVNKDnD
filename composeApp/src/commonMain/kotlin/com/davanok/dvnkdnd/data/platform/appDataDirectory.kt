package com.davanok.dvnkdnd.data.platform

import okio.Path

expect fun appDataDirectory(): Path
expect fun appCacheDirectory(): Path
