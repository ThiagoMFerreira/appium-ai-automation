# Appium Guideline — Agent Instructions

> **Purpose:** Instructions for agents to generate functional Appium tests with real locators extracted from the app.
> Default language: **Kotlin + JUnit5 + Appium Java Client 9.x**
> Primary platform: **Android (UiAutomator2)** | iOS: see reference section at the end.
>
> **Fundamental rule:** Never invent locators. Always extract from the real app using `appium-inspector`.

---

## 🔎 Pre-check — APPIUM_CONTEXT.md

Before generating any tests, check if `APPIUM_CONTEXT.md` exists at the project root.

- If it exists: proceed directly to generation.
- If it does not exist: use the `appium-setup` skill to configure the environment first.

This ensures the environment is validated before generating code.

## 🔍 Live Screen Inspection

**Never generate locators from imagination. Always extract from the real app using `appium-inspector`.**

### Pre-requisite: Start every new test scenario from a known Home state

When generating a new test, do not assume the app is on the correct screen.
Always start from a clean state and confirm you are on the Home screen before navigating.

- For automation, prefer a clean app state:
  - Set `noReset` to `false`
  - Set `fullReset` to `false` if reinstall is not needed
  - Ensure the driver session starts from the app launch activity
- Confirm Home screen using existing locators before continuing.
- If the app may be on a different screen, do not continue until Home is explicitly verified.
**Navigation Rule:** After any navigation action (tap, back, swipe), always verify the target page with `page.isDisplayed()` before proceeding. Never assume navigation succeeded.
This makes each new scenario reliable and prevents state leakage from previous tests.

### Step 1.1 — Get the app's package and activity

**PRE-CONDITION:** App must be open and visible on screen before proceeding.

**Package Name** — Multiple fallback methods (try in order):

```bash
# Method 1 (BEST - if available):
aapt dump badging path/to/app.apk | grep "^package:"

> Note: `aapt` is part of Android build-tools. If missing, install build-tools through Android SDK manager or use the `pm dump` fallback.

# Method 2 (if aapt not found, ask the user):
# "What is the app name as shown in Android Settings → Apps? (e.g., 'My Demo App')"
# Then search: adb shell pm list packages | grep -i "demo"

# Method 3 (check recently installed):
adb shell pm list packages | grep -v "com.android\|com.google" | tail -5
```

**Activity Name** — After finding the package, extract the launcher activity:

```bash
# Option A: if app is already running on screen:
adb shell dumpsys window windows | grep -E 'mCurrentFocus|mFocusedApp'
```

> Note: On many emulators, `dumpsys window windows` returns nothing useful. If that happens, do not rely on it.

```bash
# Option B: inspect the package directly if app is installed:
adb shell pm dump {{PACKAGE_NAME}} | grep -i "android.intent.action.MAIN" -A 2
```

```bash
# Option C: inspect the APK file directly if aapt is available:
aapt dump badging path/to/app.apk | grep -E 'package|launchable-activity'
```

```bash
# Option D: launch the app with monkey to ensure it is open:
adb shell monkey -p {{PACKAGE_NAME}} -c android.intent.category.LAUNCHER 1
```

**Result:** You should have:
- ✅ `appPackage` = `com.saucelabs.mydemoapp.android` (example)
- ✅ `appActivity` = `com.saucelabs.mydemoapp.android.view.activities.SplashActivity` (example)

**FALLBACK (if extraction fails):** Ask user directly:
```
❓ Could not auto-detect app activity. Please provide:
1. What is the full app name shown in Android Settings → Apps?
2. Can you see the app logo/name on the home screen of the emulator?
If yes, the activity is likely '.MainActivity' or '.SplashActivity'
```

Record: `appPackage` and `appActivity`.

### Step 1.2 — Extract the UI hierarchy (page_source XML)

**Important:** each dump captures only the currently visible screen.
Generate a separate XML for each screen and name it clearly, for example `home_screen_dump.xml`, `cart_screen_dump.xml`, `checkout_screen_dump.xml`, or `login_screen_dump.xml`.
Save all dumps in the `appium-tests/dumps/` folder for organization.

```bash
# Navigate to the screen to test, then:
adb exec-out uiautomator dump /dev/tty 2>/dev/null | cat

# Save to file for easier processing (in appium-tests/dumps/):
adb exec-out uiautomator dump /dev/tty 2>/dev/null > appium-tests/dumps/home_screen_dump.xml
```

If you change screens, dump again with a new filename in the same folder.

### Step 1.3 — Parse the XML for locators

```bash
# Using the bundled script:
python3 extract_locators.py screen_dump.xml

# Filter by keywords (focus on relevant elements):
python3 extract_locators.py screen_dump.xml --filter "login,email,password,button"

# Or pipe directly:
adb exec-out uiautomator dump /dev/tty 2>/dev/null | python3 extract_locators.py
```

The script outputs a ranked locator table with ready-to-use Kotlin `@AndroidFindBy` annotations.

### Step 1.4 — Locator priority rules (STRICT)

Apply in this exact order. Stop at the first that applies:

