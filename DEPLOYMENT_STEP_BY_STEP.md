# SmartAttend AI - Step-by-Step Deployment Guide

## Complete Deployment Instructions

### **STEP 1: Download & Setup Project Locally**

#### 1.1 Install Prerequisites
```bash
# Install Git
# Download from: https://git-scm.com/download

# Install Android Studio
# Download from: https://developer.android.com/studio

# Install Java Development Kit (JDK)
# Required for Android development
```

#### 1.2 Clone the Repository
```bash
# Open Terminal/Command Prompt
# Navigate to your desired folder
cd Desktop  # or any folder you prefer

# Clone the project
git clone https://github.com/koremaniteja/face-attendance.git

# Navigate into project
cd face-attendance
```

---

### **STEP 2: Open in Android Studio**

#### 2.1 Launch Android Studio
- Open Android Studio application
- Click **"Open an existing Android Studio project"**
- Navigate to the **face-attendance** folder you just cloned
- Click **Open**

#### 2.2 Wait for Gradle Build
- Android Studio will automatically download dependencies
- Wait for "Build" to complete (5-10 minutes on first load)
- You'll see: **"Build successful"** message at the bottom

#### 2.3 Sync Project
```
File → Sync Now
```
Wait for sync to complete.

---

### **STEP 3: Configure Firebase (Important!)**

#### 3.1 Create Firebase Project
1. Go to: https://console.firebase.google.com
2. Click **"Create a project"**
3. Name it: `SmartAttend AI`
4. Click **Continue**
5. Enable Google Analytics (optional)
6. Click **Create project**
7. Wait 1-2 minutes for project creation

#### 3.2 Add Android App to Firebase
1. In Firebase Console, click **Android icon**
2. Enter Package Name: `com.smartattend.ai`
3. Enter App Nickname: `SmartAttend`
4. Click **Register app**
5. **Download `google-services.json`** file
6. Click **Next** through remaining steps

#### 3.3 Add google-services.json to Project
1. In Android Studio, locate **app** folder (left sidebar)
2. Drag & drop the downloaded `google-services.json` into **app** folder
3. You should see: `app/google-services.json`

#### 3.4 Enable Firebase Services
In Firebase Console:

**Enable Authentication:**
- Click **Build → Authentication**
- Click **Get Started**
- Enable **Email/Password** sign-in method
- Click **Save**

**Enable Firestore:**
- Click **Build → Firestore Database**
- Click **Create database**
- Select **Start in test mode**
- Choose location (closest to you)
- Click **Create**

**Enable Storage:**
- Click **Build → Storage**
- Click **Get started**
- Click **Done**

#### 3.5 Deploy Security Rules
```bash
# Install Firebase CLI
npm install -g firebase-tools

# Login to Firebase
firebase login

# Deploy rules from your project
firebase deploy --only firestore:rules,storage
```

---

### **STEP 4: Build & Test APK**

#### 4.1 Build Debug APK (Development Testing)
```bash
# In Terminal, from project root:
./gradlew :app:assembleDebug

# Or on Windows:
gradlew.bat :app:assembleDebug
```

Wait for build to complete. You'll see:
```
BUILD SUCCESSFUL
```

**APK Location:** 
```
app/build/outputs/apk/debug/app-debug.apk
```

#### 4.2 Connect Android Device
1. Enable **Developer Mode** on your phone:
   - Settings → About Phone → Tap "Build Number" 7 times
2. Enable **USB Debugging**:
   - Settings → Developer Options → USB Debugging → On
3. Connect phone to computer with USB cable
4. Tap **Allow** on phone when prompted

#### 4.3 Install APK on Device
```bash
# In Terminal:
adb install app/build/outputs/apk/debug/app-debug.apk

# Or use Android Studio:
# Click Run button (green play icon)
# Select your connected device
# Click OK
```

#### 4.4 Launch App
- On your device, find **SmartAttend** app
- Tap to open
- Sign in with test account (created in Firebase)
- Try face scanning!

---

### **STEP 5: Deploy to Google Play Store (Production)**

#### 5.1 Create Google Play Developer Account
1. Go to: https://play.google.com/console
2. Click **Create account** (costs $25 USD)
3. Complete payment & setup

#### 5.2 Build Release APK
```bash
# Generate signed release APK
./gradlew :app:bundleRelease

# Or on Windows:
gradlew.bat :app:bundleRelease
```

