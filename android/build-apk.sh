#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
python3 tools/build_mythos_corpus.py
cd android
./gradlew --no-daemon assembleDebug
echo "APK: android/app/build/outputs/apk/debug/app-debug.apk"
