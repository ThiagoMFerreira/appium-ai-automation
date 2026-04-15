# Appium Inspector — Agent Instructions

> **Purpose:** Instructions for agents to automatically extract locators from the Android app via XML dumps and scripts with smart version control.
> **Output:** Prioritized list of locators with ready Kotlin annotations.
> **Tools:** Uses `adb`, `uiautomator dump`, `extract_locators.py` (located in `appium-tests/tools/`).

---

## 🔍 Automatic Locator Extraction

When locators are needed for a screen:

### Step 1 — Prepare Environment and App
1. **Verify adb**: Run `adb devices`. If it fails, export the necessary environment variables:
   ```bash
   export ANDROID_SDK_ROOT="$HOME/Library/Android/sdk"
   export PATH="$PATH:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/emulator"
   ```
2. **Verify App State**: Check if the app is currently in the foreground:
   ```bash
   adb shell dumpsys window | grep -E 'mCurrentFocus'
   ```
3. **Launch/Reset App**: If the app is closed or on the Launcher, start it:
   ```bash
   adb shell am start -n com.saucelabs.mydemoapp.android/com.saucelabs.mydemoapp.android.view.activities.SplashActivity
   ```
4. **Navigate to Screen**: If the target locator is on a specific sub-screen (like "Cart"), use `adb shell input tap` or `keyevent` to navigate if the app is already open, or ensure the test state is reached before dumping.

### Step 2 — Smart Dump Management
1. **Identify Screen:** Determine the canonical name for the screen (e.g., `HomeScreen`).
2. **Check Existence:**
   - If `appium-tests/dumps/HomeScreen.xml` **does not exist**:
     - Run: `adb exec-out uiautomator dump /dev/tty 2>/dev/null > appium-tests/dumps/HomeScreen.xml`
   - If it **already exists**:
     - Create temp directory: `mkdir -p appium-tests/dumps/tmp`
     - Run: `adb exec-out uiautomator dump /dev/tty 2>/dev/null > appium-tests/dumps/tmp/HomeScreen_eval.xml`
     - **Compare:** Compare structure/IDs. If identical to the official dump, delete the `_eval.xml` and use the official one.
     - **Conditional Update:** If different, proceed using the `_eval.xml`. If this new dump leads to a successful test fix, **overwrite** the official `appium-tests/dumps/HomeScreen.xml` with it.

### Step 3 — Parse XML
- Use `extract_locators.py` script to analyze the selected dump:
  ```bash
  python3 appium-tests/tools/extract_locators.py appium-tests/dumps/HomeScreen.xml --filter "keyword1,keyword2" --json
  ```
- Output: JSON with locators ranked by priority.

### Step 4 — Prioritize and Format
- **Clickability Check:** Before finalizing a locator for a click action, verify that the node (or an immediate parent/child) has `clickable="true"`. If the node with the desired text/id is not clickable, select the nearest clickable ancestor.
- Apply strict rules:
  1. `content-desc` (accessibility)
  2. `resource-id`
  3. Static `text`
  4. Relative XPath
- Automatically generate `@AndroidFindBy` annotations.
- Suggest variable names in camelCase.

Example output:
```json
[
  {"priority": 1, "annotation": "@AndroidFindBy(accessibility = \"login_button\")", "variable": "loginButton"},
  {"priority": 2, "annotation": "@AndroidFindBy(id = \"com.app:id/btn_login\")", "variable": "loginBtn"}
]
```

### Step 5 — Validation
- Verify locators exist in current XML.
- If dump fails, try alternative methods (dumpsys, aapt).

This provides real, up-to-date locators for code generation.