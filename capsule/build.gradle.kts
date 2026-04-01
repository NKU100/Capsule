plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("com.android.kotlin.multiplatform.library")
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    id("com.vanniktech.maven.publish")
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
kotlin {
    androidLibrary {
        namespace = "com.kyant.capsule"
        compileSdk = 36
        minSdk = 21
    }
    jvm()
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
        }
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates("io.github.kyant0", "capsule", "2.2.0")

    pom {
        name.set("Capsule")
        description.set("Compose Multiplatform smooth corners")
        inceptionYear.set("2025")
        url.set("https://github.com/Kyant0/Capsule")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("Kyant0")
                name.set("Kyant")
                url.set("https://github.com/Kyant0")
            }
        }
        scm {
            url.set("https://github.com/Kyant0/Capsule")
            connection.set("scm:git:git://github.com/Kyant0/Capsule.git")
            developerConnection.set("scm:git:ssh://git@github.com/Kyant0/Capsule.git")
        }
    }
}
