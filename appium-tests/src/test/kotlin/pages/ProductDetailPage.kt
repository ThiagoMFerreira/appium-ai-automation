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

class ProductDetailPage(private val driver: AndroidDriver) {

    init {
        PageFactory.initElements(AppiumFieldDecorator(driver), this)
    }

    private val wait = WebDriverWait(driver, Duration.ofSeconds(10))

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/productTV")
    private lateinit var productTitle: WebElement

    @AndroidFindBy(accessibility = "Tap to add product to cart")
    private lateinit var addToCartButton: WebElement

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/plusIV")
    private lateinit var increaseQuantityButton: WebElement

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/noTV")
    private lateinit var quantityText: WebElement

    fun isDisplayed(): Boolean =
        runCatching {
            wait.until(ExpectedConditions.visibilityOf(productTitle))
            wait.until(ExpectedConditions.visibilityOf(addToCartButton))
            true
        }.getOrDefault(false)

    fun getProductTitle(): String =
        runCatching {
            wait.until(ExpectedConditions.visibilityOf(productTitle))
            productTitle.text
        }.getOrDefault("")

    fun getQuantity(): Int =
        runCatching {
            wait.until(ExpectedConditions.visibilityOf(quantityText))
            quantityText.text.toIntOrNull() ?: 0
        }.getOrDefault(0)

    fun tapAddToCart(): ProductDetailPage {
        wait.until(ExpectedConditions.elementToBeClickable(addToCartButton))
        addToCartButton.click()
        return this
    }

    fun increaseQuantity() {
        wait.until(ExpectedConditions.elementToBeClickable(increaseQuantityButton)).click()
        wait.until(ExpectedConditions.textToBePresentInElement(quantityText, "2"))
    }

    fun addToCart() {
        wait.until(ExpectedConditions.elementToBeClickable(addToCartButton)).click()
        Thread.sleep(1000)
    }
}