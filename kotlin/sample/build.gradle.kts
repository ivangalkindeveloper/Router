plugins { id("com.android.application"); kotlin("android"); kotlin("plugin.compose") }

android {
    namespace = "io.ivangalkindeveloper.router.sample"
    compileSdk = 36
    defaultConfig {
        minSdk = 23
        applicationId = "io.ivangalkindeveloper.router.sample"
        targetSdk = 36
        versionCode = 2
        versionName = "2.0.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures { compose = true }
}
kotlin {
    jvmToolchain(17)
    compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8) }
}

dependencies {
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    implementation(platform("androidx.compose:compose-bom:2025.09.01"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.11.0")
    implementation(project(":router-compose"))
}
