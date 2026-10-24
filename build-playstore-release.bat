@echo off
setlocal enabledelayedexpansion

echo =====================================================================
echo           EDUHUB Android - Google Play Store Release Builder
echo =====================================================================
echo.

cd /d "%~dp0"

:: 1. Check or configure Java JDK (Requires Java 17+)
if exist "C:\Program Files\Java\jdk-27" (
    echo [*] Found Modern JDK at C:\Program Files\Java\jdk-27
    set "JAVA_HOME=C:\Program Files\Java\jdk-27"
    set "PATH=%JAVA_HOME%\bin;%PATH%"
) else if defined JAVA_HOME (
    echo [*] Using system JAVA_HOME: %JAVA_HOME%
) else (
    echo [!] WARNING: JAVA_HOME is not set. Attempting to use system java...
)

echo [*] Java runtime check:
java -version
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Java runtime not found or failed. Please install JDK 17 or later.
    pause
    exit /b 1
)

echo.
:: 2. Check keystore files
if not exist "release.keystore" (
    echo [ERROR] Keystore 'release.keystore' not found in project root!
    pause
    exit /b 1
)
if not exist "keystore.properties" (
    echo [ERROR] 'keystore.properties' not found in project root!
    pause
    exit /b 1
)
echo [*] Release keystore and properties verified.

echo.
:: 3. Check for local.properties / Android SDK
if not exist "local.properties" (
    if defined ANDROID_HOME (
        echo [*] Writing local.properties from ANDROID_HOME: %ANDROID_HOME%
        echo sdk.dir=%ANDROID_HOME:\=\\%> local.properties
    ) else if defined ANDROID_SDK_ROOT (
        echo [*] Writing local.properties from ANDROID_SDK_ROOT: %ANDROID_SDK_ROOT%
        echo sdk.dir=%ANDROID_SDK_ROOT:\=\\%> local.properties
    ) else if exist "%LOCALAPPDATA%\Android\Sdk" (
        echo [*] Found default Android SDK at %LOCALAPPDATA%\Android\Sdk
        echo sdk.dir=%LOCALAPPDATA:\=\\%\\Android\\Sdk> local.properties
    ) else (
        echo [!] NOTE: Android SDK path not yet configured in local.properties.
        echo     If build fails with 'SDK location not found', create local.properties
        echo     with: sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
    )
)

echo.
echo =====================================================================
echo [*] Building Play Store Release App Bundle (.aab)...
echo =====================================================================
call gradlew.bat bundleRelease
if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] Gradle bundleRelease build failed. See logs above.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo =====================================================================
echo [*] Building Standalone Release APK (.apk)...
echo =====================================================================
call gradlew.bat assembleRelease
if %ERRORLEVEL% neq 0 (
    echo.
    echo [!] Note: assembleRelease had issues, but bundleRelease succeeded.
)

echo.
echo =====================================================================
echo                    BUILD COMPLETED SUCCESSFULLY!
echo =====================================================================
echo.
echo Upload this file to Google Play Console:
if exist "app\build\outputs\bundle\release\app-release.aab" (
    echo [AAB] app\build\outputs\bundle\release\app-release.aab
    dir "app\build\outputs\bundle\release\app-release.aab" | findstr app-release
) else (
    echo [!] app-release.aab was not found at expected location.
)
echo.
echo Universal testing APK:
if exist "app\build\outputs\apk\release\app-release.apk" (
    echo [APK] app\build\outputs\apk\release\app-release.apk
    dir "app\build\outputs\apk\release\app-release.apk" | findstr app-release
)

echo.
echo See PLAYSTORE-UPLOAD-GUIDE.md for complete step-by-step instructions.
echo.
pause
