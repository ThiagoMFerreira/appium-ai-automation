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

class CartPage(private val driver: AndroidDriver) {

    init {
        PageFactory.initElements(AppiumFieldDecorator(driver), this)
    }

    private val wait = WebDriverWait(driver, Duration.ofSeconds(10))

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/noItemTitleTV")
    private lateinit var emptyCartLabel: WebElement

    fun isDisplayed(): Boolean =
        runCatching {
            wait.until(ExpectedConditions.visibilityOf(emptyCartLabel))
            true
        }.getOrDefault(false)

    fun isCartEmpty(): Boolean =
        runCatching {
            wait.until(ExpectedConditions.visibilityOf(emptyCartLabel))
            true
        }.getOrDefault(false)

    fun isProductInCart(productName: String): Boolean {
        val matchingElements = driver.findElements(
            AppiumBy.androidUIAutomator("new UiSelector().textContains(\"$productName\")")
        )
        return matchingElements.isNotEmpty()
    }
}