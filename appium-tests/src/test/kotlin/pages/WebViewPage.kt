package pages

import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.pagefactory.AndroidFindBy
import io.appium.java_client.pagefactory.AppiumFieldDecorator
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.PageFactory
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

class WebViewPage(private val driver: AndroidDriver) {

    init {
        PageFactory.initElements(AppiumFieldDecorator(driver), this)
    }

    private val wait = WebDriverWait(driver, Duration.ofSeconds(10))

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/webViewTV")
    private lateinit var webViewTitle: WebElement

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/urlET")
    private lateinit var urlEditText: WebElement

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/goBtn")
    private lateinit var goButton: WebElement

    fun isDisplayed(): Boolean =
        runCatching {
            wait.until(ExpectedConditions.visibilityOf(webViewTitle))
            webViewTitle.text == "Webview"
        }.getOrDefault(false)

    fun enterUrl(url: String): WebViewPage {
        wait.until(ExpectedConditions.visibilityOf(urlEditText))
        urlEditText.sendKeys(url)
        return this
    }

    fun tapGoToSite(): WebViewPage {
        wait.until(ExpectedConditions.elementToBeClickable(goButton))
        goButton.click()
        return this
    }
}