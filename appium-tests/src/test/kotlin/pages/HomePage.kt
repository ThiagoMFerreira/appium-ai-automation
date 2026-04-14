package pages

import io.appium.java_client.AppiumBy
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.pagefactory.AndroidFindBy
import io.appium.java_client.pagefactory.AppiumFieldDecorator
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.PageFactory
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

class HomePage(private val driver: AndroidDriver) {

    init {
        PageFactory.initElements(AppiumFieldDecorator(driver), this)
    }

    private val wait = WebDriverWait(driver, Duration.ofSeconds(10))

    // ── Locators (extracted from Phase 1 UI dump) ────────────────────
    @AndroidFindBy(accessibility = "App logo and name")
    private lateinit var appLogo: WebElement

    @AndroidFindBy(accessibility = "title")
    private lateinit var productsTitle: WebElement

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/productRV")
    private lateinit var productsRecyclerView: WebElement

    // ── Actions ──────────────────────────────────────────────────────────────
    fun isDisplayed(): Boolean =
        runCatching {
            wait.until(ExpectedConditions.visibilityOf(productsTitle))
            wait.until(ExpectedConditions.visibilityOf(productsRecyclerView))
            true
        }.getOrDefault(false)

    fun isAppLogoVisible(): Boolean =
        runCatching {
            wait.until(ExpectedConditions.visibilityOf(appLogo))
            true
        }.getOrDefault(false)

    fun isProductsTitleVisible(): Boolean =
        runCatching {
            wait.until(ExpectedConditions.visibilityOf(productsTitle))
            productsTitle.text == "Products"
        }.getOrDefault(false)

    fun isProductsListVisible(): Boolean =
        runCatching {
            wait.until(ExpectedConditions.visibilityOf(productsRecyclerView))
            true
        }.getOrDefault(false)

    fun openFirstProduct() {
        val firstProductImage = runCatching {
            wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(AppiumBy.id("com.saucelabs.mydemoapp.android:id/productIV"))))
        }.getOrNull() ?: throw IllegalStateException("No product image found to open")

        firstProductImage.click()
    }
}