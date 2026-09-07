plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.idevsu.mythos"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.idevsu.mythos.hacktheworld"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}
tasks.register("prepareMythosCorpus") {
    doLast {
        val root = project.projectDir.parentFile.parentFile
        exec { commandLine("python3", File(root, "tools/build_mythos_corpus.py").absolutePath) }
    }
}
tasks.named("preBuild").configure { dependsOn("prepareMythosCorpus") }
dependencies {
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
}