**Output:** `app/build/outputs/bundle/release/app-release.aab`

#### 5.3 Create App in Play Console
1. Go to: https://play.google.com/console
2. Click **Create app**
3. App name: `SmartAttend AI`
4. Category: Education
5. Content rating: Not restricted
6. Click **Create app**

#### 5.4 Upload App Bundle
1. Click **All apps → SmartAttend AI**
2. On left sidebar: **Release → Production**
3. Click **Create new release**
4. Upload `app-release.aab` file
5. Add release notes, version number
6. Click **Save → Review → Rollout to production**

#### 5.5 Submit for Review
1. Fill in app details:
   - Screenshots (at least 2)
   - App description (100-4000 characters)
   - Short description
   - Category
   - Content rating
2. Click **Save**
3. Click **Submit for review**
4. Wait 24-48 hours for approval

**Your App Link (after approval):**
```
https://play.google.com/store/apps/details?id=com.smartattend.ai
```

---

### **STEP 6: Share with Users (Alternative - Direct APK)**

#### 6.1 Create GitHub Release
```bash
# Tag the version
git tag -a v1.0.0 -m "Initial face recognition release"
git push origin v1.0.0
```

#### 6.2 Create Release on GitHub
1. Go to: https://github.com/koremaniteja/face-attendance/releases
2. Click **Create a new release**
3. Select tag: `v1.0.0`
4. Title: `SmartAttend AI v1.0.0 - Face Recognition`
5. Description: Copy from DEPLOYMENT.md
6. Upload `app-debug.apk` file
7. Click **Publish release**

**Share This Link:**
```
https://github.com/koremaniteja/face-attendance/releases/tag/v1.0.0
```

Users can download APK and install directly.

---

### **STEP 7: Test the App**

#### 7.1 Create Test Users in Firebase
1. Firebase Console → Authentication
2. Click **Add user**
3. Email: `professor@test.com`
4. Password: `test123456`
5. Add another: `student@test.com`

#### 7.2 Test Attendance Flow
1. Login with professor account
2. Click **Home tab**
3. Click **Start scanning** button
4. Position face in camera frame
5. When face detected → ✅ Attendance marked
6. Check **Reports tab** to see attendance records

#### 7.3 Verify Firebase
1. Firebase Console → Firestore Database
2. You should see:
   - `attendance` collection with records
   - `classes` collection with class info
   - `students` collection with student data

---

### **STEP 8: Monitor & Maintain**

#### 8.1 Check Logs
```bash
# View app logs
adb logcat

# Filter for app logs
adb logcat | grep SmartAttend
```

#### 8.2 Monitor Firebase
- Firebase Console → Dashboard
- View: Real-time Database, Storage usage, Auth users

#### 8.3 Update App
```bash
# Make changes to code
# Rebuild and deploy
./gradlew :app:bundleRelease

# Upload new version to Play Store
# Increment version in build.gradle.kts
```

---

## **Quick Reference: All Commands**

```bash
# Clone
git clone https://github.com/koremaniteja/face-attendance.git

# Build debug APK
./gradlew :app:assembleDebug

# Build release APK
./gradlew :app:bundleRelease

# Install on device
adb install app/build/outputs/apk/debug/app-debug.apk

# View logs
adb logcat

# Deploy Firebase rules
firebase deploy --only firestore:rules,storage
```

---

## **Troubleshooting**

| Issue | Solution |
|-------|----------|
| Gradle build fails | Clear cache: `./gradlew clean` then rebuild |
| App crashes on launch | Check google-services.json is in app/ folder |
| Camera not working | Grant camera permission in app settings |
| Face not recognized | Ensure good lighting, face is clearly visible |
| Firebase connection error | Check internet connection, verify Firebase config |

---

## **Final Links**

- **Repository:** https://github.com/koremaniteja/face-attendance
- **Latest Commit:** https://github.com/koremaniteja/face-attendance/commit/47b5dfefe46e2ff8d829c56c6eaa75d6e1cbbb86
- **Firebase Console:** https://console.firebase.google.com
- **Google Play Console:** https://play.google.com/console
- **Android Studio:** https://developer.android.com/studio

---

**✅ You're now ready to deploy SmartAttend AI!**

For questions: Check GitHub Issues at https://github.com/koremaniteja/face-attendance/issues