| Priority | Attribute | Kotlin code |
|---|---|---|
| 1 ✅ BEST | `content-desc` (accessibility ID) | `@AndroidFindBy(accessibility = "login_button")` |
| 2 ✅ GOOD | `resource-id` (stable, unique) | `@AndroidFindBy(id = "com.myapp:id/btn_login")` |
| 3 ⚠️ OK | Static visible `text` | `@AndroidFindBy(uiAutomator = "new UiSelector().text(\"Entrar\")")` |
| 4 ⚠️ FRAGILE | Relative XPath (attribute-based) | `@AndroidFindBy(xpath = "//android.widget.Button[@text='Entrar']")` |
| 5 ❌ NEVER | Absolute XPath | Breaks on any layout change |
| 6 ❌ NEVER | Pixel coordinates | Breaks on any screen size |

### Step 1.5 — Clickability Rule (CRITICAL)

When selecting a locator for an interaction (click/tap), **always** ensure the target node has the property `clickable="true"`.

- **Problem:** Many nodes (especially `TextView`) have the text you want but are `clickable="false"`. Clicking them will result in a "silent failure" where the test continues but nothing happens in the app.
- **Solution:** If the desired node is not clickable, navigate up the hierarchy (parent/ancestor) or down (child) to find the node that actually handles the click.
- **Example:** Instead of clicking the `TextView` with the product name, click the parent `ViewGroup` or the sibling `ImageView` that has `clickable="true"`.

---

## 🏗️ Phase 2 — Project Structure

```
appium-tests/
├── build.gradle.kts
├── src/
│   └── test/
│       └── kotlin/
│           ├── base/
│           │   └── BaseTest.kt          ← Appium session setup/teardown
│           ├── pages/
│           │   └── LoginPage.kt         ← Page Object (locators + actions)
│           └── tests/
│               └── LoginTest.kt         ← Test scenarios
└── capabilities/
    ├── android.json
    └── ios.json                         ← future    
```

---

## 📦 Phase 3 — Boilerplate

### build.gradle.kts

```kotlin
plugins {
    kotlin("jvm") version "1.9.23"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.appium:java-client:9.2.3")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.seleniumhq.selenium:selenium-java:4.19.1")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
```

### BaseTest.kt

```kotlin
package base

import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.options.UiAutomator2Options
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.net.URL
import java.time.Duration

open class BaseTest {

    protected lateinit var driver: AndroidDriver

    @BeforeEach
    fun setUp() {
        val options = UiAutomator2Options().apply {
            setPlatformName("Android")
            setAppPackage("{{APP_PACKAGE}}")       // ← replace with real value from Phase 1
            setAppActivity("{{APP_ACTIVITY}}")     // ← replace with real value from Phase 1
            setAutomationName("UiAutomator2")
            setNewCommandTimeout(Duration.ofSeconds(60))
            setNoReset(true)
        }

        driver = AndroidDriver(URL("http://127.0.0.1:4723"), options)
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5))
    }

    @AfterEach
    fun tearDown() {
        if (::driver.isInitialized) driver.quit()
    }
}
```

---

## 📄 Phase 4 — Page Object Template

One file per screen. Locators at the top, actions as methods. Always return `this` for chaining.

```kotlin
package pages

import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.pagefactory.AndroidFindBy
import io.appium.java_client.pagefactory.AppiumFieldDecorator
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.PageFactory
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

class LoginPage(private val driver: AndroidDriver) {

    init {
        PageFactory.initElements(AppiumFieldDecorator(driver), this)
    }

    private val wait = WebDriverWait(driver, Duration.ofSeconds(10))

    // ── Locators (replace with real values from Phase 1) ────────────────────
    @AndroidFindBy(accessibility = "{{CONTENT_DESC}}")
    private lateinit var emailField: WebElement

    @AndroidFindBy(accessibility = "{{CONTENT_DESC}}")
    private lateinit var passwordField: WebElement

    @AndroidFindBy(accessibility = "{{CONTENT_DESC}}")
    private lateinit var loginButton: WebElement

    @AndroidFindBy(accessibility = "{{CONTENT_DESC}}")
    private lateinit var errorMessage: WebElement

    // ── Actions ──────────────────────────────────────────────────────────────
    fun enterEmail(email: String): LoginPage {
        wait.until(ExpectedConditions.visibilityOf(emailField))
        emailField.clear()
        emailField.sendKeys(email)
        return this
    }

    fun enterPassword(password: String): LoginPage {
        passwordField.clear()
        passwordField.sendKeys(password)
        return this
    }

    fun tapLogin(): LoginPage {
        loginButton.click()
        return this
    }

    fun login(email: String, password: String): LoginPage =
        enterEmail(email).enterPassword(password).tapLogin()

    fun isErrorVisible(): Boolean =
        runCatching {
            wait.until(ExpectedConditions.visibilityOf(errorMessage))
            true
        }.getOrDefault(false)

    fun isLoginButtonDisabled(): Boolean =
        !loginButton.isEnabled
}
```

---

## 🧪 Phase 5 — Test Template

Minimum 3 scenarios per screen: happy path, validation error, edge case.

