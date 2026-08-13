plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    `maven-publish`
}

val isXcodeAvailable: Boolean by lazy {
    try {
        val process = Runtime.getRuntime().exec(arrayOf("xcodebuild", "-version"))
        process.waitFor() == 0
    } catch (e: Exception) {
        false
    }
}

kotlin {
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    
    if (isXcodeAvailable) {
        iosX64()
        iosArm64()
        iosSimulatorArm64()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.coroutines.core)
            implementation(libs.serialization.json)
        }
        androidMain.dependencies {
            implementation(libs.coroutines.android)
            implementation(libs.core.ktx)
        }
        commonTest.dependencies {
            implementation(libs.junit)
        }
    }
}

android {
    namespace = "com.hari.tracea.core"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
