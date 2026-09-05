# SmartAttend AI - Deployment Guide

## Latest Update (September 5, 2026)

✅ **Face Recognition & Attendance System Implemented**
- Real-time face detection using Google ML Kit
- Face embedding generation with TensorFlow Lite FaceNet
- Automatic student matching with confidence scores
- Only matched students get attendance marked
- Full Firestore integration for attendance records
- All authenticated users have access to read/write

**Latest Commit:** 
```
47b5dfefe46e2ff8d829c56c6eaa75d6e1cbbb86
Add comprehensive face recognition system for attendance
```

## Deployment Options

### Option 1: Build APK for Android Testing (Recommended for Development)

```bash
# Clone repository
git clone https://github.com/koremaniteja/face-attendance.git
cd face-attendance

# Build APK
./gradlew :app:assembleDebug

# APK Location
# Output: app/build/outputs/apk/debug/app-debug.apk
```

**Transfer to Device:**
```bash
# Using adb
adb install app/build/outputs/apk/debug/app-debug.apk

# Or manually transfer .apk file to Android device
```

### Option 2: Google Play Store (Production Deployment)

**Prerequisites:**
- Google Play Developer Account ($25 USD)
- Signed keystore file
- Google Services configuration

**Steps:**
1. Generate signed APK/AAB:
   ```bash
   ./gradlew :app:bundleRelease
   # Output: app/build/outputs/bundle/release/app-release.aab
   ```

2. Upload to Google Play Console:
   - Go to https://play.google.com/console
   - Create new app: "SmartAttend AI"
   - Upload AAB file
   - Fill app details, screenshots, permissions
   - Request review (24-48 hours)
   - Publish to Play Store

**Store Link** (after approval):
```
https://play.google.com/store/apps/details?id=com.smartattend.ai
```

### Option 3: Firebase App Distribution (For Internal Testing)

```bash
# Install Firebase CLI
npm install -g firebase-tools

# Build APK
./gradlew :app:assembleDebug

# Upload to Firebase
firebase appdistribution:distribute app/build/outputs/apk/debug/app-debug.apk \
  --app=<APP_ID> \
  --groups=testers
```

**Testing Link:** Sent to your Firebase Console

### Option 4: GitHub Releases (Direct Download)

Create a release on GitHub:

```bash
# Tag the current commit
git tag -a v1.0.0 -m "Initial face recognition release"
git push origin v1.0.0
```

Then on GitHub:
1. Go to https://github.com/koremaniteja/face-attendance/releases
2. Click "Create a new release"
3. Select tag `v1.0.0`
4. Upload `app-debug.apk` file
5. Publish release

**Download Link:**
```
https://github.com/koremaniteja/face-attendance/releases/download/v1.0.0/app-debug.apk
```

---

## System Requirements

- **Android Version:** API 21 (Android 5.0) and above
- **Target Version:** API 34 (Android 14)
- **Camera:** Front camera for face recognition
- **Permissions Needed:**
  - Camera
  - Internet (Firebase)
  - Storage (attendance reports)

## Firebase Setup

1. Create Firebase Project:
   - Go to https://console.firebase.google.com
   - Click "Create Project"
   - Enable Authentication, Firestore, Storage

2. Download `google-services.json`:
   - In Firebase Console → Project Settings
   - Download config file
   - Place in `app/` directory

3. Deploy Security Rules:
   ```bash
   npm install -g firebase-tools
   firebase login
   firebase deploy --only firestore:rules,storage
   ```

## Current Features Deployed

✅ **Face Recognition**
- Real-time face detection from camera
- 128-dimensional face embeddings
- Cosine similarity matching (threshold: 0.6)
- Face quality validation (size, rotation)

✅ **Attendance System**
- Auto-mark present when face matches
- Confidence score tracking
- Attendance history per student
- Class-wise summary reports

✅ **Security & Access Control**
- Firebase Authentication
- Role-based Firestore rules
- All authenticated users can access
- Student data protected

---

## Links

- **Repository:** https://github.com/koremaniteja/face-attendance
- **Latest Commit:** https://github.com/koremaniteja/face-attendance/commit/47b5dfefe46e2ff8d829c56c6eaa75d6e1cbbb86
- **Releases:** https://github.com/koremaniteja/face-attendance/releases
- **Issues:** https://github.com/koremaniteja/face-attendance/issues

---

## Next Steps

1. **Build & Test Locally**
   ```bash
   git clone https://github.com/koremaniteja/face-attendance.git
   ./gradlew :app:assembleDebug
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

2. **Deploy to Play Store** (Optional)
   - Follow Option 2 steps above

3. **Share with Students/Professors**
   - Download APK from releases
   - Or install from Play Store

4. **Configure Firebase**
   - Add google-services.json
   - Deploy security rules
   - Create test students & classes

---

**Version:** 1.0.0  
**Last Updated:** September 5, 2026  
**Status:** ✅ Ready for Testing
