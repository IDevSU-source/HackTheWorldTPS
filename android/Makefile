.PHONY: apk

apk:
	@if [ ! -x ./gradlew ]; then echo "Gradle wrapper missing. Run: gradle wrapper --gradle-version 8.13"; exit 1; fi
	@./gradlew --no-daemon assembleDebug
	@echo "APK: app/build/outputs/apk/debug/app-debug.apk"
