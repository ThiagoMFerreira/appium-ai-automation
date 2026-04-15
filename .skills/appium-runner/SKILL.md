# Appium Runner — Agent Instructions

> **Purpose:** Instructions for agents to generate run commands, debugging, and reports for Appium tests.
> **Output:** Run scripts, pre-run validations, and self-healing.
> **Integration:** Uses Gradle for build and execution.

---

## 🚀 Test Execution and Debugging

When running generated tests:

### Step 1 — Pre-Validation
- Check environment:
  ```bash
  adb devices  # Should list device
  ```
- If `adb` command not found, set Android SDK environment variables and retry:
  ```bash
  export ANDROID_SDK_ROOT="$HOME/Library/Android/sdk"
  export PATH="$PATH:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/emulator"
  adb devices
  ```
- Then check Appium server: `curl http://localhost:4723/status`
- If Appium not OK, attempt to start: `appium --address 127.0.0.1 --port 4723 & sleep 5 && curl http://localhost:4723/status`
- If still not OK, report: "Failed to start Appium server."
- Check app installed: `adb shell pm list packages | grep {{APP_PACKAGE}}`
- If any check fails, invoke `appium-setup` to fix.

### Step 2 — Run Tests

**🚨 CRITICAL: MANDATORY PRE-RUN CLEANUP**
- You **MUST** delete all previous failure artifacts before starting any execution. If you don't, you will analyze screenshots from previous failed runs.
- **Command:** `rm -rf appium-tests/screenshot-failures/*`

**Execution Commands:**
- All: `./gradlew test`
- Specific class: `./gradlew test --tests "tests.HomeTest"`
- Verbose: `./gradlew test --info`
- Report: Open `appium-tests/build/reports/tests/test/index.html`

### Step 3 — Debug Failures

1.  **Check for Compilation Errors**: First, verify if the failure occurred during the build/compilation phase. If there are syntax or type errors, skip to step 4.

**🚨 STOP! GATEKEEPER — MANDATORY PROTOCOL:**
Before running ANY `adb shell`, `uiautomator dump`, `ui_state`, or code modification, you **MUST**:
1.  **Locate**: Find the latest failure PNG in `appium-tests/screenshot-failures/`.
2.  **Inspect**: Use `read_file` to open and examine the **existing** screenshot file on disk. **DO NOT** use `ui_state` or `take_screenshot` for failure analysis, as they capture the post-test state (often the Launcher).
3.  **Declare**: You **MUST** start your analysis by describing the application's state in the screenshot (e.g., "The app is on the Launcher", "App is showing a 'My Cart' screen with items").

**WARNING:** `ui_state` and `take_screenshot` provide the CURRENT/LIVE state. Since tests close the app on failure (`tearDown`), these tools will show the Launcher. You MUST analyze the screenshot captured AT THE MOMENT OF FAILURE using `read_file`.

**Analysis without a prior visual declaration based on the FAILURE SCREENSHOT is strictly prohibited.**

2.  **Visual Validation (The "Why")**:
    - **Verify Page Context**: Is the app on the screen the code expects? If it's on the Launcher, a different screen, or showing a crash dialog, the issue is navigation or app stability.
    - **Verify Element Visibility**: If the screen is correct, is the target element actually visible and not obscured by another view (e.g., a keyboard or popup)?
3.  **UI Dump Analysis**: **ONLY if the screenshot is inconclusive** (e.g., the element looks correct but interaction fails), use the **`appium-inspector` skill** to generate a fresh XML dump and inspect properties like `clickable`, `enabled`, and exact bounds. **DO NOT** run `uiautomator dump` or `adb shell` commands directly for this purpose.
4.  **Apply Corrections via Skills**:
    - For **Locator updates or UI inspection**: Use the `appium-inspector` skill.
    - For **Code modifications** (logic, Page Objects, waits): **You MUST use the `appium-developer` skill** to ensure the code adheres to project standards (e.g., `isDisplayed()` checks, explicit waits, Page Object pattern).

### Step 4 — Optimization
- Set `noReset: true` for speed, reset state in `@BeforeEach`.
- Parallelize if possible via Gradle.

This ensures reliable execution and automatic maintenance.