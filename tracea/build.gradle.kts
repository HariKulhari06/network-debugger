plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    `maven-publish`
}

kotlin {
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()

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
