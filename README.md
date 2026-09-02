# SmartAttend AI - Student Attendance Management System

## Overview
SmartAttend AI is a modern Android application designed for managing student attendance using advanced face recognition technology. The application allows professors to easily take attendance by detecting and recognizing students' faces in real-time.

## Features
- **User Authentication**: Secure login and registration for professors using Firebase Authentication.
- **Class Management**: Create, edit, and delete classes and subjects.
- **Student Enrollment**: Enroll students by capturing their photos and storing their face embeddings.
- **Real-time Attendance Scanning**: Use the camera to detect and recognize students, marking their attendance automatically.
- **Attendance Reports**: Generate and export attendance reports in Excel format.
- **User-friendly Interface**: A clean and modern UI built with Jetpack Compose, supporting both light and dark modes.
- **Ready-to-run demo mode**: Explore the dashboard, classes, students, reports, and simulated face scan without Firebase configuration.

## Technology Stack
- **Mobile Development**: Kotlin, Android Studio, Jetpack Compose
- **Camera & Face Detection**: CameraX, Google ML Kit
- **Backend & Database**: Firebase Authentication, Firestore, Firebase Storage
- **Face Recognition**: TensorFlow Lite with FaceNet or MobileFaceNet model

## Project Structure
```
smartattend-ai
├── app
│   ├── src
│   │   ├── main
│   │   │   ├── java/com/smartattend/ai
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── data
│   │   │   │   │   ├── firebase
│   │   │   │   │   │   ├── AuthDataSource.kt
│   │   │   │   │   │   ├── FirestoreDataSource.kt
│   │   │   │   │   │   └── StorageDataSource.kt
│   │   │   │   │   ├── model
│   │   │   │   │   ├── repository
│   │   │   │   │   └── mapper
│   │   │   │   ├── domain
│   │   │   │   │   ├── model
│   │   │   │   │   ├── repository
│   │   │   │   │   └── usecase
│   │   │   │   ├── presentation
│   │   │   │   │   ├── auth
│   │   │   │   │   ├── dashboard
│   │   │   │   │   ├── classes
│   │   │   │   │   ├── students
│   │   │   │   │   ├── attendance
│   │   │   │   │   ├── reports
│   │   │   │   │   ├── navigation
│   │   │   │   │   └── components
│   │   │   │   ├── camera
│   │   │   │   │   ├── CameraManager.kt
│   │   │   │   │   └── FaceOverlay.kt
│   │   │   │   ├── ml
│   │   │   │   │   ├── FaceDetectorManager.kt
│   │   │   │   │   ├── FaceEmbeddingManager.kt
│   │   │   │   │   ├── FaceRecognitionManager.kt
│   │   │   │   │   └── SimilarityCalculator.kt
│   │   │   │   ├── ui
│   │   │   │   │   ├── theme
│   │   │   │   │   └── components
│   │   │   │   └── util
│   │   │   ├── res
│   │   │   │   ├── drawable
│   │   │   │   ├── mipmap
│   │   │   │   ├── values
│   │   │   │   └── xml
│   │   │   ├── assets
│   │   │   │   └── face_recognition_model.tflite
│   │   │   └── AndroidManifest.xml
│   │   └── test
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── firestore.rules
├── storage.rules
├── README.md
└── .gitignore
```

## Setup Instructions
1. **Clone the Repository**: 
   ```
   git clone <repository-url>
   cd smartattend-ai
   ```

2. **Open the Project**: Open the project in Android Studio.

3. **Configure Firebase**:
   - Create a Firebase project in the Firebase Console.
   - Add your Android app to the Firebase project.
   - Download the `google-services.json` file and place it in the `app/` directory.

4. **Build the APK**: Run `gradlew.bat :app:assembleDebug` from the project root. The APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

5. **Run the Application**: Connect an Android device or start an emulator, then run the application from Android Studio.

## Usage
- **Login**: Use your credentials to log in to the application.
- **Manage Classes and Students**: Create classes, enroll students, and start taking attendance.
- **Attendance Scanning**: Use the camera to scan for students and automatically mark their attendance.
- **Generate Reports**: View and export attendance reports as needed.

When Firebase is not configured, sign-in opens the local demo experience. Add `google-services.json` and connect the repository/data layer before enabling production authentication and cloud persistence.

## Contributing
Contributions are welcome! Please feel free to submit a pull request or open an issue for any suggestions or improvements.

## License
This project is licensed under the MIT License. See the LICENSE file for more details.