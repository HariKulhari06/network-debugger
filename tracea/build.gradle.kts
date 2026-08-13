import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

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
        val xcf = XCFramework("Tracea")
        iosX64 {
            binaries.framework {
                baseName = "Tracea"
                xcf.add(this)
            }
        }
        iosArm64 {
            binaries.framework {
                baseName = "Tracea"
                xcf.add(this)
            }
        }
        iosSimulatorArm64 {
            binaries.framework {
                baseName = "Tracea"
                xcf.add(this)
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":tracea-core"))
            api(project(":tracea-manual"))
            api(project(":tracea-storage"))
            implementation(libs.coroutines.core)
        }
        androidMain.dependencies {
            api(project(":tracea-okhttp"))
            api(project(":tracea-ui"))
            implementation(libs.core.ktx)
            implementation(libs.okhttp)
            implementation(libs.coroutines.android)
        }
    }
}

android {
    namespace = "com.hari.tracea"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
