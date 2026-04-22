package pages

import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.pagefactory.AndroidFindBy
import io.appium.java_client.pagefactory.AppiumFieldDecorator
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.PageFactory
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

class AboutPage(private val driver: AndroidDriver) {

    init {
        PageFactory.initElements(AppiumFieldDecorator(driver), this)
    }

    private val wait = WebDriverWait(driver, Duration.ofSeconds(10))

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/aboutTV")
    private lateinit var aboutTitle: WebElement

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/versionTV")
    private lateinit var versionText: WebElement

    fun isDisplayed(): Boolean =
        runCatching {
            wait.until(ExpectedConditions.visibilityOf(aboutTitle))
            wait.until(ExpectedConditions.visibilityOf(versionText))
            aboutTitle.text == "About"
        }.getOrDefault(false)
}
