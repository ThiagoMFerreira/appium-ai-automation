# Appium Test Context

## App Under Test
- Package: com.saucelabs.mydemoapp.android
- Launch Activity: com.saucelabs.mydemoapp.android.view.activities.SplashActivity
- APK path: mda-2.2.0-25.apk

## Environment
- Appium: 2.x @ http://127.0.0.1:4723
- Driver: UiAutomator2
- Client: io.appium:java-client:9.x
- Language: Kotlin
- Test framework: JUnit 5
- Java: 17+
- Android SDK: $HOME/Library/Android/sdk
- ADB: Available via PATH from Android SDK platform-tools

## Test Start Strategy
- Always start each scenario from a clean app state.
- Use `noReset=false` and `fullReset=false` in Appium options for new sessions.
- If needed, force-stop and relaunch the app before a new scenario:
  ```bash
  adb shell am force-stop com.saucelabs.mydemoapp.android
  adb shell monkey -p com.saucelabs.mydemoapp.android -c android.intent.category.LAUNCHER 1
  ```
- Confirm Home screen before proceeding with scenario steps.

## Screens Covered
- [x] Home
- [ ] Cart
- [ ] Checkout
- [ ] Login

## Run Command
```bash
cd appium-tests
./gradlew test --tests "tests.*"
```

## Known Issues
- `uiautomator dump` only captures the currently visible screen; generate a separate XML dump for each screen.
- `dumpsys window windows` may not return useful focus information on some emulators.
- App installation does not launch the app automatically; always start the app explicitly.
