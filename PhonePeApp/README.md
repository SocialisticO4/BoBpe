# PhonePe Bottom Navigation App

A complete Android application implementing the PhonePe-style bottom navigation bar with elevated QR scanner button.

## Features

✅ **Pixel-perfect PhonePe navigation design**  
✅ **5 navigation tabs**: Home, Search, QR Scanner (FAB), Alerts, History  
✅ **History tab active by default** (matching original design)  
✅ **Elevated purple QR FAB** with animations  
✅ **Material Design components**  
✅ **Fragment-based navigation**  
✅ **Click animations** and state management  

## Project Structure

```
PhonePeApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/phonepe/app/
│   │   │   ├── MainActivity.kt
│   │   │   ├── HomeFragment.kt
│   │   │   ├── SearchFragment.kt
│   │   │   ├── AlertsFragment.kt
│   │   │   ├── HistoryFragment.kt
│   │   │   └── QRScannerFragment.kt
│   │   └── res/
│   │       ├── drawable/ (icons)
│   │       ├── layout/ (XML layouts)
│   │       ├── menu/ (navigation menu)
│   │       ├── color/ (color selectors)
│   │       └── values/ (strings, colors, themes)
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── README.md
```

## Setup Instructions

### 1. Import to Android Studio
- Open Android Studio
- Select "Import Project"
- Choose the `PhonePeApp` folder
- Wait for Gradle sync

### 2. Build & Run
- Clean Project: `Build > Clean Project`
- Rebuild: `Build > Rebuild Project`
- Run on device/emulator

### 3. Dependencies
The following dependencies are included:
- Material Design Components
- Navigation Components
- Fragment KTX
- ZXing (for QR scanning - optional)

## Key Components

### MainActivity.kt
- Main entry point
- Sets up bottom navigation
- Handles FAB click events
- Fragment navigation management

### Bottom Navigation
- 5 tabs with proper icons
- Color state management
- History tab selected by default

### Floating Action Button
- Elevated purple QR scanner
- Click animations
- Centered positioning over navigation

## Customization

### Colors
Edit `res/values/colors.xml` to change the purple theme colors:
```xml
<color name="phonepe_purple">#7c3aed</color>
<color name="phonepe_purple_dark">#6b2fc7</color>
```

### Icons
Replace icons in `res/drawable/` to customize navigation items.

### Add QR Scanning
Uncomment ZXing dependency in `app/build.gradle` and implement camera functionality in `QRScannerFragment.kt`.

## Testing
The app includes placeholder screens for each navigation item. The History tab is active by default, matching the original PhonePe design.

## License
This is a demonstration project recreating PhonePe's UI design for educational purposes.