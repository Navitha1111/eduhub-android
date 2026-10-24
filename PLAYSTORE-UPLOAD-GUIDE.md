# EDUHUB Android: Google Play Store Upload & Release Guide

This document provides a step-by-step walkthrough to build, sign, and publish **EDUHUB (`com.eduhub.android`)** to the Google Play Store.

---

## 1. Quick Release Summary

| Field | Setting / Value |
|---|---|
| **App Name** | `EDUHUB: Code & Learn 0-to-100` |
| **Package Name** | `com.eduhub.android` |
| **Version Code** | `1` |
| **Version Name** | `1.0.0` |
| **Target SDK** | `35` (Android 15 — exceeds Play Store minimum requirement of 34) |
| **Min SDK** | `26` (Android 8.0 Oreo — supports 95%+ of active devices globally) |
| **Keystore File** | `release.keystore` (PKCS12, 2048-bit RSA) |
| **Key Alias** | `eduhub` |
| **Release Artifact** | Android App Bundle: `app/build/outputs/bundle/release/app-release.aab` |
| **Local APK (Optional)** | Universal APK: `app/build/outputs/apk/release/app-release.apk` |

---

## 2. One-Click Build Instructions

### On Windows
Run the automated build script located in the project root:
```cmd
cd c:\software\eduhub3\rebuild\eduhub-android
build-playstore-release.bat
```
This script will:
1. Verify Java/JDK and Android SDK paths.
2. Verify `release.keystore` and `keystore.properties`.
3. Clean and build the signed **Android App Bundle (`.aab`)** for Play Store upload.
4. Clean and build the signed **Release APK (`.apk`)** for direct device installation or sideloading.
5. Place both artifacts ready for upload.

### Or Build Manually via Terminal
```bash
# Generate the signed App Bundle (.aab) for Google Play Console:
./gradlew bundleRelease

# Generate the signed APK (.apk) for direct testing:
./gradlew assembleRelease
```
Outputs:
- **Play Store Bundle (`.aab`):** `app/build/outputs/bundle/release/app-release.aab`
- **Device APK (`.apk`):** `app/build/outputs/apk/release/app-release.apk`

---

## 3. Step-by-Step Google Play Console Submission

### Step 3.1: Create App in Google Play Console
1. Log into your [Google Play Console](https://play.google.com/console).
2. Click **Create app** (top-right button).
3. Fill in:
   - **App name:** `EDUHUB: Code & Learn 0-to-100`
   - **Default language:** `English (United States) - en-US`
   - **App or game:** `App`
   - **Free or paid:** `Free`
4. Accept Developer Program Policies and US export laws, then click **Create app**.

---

### Step 3.2: Complete Policy & App Content Declarations
Under the left sidebar, navigate to **Policy > App content**. Complete each required questionnaire:

1. **Privacy Policy:**
   - Provide the public URL hosting `playstore/privacy-policy.html` (e.g., host on GitHub Pages, Cloudflare Pages, or Firebase Hosting).
   - Alternatively, you can use raw GitHub / static URL.
2. **Ads:**
   - Select **No, my app does not contain ads**.
3. **App Access:**
   - Select **All functionality is available without special access** (No login or credentials needed).
4. **Content Ratings:**
   - Click **Start questionnaire**.
   - Email: Enter your developer contact email.
   - Category: Select **Education**.
   - Violence, Sexual material, Offensive language, Drugs/Alcohol: Select **No** to all.
   - Shares user physical location or purchases digital goods: Select **No**.
   - Rating result: Will grant **Everyone (ESRB) / PEGI 3**. Click **Save** and **Apply rating**.
5. **Target Audience and Content:**
   - Target ages: Check **13-15**, **16-17**, and **18 and over**.
   - Appeal to children: Select **No**.
6. **Data Safety:**
   - Follow `playstore/data-safety-guide.md` exactly:
   - Does app collect or share data? Select **No**.
   - Deletion request? Select **Yes** (via device Settings > Clear Data).
7. **Financial Features & Government Apps:**
   - Financial features: Select **None of the above**.
   - Government app: Select **No**.

---

### Step 3.3: Set Up Store Listing & Assets
Under **Grow > Store presence > Main store listing**:

1. **Listing Details:**
   - Copy Title, Short Description, and Full Description from `playstore/store-listing.md`.
2. **Graphics:**
   - **App icon:** Upload `playstore/icon-512.png` (512x512 PNG, 32-bit).
   - **Feature graphic:** Upload `playstore/feature-graphic.png` (1024x500 PNG).
   - **Phone Screenshots:** Upload at least 2 to 8 screenshots of the running app (Curriculum screen, Interactive Quiz, Code Playground, Certificate screen).
   - **7-inch / 10-inch Tablet Screenshots:** (Optional but recommended for tablet discoverability).
3. **Categorization & Contact Details:**
   - App category: **Education**.
   - Tags: `Education`, `Programming`, `Computer Science`, `Productivity`.
   - Contact email: `support@eduhub.local` (or your personal/company email).

---

### Step 3.4: Upload Android App Bundle (`.aab`) & Publish
1. In the left navigation, choose your release track:
   - **Testing > Internal testing** (Recommended for immediate smoke testing on your phone), OR
   - **Release > Production** (Direct public rollout).
2. Click **Create new release**.
3. In **App bundles**, click **Upload** and choose:
   `app/build/outputs/bundle/release/app-release.aab`
4. Play Console will inspect the bundle:
   - Verifies target SDK 35
   - Reads version name `1.0.0` (code `1`)
   - Checks certificate signature against Play App Signing.
5. In **Release notes**, enter:
   ```txt
   Initial release of EDUHUB:
   • 39 Full Programming Language tracks from Beginner to Advanced
   • 100% Free & Offline-first with zero advertisements
   • Built-in interactive code playground and syntax runner
   • Progressive quizzes, coding challenges, and study notes
   • Verifiable Course Completion Certificates
   ```
6. Click **Next** > **Review release**.
7. If there are no blocking errors, click **Start rollout to Production** (or Internal Testing).

---

## 4. Keystore Security & Backup Reminder
- Keep `release.keystore` and `keystore.properties` backed up in a secure offline drive or password manager.
- Once an app is published to Google Play, the upload key **cannot be replaced** without contacting Google Play Developer Support.
- Passwords are saved in `keystore.properties` for automated CI/CD and local builds.
