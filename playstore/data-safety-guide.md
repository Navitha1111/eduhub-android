# Google Play Console: Data Safety Questionnaire Guide

When uploading **EDUHUB (`com.eduhub.android`)** to Google Play Console, you will be prompted to fill out the **Data safety** section under *Policy > Data safety*.

Follow the exact answers below to pass review on your first submission.

---

### Step 1: Data Collection and Security

| Question | Your Answer | Reason |
|---|---|---|
| **Does your app collect or share any of the required user data types?** | **No** | EduHub is 100% offline-first. No user data is collected, stored on external servers, or transmitted. |
| **Is all of the user data collected by your app encrypted in transit?** | *N/A (Auto-disabled when selecting "No" above)* | No network data collection. |
| **Do you provide a way for users to request that their data be deleted?** | **Yes** | Users can clear data at any time in Android Settings > Apps > EDUHUB > Clear Data, or by uninstalling. |

---

### Step 2: Specific Data Categories Check

If prompted to verify categories:
- **Location:** No
- **Personal info (Name, Email, Address, Phone, Race, Religion, etc.):** No
- **Financial info (Credit cards, bank details):** No
- **Health and fitness:** No
- **Messages (SMS, emails, chat):** No
- **Photos and videos:** No
- **Audio files:** No
- **Files and docs:** No
- **Calendar:** No
- **Contacts:** No
- **App activity (Page views, clicks, search history):** No
- **Web browsing:** No
- **App info and performance (Crash logs, diagnostics):** No
- **Device or other IDs (AAID, IMEI, MAC):** No

---

### Step 3: Target Audience and Content

| Question | Recommended Selection |
|---|---|
| **Target age groups** | Check **13-15**, **16-17**, and **18 and over** (Educational app suitable for all learners). |
| **Appeal to children** | Select **No** (unless you want to be listed in the Google Play Kids tab, which requires extra Teacher Approved review). |

---

### Step 4: Ads & Financial Features

| Question | Your Answer |
|---|---|
| **Does your app contain advertising?** | **No, my app does not contain ads** |
| **Does your app contain in-app purchases or subscriptions?** | **No** |
| **Financial features Declaration** | **None of the above** |

---

### Step 5: Government Apps / COVID-19 / Health

| Question | Your Answer |
|---|---|
| **Is your app developed by or on behalf of a government?** | **No** |
| **COVID-19 contact tracing or status?** | **No** |
| **Health and medical functionality?** | **No** |

---

### Step 6: App Access (Credentials for Reviewers)

| Question | Your Answer |
|---|---|
| **Do Google reviewers need credentials to test your app?** | **All functionality is available without special access** (No login or password needed). |

---

### Summary Checklist for Policy Review
- [x] Privacy Policy URL provided (e.g., your hosted `privacy-policy.html` on GitHub Pages).
- [x] Data safety answers match: **Zero data collected**.
- [x] Ads declaration: **No ads**.
- [x] App access: **No credentials required**.
