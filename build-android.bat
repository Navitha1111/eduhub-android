@echo off
setlocal enabledelayedexpansion

echo ======================================================================
echo    EDUHUB ANDROID EDITION (KOTLIN + JETPACK COMPOSE MATERIAL 3)
echo    100%% Free 0-to-Advanced Global Self-Study Platform
echo ======================================================================
echo.

if exist "%ANDROID_HOME%" (
    echo [INFO] Detected ANDROID_HOME at %ANDROID_HOME%
) else if exist "%LOCALAPPDATA%\Android\Sdk" (
    set "ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk"
    echo [INFO] Detected Android SDK at !ANDROID_HOME!
) else (
    echo [INFO] Android SDK path can be set via Android Studio or ANDROID_HOME.
)

echo.
echo ======================================================================
echo  How to run this Android Project:
echo  1. Launch Android Studio (Ladybug, Koala, Meerkat or newer)
echo  2. Click "Open" and select this directory:
echo     %~dp0
echo  3. Let Gradle sync and download dependencies.
echo  4. Connect an Android phone or launch an Emulator (minSdk 26).
echo  5. Click Run (Shift + F10) to enjoy the full free 0-to-Advanced app!
echo ======================================================================
echo.
pause
