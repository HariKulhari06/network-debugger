pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "network-debugger"

include(":network-debugger-core")
include(":network-debugger-okhttp")
include(":network-debugger-manual")
include(":network-debugger-storage")
include(":network-debugger-ui")
include(":network-debugger")
include(":network-debugger-demo")
include(":network-debugger-kmp")
