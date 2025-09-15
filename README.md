# PhonePe UI Clone (Educational Mock-up)

## Description

This project is a simplified UI clone of the popular Indian digital payments platform, PhonePe. It has been developed as a learning exercise to explore and implement modern Android application development concepts using Jetpack Compose.

The application mock-up includes features such as:
*   A payment screen to simulate sending money.
*   A success screen displayed after a mock transaction.
*   Display of transaction details.
*   Use of custom UI components inspired by modern design trends.

## Disclaimer

**For Educational Purposes Only:** This application is created solely for educational and demonstrative purposes to showcase Android development skills. It is not intended for any real transactions, commercial use, or to impersonate the official PhonePe application.

**Not Affiliated with PhonePe:** This project is an independent creation and is not affiliated with, endorsed by, sponsored by, or in any way officially connected with PhonePe Private Limited or any of its subsidiaries or its affiliates.

**Trademarks:** PhonePe is a registered trademark of PhonePe Private Limited. All trademarks, service marks, trade names, product names, and logos appearing in this educational mock-up are the property of their respective owners. Their use in this project is for identification and educational demonstration purposes only and does not imply endorsement.

## Purpose

The primary goal of this project is to:
*   Practice and demonstrate proficiency in building Android UIs with Jetpack Compose.
*   Implement common app navigation flows.
*   Understand and use ViewModel for UI-related data management (MVVM).
*   Integrate third-party UI libraries.
*   Explore UI/UX concepts in the context of a well-known application.

## Tech Stack & Technical Aspects

*   **Programming Language:** Kotlin
*   **UI Toolkit:** Jetpack Compose (for declarative UI development)
*   **Architecture:** MVVM (Model-View-ViewModel)
    *   `ViewModel`: Used for managing UI-related data and state.
*   **Navigation:** Jetpack Navigation Compose (for navigating between screens)
*   **Asynchronous Operations:** Kotlin Coroutines (for background tasks, e.g., simulating data insertion)
*   **Local Data Storage (Implicit):** The `HistoryViewModel` and `Transaction` data class suggest a mechanism for storing transaction data, potentially intended for a library like Room Persistence Library (though not explicitly confirmed as implemented in detail).
*   **Key External Libraries:**
    *   `club.cred:neopop:1.0.2`: For NeoPop UI elements like buttons.
    *   Potentially CameraX/ML Kit if QR code scanning was a fully implemented feature (the `qrData` parameter suggests its consideration).
*   **UI Components:**
    *   Custom composable functions for UI elements.
    *   Use of Material 3 components.
    *   Vector drawables for icons and graphics.

## Features Implemented (Mock-up Level)

*   **Payment Initiation Screen:** Allows entering an amount for a mock payment to a predefined recipient.
*   **Transaction "Success" Screen:** Displays details of the mock transaction.
*   **Transaction Data Handling:** `Transaction` data class and `HistoryViewModel` to manage transaction information in memory (or potentially persist it).
*   **UI Styling:** Custom button styles using the NeoPop library and vector assets.

## How to Build and Run

1.  Clone this repository.
2.  Open the project in the latest stable version of Android Studio.
3.  Ensure you have the necessary Android SDKs and build tools installed.
4.  Let Android Studio sync the Gradle files.
5.  Click the "Run" button to build and deploy the app to an emulator or a connected Android device.

## Download APK (for testing)

If you just want to try out the app on an Android device without building it from the source:

1.  Go to the [**Releases**](https://github.com/SocialisticO4/BoBpe.git/releases) page of this repository.
2.  Look for the latest release.
3.  Download the `.apk` file (e.g., `app-debug.apk` or `app-release.apk`) from the "Assets" section of the release.
4.  You may need to enable "Install from unknown sources" in your Android device's settings to install the APK.

**Note:** The APKs provided are for demonstration and testing purposes only, as this is an educational mock-up.

---

