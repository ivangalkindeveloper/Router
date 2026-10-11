plugins { id("com.android.library"); kotlin("android"); kotlin("plugin.compose"); `maven-publish` }

android {
    namespace = "io.github.ivangalkindeveloper.router.compose"
    compileSdk = 36
    defaultConfig {
        minSdk = 23
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures { compose = true }
    publishing { singleVariant("release") { withSourcesJar() } }
    testOptions { unitTests.isIncludeAndroidResources = true }
}
kotlin {
    jvmToolchain(17)
    compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8) }
}

dependencies {
    testImplementation("androidx.test.espresso:espresso-core:3.7.0")
    api(platform("androidx.compose:compose-bom:2025.09.01"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.11.0")
    api(project(":router"))
    api("androidx.compose.runtime:runtime")
    api("androidx.compose.ui:ui")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.4")
    testImplementation(kotlin("test-junit"))
    testImplementation("org.robolectric:robolectric:4.16.1")
    testImplementation("androidx.test:core:1.7.0")
    testImplementation(platform("androidx.compose:compose-bom:2025.09.01"))
    testImplementation("androidx.compose.ui:ui-test-junit4")
    testImplementation("androidx.compose.ui:ui-test-manifest")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                artifactId = "router-compose"
                pom { name.set("router-compose"); description.set("Android Kotlin library") }
            }
        }
    }
}
