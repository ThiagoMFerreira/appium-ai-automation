# Appium Setup — Agent Reference

> **Purpose:** Setup and validation of Appium environment for Android testing.
> **Output:** Creates `APPIUM_CONTEXT.md` with validated environment details.
> **Next:** Use `appium-automation` skill for creating tests.

---

## ⚡ Setup Phase — Environment Validation

Run these checks in order before creating any tests. Stop at the first failure and fix it.

### Step 1 — Check Java (required for Appium and Gradle)

```bash
java -version
javac -version
```

**Expected:** Java 17 or higher. Appium and the Kotlin/Gradle test project both require a JDK installed and available on the PATH.

If Java is missing:
- macOS: `brew install openjdk@17` or install from Azul/Oracle
- Linux: `sudo apt install openjdk-17-jdk` or equivalent
- Windows: install JDK 17/21 and ensure `java` and `javac` are on PATH

### Step 2 — Check Node.js (required for Appium)

```bash
node --version
```

**Expected:** `v18.x` or higher (Appium 2.x requires Node ≥ 18).

If Node is missing or outdated → **cannot be installed automatically.** Show this message to the user:

> ❌ **Node.js not found or version too old.**
> Appium 2.x requires Node.js **v18 or higher**.
> Please install it manually:
> - **Download:** https://nodejs.org (choose the LTS version)
> - **Or via version manager (recommended):**
>   - macOS/Linux: `nvm install --lts` (install nvm first: https://github.com/nvm-sh/nvm)
>   - Windows: `winget install OpenJS.NodeJS.LTS`
> - After installing, restart your terminal and run `node --version` to confirm.

Do not proceed until `node --version` returns v18 or higher.

### Step 3 — Check npm (comes with Node)

```bash
npm --version
```

**Expected:** `v9.x` or higher. If this fails, Node installation is incomplete — reinstall Node.

### Step 4 — Check / Install Appium

```bash
appium --version
```

If Appium is not installed:
```bash
npm install -g appium
appium driver install uiautomator2   # Android
appium driver install xcuitest       # iOS (macOS only)
```

**Expected Appium version:** 2.x or higher.

> ⚠️ If `npm install -g appium` fails with a permissions error on macOS/Linux:
> Use `sudo npm install -g appium` or fix npm global permissions:
> https://docs.npmjs.com/resolving-eacces-permissions-errors-when-installing-packages-globally

**Important environment setup for Appium Android:**
```bash
export ANDROID_SDK_ROOT="$HOME/Library/Android/sdk"
export ANDROID_HOME="$ANDROID_SDK_ROOT"
export PATH="$PATH:$ANDROID_SDK_ROOT/platform-tools"
```

If Appium fails with `Neither ANDROID_HOME nor ANDROID_SDK_ROOT environment variable was exported`, set these variables and restart your terminal.

### Step 5 — Check ADB and device

```bash
adb devices
```

**Expected:** at least one device listed (emulator or physical).

If `adb` is not found:
- Ensure the Android SDK platform-tools are installed
- Add them to PATH: `export PATH="$PATH:$HOME/Library/Android/sdk/platform-tools"`

If no device appears:
- Start an emulator: `emulator -avd <AVD_NAME>` or open Android Studio → Device Manager
- Or connect a physical device with USB debugging enabled (Settings → Developer Options → USB Debugging)

### Step 6 — Install APK

**First, check if the APK path was provided in the user prompt.**

- If APK path was provided: use that path
- If not provided: ask the user for the APK file path (preferably if it's not in the project root)

```bash
adb install -r path/to/your/app.apk
```

**Expected:** `Success` message in output.

> 💡 **Tip:** To install and automatically launch the app in one command (recommended for setup):
> ```bash
> adb install -r path/to/your/app.apk && adb shell monkey -p {{APP_PACKAGE}} -c android.intent.category.LAUNCHER 1
> ```
> This installs the APK and immediately launches it, allowing you to see the app open and gather package/activity details from the screen.

### Step 7 — CRITICAL: Verify app is open and visible

After installation (and launch if using the combined command above), verify the app is running:

```bash
adb shell dumpsys window windows | grep -E 'mCurrentFocus|mFocusedApp'
```

**Expected:** Should show your app's package and activity, e.g., `mCurrentFocus=Window{... com.yourapp/.MainActivity}`

If the app is not open:
- Launch manually: `adb shell monkey -p {{APP_PACKAGE}} -c android.intent.category.LAUNCHER 1`
- Wait 3-5 seconds for the app to fully load

### Step 8 — Start Appium server

```bash
appium &
# Default port: 4723
# Verify: curl http://localhost:4723/status
```

### Step 9 — Verify Appium can see Android tooling

```bash
curl -s http://localhost:4723/status
adb devices
```

If the Appium server is ready but the session still fails, check:
- `ANDROID_HOME` / `ANDROID_SDK_ROOT` are exported
- `adb` is on PATH and the emulator/device is listed
- Java is installed and `java -version` works

---

## 📁 Output — APPIUM_CONTEXT.md

After completing all setup steps, create this file at the project root:

```markdown
# Appium Test Context

## App Under Test
- Package: com.yourapp.package
- Activity: .MainActivity
- APK path: app/build/outputs/apk/debug/app-debug.apk

## Environment
- Appium: 2.x @ http://127.0.0.1:4723
- Driver: UiAutomator2
- Client: io.appium:java-client:9.2.3
- Node: v20.x

## Test Start Strategy
- Always start each scenario from a clean app state.
- Use `noReset=false` and `fullReset=false` in Appium options for new sessions.
- If needed, force-stop and relaunch the app before a new scenario:
  ```bash
  adb shell am force-stop {{APP_PACKAGE}}
  adb shell monkey -p {{APP_PACKAGE}} -c android.intent.category.LAUNCHER 1
  ```
- Confirm Home screen before proceeding with scenario steps.

## Run Command
./gradlew test --tests "tests.*"

## Known Issues
<!-- list flaky tests or environment quirks -->
```

**Next:** Use the `appium-automation` skill for creating test scenarios.