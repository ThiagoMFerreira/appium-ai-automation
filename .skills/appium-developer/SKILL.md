# Appium Developer — Agent Instructions

> **Purpose:** Instructions for agents to automatically generate Kotlin code for Appium tests, integrating with other skills for locator extraction and setup.
> **Dependencies:** Uses `appium-inspector` for locators, `appium-setup` for environment, `appium-guideline` for rules.
> **Output:** Ready Page Object classes, JUnit5 tests, and Gradle configurations.

---

## 🔧 Automatic Code Generation

When asked to create Appium tests, follow these steps to generate complete, executable code:

### Step 1 — Gather Context
- Check `APPIUM_CONTEXT.md` for package, activity, and existing screens.
- If it does not exist, invoke `appium-setup` to create it.
- Identify screens to test (e.g., Home, Login, Cart) and scenarios (happy path, error validation, edge case).

### Step 2 — Extract Locators
- For each screen, use `appium-inspector` to generate XML dump and extract prioritized locators (content-desc > resource-id > text > relative xpath).
- Automatically generate `@AndroidFindBy` annotations based on extracted locators.

### Step 3 — Generate Page Objects
- Create one Kotlin class per screen (e.g., `HomePage.kt`).
- Include locators as private fields with `@AndroidFindBy`.
- Add action methods (e.g., `tapButton()`, `enterText()`) that return `this` for chaining.
- Use `WebDriverWait` for implicit waits.
- **Page Load Validation**: Implement an `isDisplayed()` method for every Page Object.
    - **CRITICAL**: Use elements that are **always visible** when the page is loaded and **UNIQUE**
      to that specific page, regardless of the data or state (e.g., a header title, a navigation bar,
      or a persistent logo) when possible.
    - **MANDATORY**: For `isDisplayed()` validation, **AVOID** using locators (IDs, accessibility labels)
      that are also present on the previous page in the navigation flow (e.g., don't use a generic "Title"
      ID if the Home page also has a "Title" ID). This prevents false negatives or interaction with stale 
      elements from the previous screen and Avoid using elements that may disappear depending on the
      scenario (e.g., "Empty Cart" labels, "Loading" spinners, or specific data-dependent list items).

Automatically generated example:
```kotlin
class HomePage(private val driver: AndroidDriver) {
    init { PageFactory.initElements(AppiumFieldDecorator(driver), this) }
    private val wait = WebDriverWait(driver, Duration.ofSeconds(10))

    @AndroidFindBy(accessibility = "login_button")
    private lateinit var loginButton: WebElement

    fun tapLogin(): HomePage {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton))
        loginButton.click()
        return this
    }
}
```

### Step 4 — Generate Tests
- Create test classes (e.g., `HomeTest.kt`) extending `BaseTest`.
- Generate at least 3 scenarios per screen: happy path, error validation, edge case.
- Use Page Objects for actions and JUnit5 assertions.
- **Always confirm navigation**: After any navigation (click, back, etc.), verify you are on the expected page using `page.isDisplayed()` before proceeding to next steps. This prevents state assumptions and flaky tests.

Example:
```kotlin
class HomeTest : BaseTest() {
    @Test
    fun `valid login navigates to home`() {
        HomePage(driver).tapLogin()
        assertTrue(HomePage(driver).isDisplayed())
    }
}
```

### Step 5 — Configure Build
- Update `build.gradle.kts` with dependencies (Appium, JUnit, Selenium).
- Ensure folder structure: `src/test/kotlin/base/`, `pages/`, `tests/`.

### Step 6 — Self-Healing
- If locators fail, re-invoke `appium-inspector` to update.
- Apply fallbacks: use UiSelector with multiple criteria if needed.
- On test failure, check screenshots in `screenshot-failures/` to diagnose page state and element visibility.

### Step 7 — Execution
- Then, generate run commands via `appium-runner`.
- Include pre-run validation (adb devices, Appium server).

### Step 8 — Regression & Refactoring Validation
- **Test Refactoring**: Whenever you refactor existing tests (e.g., to improve code quality, readability, or alignment with these standards), you **MUST** execute the modified tests via `appium-runner` to confirm they still pass.
- **Page Object Updates**: If you modify a Page Object class (locators or methods), you **MUST** identify all tests that use this class (using `find_usages`) and execute them to ensure no regressions were introduced.

This ensures complete generation and maintenance without manual intervention.
