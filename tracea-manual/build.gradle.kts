plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
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
            implementation(project(":tracea-core"))
            implementation(libs.coroutines.core)
        }
        androidMain.dependencies {
            implementation(libs.coroutines.android)
        }
        commonTest.dependencies {
            implementation(libs.junit)
        }
    }
}

android {
    namespace = "com.hari.tracea.manual"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
