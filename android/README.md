# TPS Android APK

This directory turns the TPS repository into a minimal native Android app.

## Build locally

Prerequisite: Android Studio or a JDK 17 + Android SDK environment.

From the repository root:

```bash
cd android
```

On a machine with Gradle installed once:

```bash
gradle wrapper --gradle-version 8.13
chmod +x gradlew
./gradlew assembleDebug
```

The APK is then:

```text
app/build/outputs/apk/debug/app-debug.apk
```

After the wrapper files exist, future builds are simply:

```bash
cd android
./gradlew assembleDebug
```

You can also run:

```bash
./build-apk.sh
```

## GitHub Actions

Every push that changes `android/**`, and every manual workflow dispatch, builds `app-debug.apk` and uploads it as the `tps-debug-apk` artifact.

## Current app

The first build is intentionally lean: it launches a native TPS reader shell with the project identity and boot sequence. The next layer can make the repository's Markdown chapters searchable and browsable inside the APK without changing the build system.
