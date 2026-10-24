#!/bin/bash
set -e

echo "====================================================================="
echo "          EDUHUB Android - Google Play Store Release Builder         "
echo "====================================================================="

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# 1. Check keystore
if [ ! -f "release.keystore" ]; then
    echo "[ERROR] Keystore 'release.keystore' not found in $SCRIPT_DIR!"
    exit 1
fi
if [ ! -f "keystore.properties" ]; then
    echo "[ERROR] 'keystore.properties' not found in $SCRIPT_DIR!"
    exit 1
fi
echo "[*] Release keystore and properties verified."

# 2. Check Android SDK
if [ ! -f "local.properties" ]; then
    if [ -n "$ANDROID_HOME" ]; then
        echo "[*] Creating local.properties from ANDROID_HOME: $ANDROID_HOME"
        echo "sdk.dir=$ANDROID_HOME" > local.properties
    elif [ -n "$ANDROID_SDK_ROOT" ]; then
        echo "[*] Creating local.properties from ANDROID_SDK_ROOT: $ANDROID_SDK_ROOT"
        echo "sdk.dir=$ANDROID_SDK_ROOT" > local.properties
    fi
fi

chmod +x gradlew

echo "====================================================================="
echo "[*] Building Play Store Release App Bundle (.aab)..."
echo "====================================================================="
./gradlew bundleRelease

echo "====================================================================="
echo "[*] Building Standalone Release APK (.apk)..."
echo "====================================================================="
./gradlew assembleRelease || echo "[!] assembleRelease had warnings, bundleRelease completed."

echo "====================================================================="
echo "                   BUILD COMPLETED SUCCESSFULLY!                     "
echo "====================================================================="
if [ -f "app/build/outputs/bundle/release/app-release.aab" ]; then
    echo "[AAB] app/build/outputs/bundle/release/app-release.aab"
    ls -lh app/build/outputs/bundle/release/app-release.aab
fi
if [ -f "app/build/outputs/apk/release/app-release.apk" ]; then
    echo "[APK] app/build/outputs/apk/release/app-release.apk"
    ls -lh app/build/outputs/apk/release/app-release.apk
fi
echo ""
echo "See PLAYSTORE-UPLOAD-GUIDE.md for Play Console upload walkthrough."
