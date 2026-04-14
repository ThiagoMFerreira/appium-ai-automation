# Appium Tests for MDA App

Automated tests to validate that the MDA app (mda-2.2.0-25.apk) launches successfully and displays the home screen.

## Prerequisites

- Node.js v18+
- Appium 2.x with UiAutomator2 driver
- Android emulator running
- APK installed on emulator

## Project Structure

```
appium-tests/
├── build.gradle.kts          # Kotlin/Gradle configuration
├── src/test/kotlin/
│   ├── base/
│   │   └── BaseTest.kt       # Appium session setup
│   ├── pages/
│   │   └── HomePage.kt       # Home screen page object
│   └── tests/
│       └── AppLaunchTest.kt  # Launch validation tests
└── capabilities/
    └── android.json          # Appium capabilities
```

## Running Tests

1. **Start Appium server:**
   ```bash
   appium --port 4723
   ```

2. **Run tests:**
   ```bash
   cd appium-tests
   ./gradlew test
   ```

3. **Run specific test:**
   ```bash
   ./gradlew test --tests "tests.AppLaunchTest"
   ```

## Test Cases

### AppLaunchTest
- ✅ `app launches successfully and displays home screen`
  - Validates app logo is visible
  - Validates "Products" title is displayed
  - Validates products list (RecyclerView) is shown

- ✅ `app package and activity are correct`
  - Ensures correct package/activity configuration

## Expected Results

All tests should pass, confirming that:
- The app launches without crashes
- The home screen loads correctly
- UI elements are accessible and visible