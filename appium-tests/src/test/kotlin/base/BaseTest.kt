package base

import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.options.UiAutomator2Options
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.api.extension.TestWatcher
import org.openqa.selenium.OutputType
import java.io.File
import java.net.URL
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

open class BaseTest {

    protected lateinit var driver: AndroidDriver

    @JvmField
    @RegisterExtension
    val watcher = object : TestWatcher {
        override fun testFailed(context: ExtensionContext, cause: Throwable?) {
            takeScreenshot("failed_${context.displayName}")
        }
    }

    @BeforeEach
    fun setUp() {
        // Configurar variáveis de ambiente para Appium
        System.setProperty("ANDROID_SDK_ROOT", "/Users/userName/Library/Android/sdk")

        val options = UiAutomator2Options().apply {
            setPlatformName("Android")
            setDeviceName("emulator-5554")
            setAppPackage("com.saucelabs.mydemoapp.android")
            setAppActivity("com.saucelabs.mydemoapp.android.view.activities.SplashActivity")
            setAutomationName("UiAutomator2")
            setNewCommandTimeout(Duration.ofSeconds(60))
            setNoReset(false)
            setFullReset(false)
        }

        driver = AndroidDriver(URL("http://127.0.0.1:4723"), options)
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5))
    }

    @AfterEach
    fun tearDown() {
        if (::driver.isInitialized) driver.quit()
    }

    fun takeScreenshot(fileName: String) {
        if (::driver.isInitialized) {
            val screenshot = driver.getScreenshotAs(OutputType.FILE)
            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
            val destFile = File("screenshot-failures/${fileName}_${timestamp}.png")
            screenshot.copyTo(destFile)
        }
    }
}