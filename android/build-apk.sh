#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"

if [[ ! -x ./gradlew ]]; then
  echo "Gradle wrapper executable is missing. Install Gradle once and run: gradle wrapper"
  exit 1
fi

./gradlew --no-daemon assembleDebug
printf '\nAPK ready: %s\n' "app/build/outputs/apk/debug/app-debug.apk"
