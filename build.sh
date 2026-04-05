#!/bin/bash

# Pomodoro App Build Script
# Usage: ./build.sh [debug|release]

set -e

# Configuration
export ANDROID_HOME="${ANDROID_HOME:-$HOME/Android/Sdk}"
export JAVA_HOME="${JAVA_HOME:-/usr/lib/jvm/bellsoft-java17-amd64}"

BUILD_TYPE="${1:-debug}"
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "==================================="
echo "Pomodoro App Build Script"
echo "==================================="
echo "Build type: $BUILD_TYPE"
echo "ANDROID_HOME: $ANDROID_HOME"
echo "JAVA_HOME: $JAVA_HOME"
echo ""

cd "$PROJECT_DIR"

if [ "$BUILD_TYPE" == "release" ]; then
    echo "Building release APK..."
    ./gradlew assembleRelease
    APK_PATH="app/build/outputs/apk/release/app-release-unsigned.apk"
else
    echo "Building debug APK..."
    ./gradlew assembleDebug
    APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
fi

if [ -f "$APK_PATH" ]; then
    echo ""
    echo "==================================="
    echo "Build successful!"
    echo "APK location: $PROJECT_DIR/$APK_PATH"
    echo "==================================="
    
    # Show APK info
    ls -lh "$APK_PATH"
else
    echo "Build failed! APK not found."
    exit 1
fi
