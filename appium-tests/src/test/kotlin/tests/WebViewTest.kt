package tests

import base.BaseTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pages.HomePage
import pages.WebViewPage

class WebViewTest : BaseTest() {

    @Test
    fun `test open web view and verify elements`() {
        val homePage = HomePage(driver)
        assertTrue(homePage.isDisplayed(), "Home page should be displayed")

        val webViewPage = homePage.tapWebView()
        assertTrue(webViewPage.isDisplayed(), "WebView page should be displayed")

        webViewPage.enterUrl("https://www.google.com")
        webViewPage.tapGoToSite()
        
        // Verifying we are still on WebView page after tapping Go
        assertTrue(webViewPage.isDisplayed(), "Should still be on WebView page after entering URL")
    }
}