```kotlin
package tests

import base.BaseTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pages.HomePage
import pages.LoginPage

class LoginTest : BaseTest() {

    @Test
    fun `login with valid credentials navigates to home`() {
        val loginPage = LoginPage(driver)
        loginPage.login("user@email.com", "validPassword123")

        val homePage = HomePage(driver)
        assertTrue(homePage.isDisplayed(), "Home screen should be visible after login")
    }

    @Test
    fun `login with wrong password shows error message`() {
        val loginPage = LoginPage(driver)
        loginPage.login("user@email.com", "wrongPassword")

        assertTrue(loginPage.isErrorVisible(), "Error message should appear for wrong credentials")
    }

    @Test
    fun `login button disabled when fields are empty`() {
        val loginPage = LoginPage(driver)
        assertTrue(loginPage.isLoginButtonDisabled(), "Login button should be disabled with empty fields")
    }
}
```

---

## 🔧 Phase 6 — Self-Healing Protocol

When a test fails with `NoSuchElementException` or `TimeoutException`:

1. **Re-inspect the screen** — run Phase 1 again. The UI may have changed.
2. **Check the failure log** — which locator failed? What attribute changed?
3. **Apply healing strategy:**

| Broken locator | Likely cause | Fix |
|---|---|---|
| `resource-id` | Dev renamed the ID | Re-inspect → new ID or fallback to text |
| `text` | Copy/translation change | Use `content-desc` instead |
| XPath | Layout restructured | Re-inspect → rebuild relative XPath |
| Timeout | Slow network/animation | Increase `WebDriverWait` duration |

4. **Fallback locator with multiple criteria:**

```kotlin
driver.findElement(AppiumBy.androidUIAutomator(
    "new UiSelector().resourceId(\"com.app:id/btn_login\")" +
    ".className(\"android.widget.Button\")"
))
```

---

## 🚀 Phase 7 — Running the Tests

To run the tests and analyze results, follow the instructions in the `appium-runner` skill.
This ensures proper environment preparation, clean execution, and standardized failure analysis.

> **See:** `.skills/appium-runner/SKILL.md`

---

## ⚠️ Anti-patterns

| Anti-pattern | Problem | Fix |
|---|---|---|
| `Thread.sleep()` hardcoded | Flaky on slow devices | Use `WebDriverWait` |
| Absolute XPath (`/hierarchy/android...`) | Breaks on any layout change | Relative XPath or resource-id |
| Pixel coordinates (`tap(x=320, y=540)`) | Breaks on different screens | Use locators |
| Logic/assertions in Page Objects | Tests become unreadable | Pages = actions only; tests = assertions |
| `driver.findElement` without wait | Race condition | Always wrap in `WebDriverWait` |
| `noReset: false` in every test | Slow — reinstalls app every time | Use `noReset: true`, reset state in `@BeforeEach` |
| Clicking non-clickable nodes | Silent failure (no action triggered) | Use `clickable="true"` nodes (check XML) |

---

## 📋 Capabilities Reference

### Android (UiAutomator2)

```json
{
  "platformName": "Android",
  "appium:automationName": "UiAutomator2",
  "appium:appPackage": "com.yourapp.package",
  "appium:appActivity": ".MainActivity",
  "appium:noReset": true,
  "appium:newCommandTimeout": 60,
  "appium:app": "/absolute/path/to/app.apk"
}
```

Use `"appium:app"` to install fresh on each run, or only `appPackage`+`appActivity` if already installed.

### iOS (XCUITest) — future

```kotlin
val options = XCUITestOptions().apply {
    setPlatformName("iOS")
    setDeviceName("iPhone 15")
    setPlatformVersion("17.0")
    setBundleId("com.yourapp.bundleid")
    setAutomationName("XCUITest")
    setNoReset(true)
}
```

Locators iOS:
```kotlin
By.accessibilityId("login_button")                                          // same as Android
AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND label == 'Entrar'")
AppiumBy.iOSClassChain("**/XCUIElementTypeTextField[`placeholderValue == 'Email'`]")
```

---

## 📁 extract_locators.py — Usage Reference

Script bundled with this skill. Parses `uiautomator dump` XML and outputs ranked locators.

```bash
# From file
python3 extract_locators.py screen_dump.xml

# From adb pipe
adb exec-out uiautomator dump /dev/tty 2>/dev/null | python3 extract_locators.py

# Filter by keyword
python3 extract_locators.py screen_dump.xml --filter "login,email,password"

# JSON output (for programmatic use)
python3 extract_locators.py screen_dump.xml --json
```

Output includes: priority ranking, `@AndroidFindBy` annotation, camelCase variable name suggestion.

---

## 🤝 Agent Handoff — APPIUM_CONTEXT.md

Create this file at the project root when starting a new test suite:

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

## Screens Covered
- [ ] Login (LoginPage.kt + LoginTest.kt)

## Screens Pending
- [ ] Registration
- [ ] Home
- [ ] Profile

## Run Command
./gradlew test --tests "tests.*"

## Known Issues
<!-- list flaky tests or environment quirks -->
```
