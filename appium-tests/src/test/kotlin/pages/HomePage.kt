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

    @AndroidFindBy(accessibility = "Product Image")
    private lateinit var firstProductImage: WebElement

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

    fun tapFirstProduct(): ProductDetailPage {
        wait.until(ExpectedConditions.elementToBeClickable(firstProductImage))
        firstProductImage.click()
        return ProductDetailPage(driver)
    }

    fun tapProductByInstance(index: Int): ProductDetailPage {
        val product = wait.until(ExpectedConditions.elementToBeClickable(
            AppiumBy.androidUIAutomator("new UiSelector().description(\"Product Image\").instance($index)")
        ))
        product.click()
        return ProductDetailPage(driver)
    }

    fun tapCart(): CartPage {
        val cartButton = wait.until(ExpectedConditions.elementToBeClickable(
            AppiumBy.accessibilityId("View cart")
        ))
        cartButton.click()
        return CartPage(driver)
    }

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