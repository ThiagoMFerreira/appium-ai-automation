package tests

import base.BaseTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pages.HomePage

class AppLaunchTest : BaseTest() {

    @Test
    fun `app launches successfully and displays home screen`() {
        val homePage = HomePage(driver)

        // Validate that the app launched and home screen is displayed
        assertTrue(homePage.isDisplayed(), "Home screen should be displayed after app launch")

        // Additional validations for home screen elements
        assertTrue(homePage.isAppLogoVisible(), "App logo should be visible on home screen")
        assertTrue(homePage.isProductsTitleVisible(), "Products title should be visible on home screen")
        assertTrue(homePage.isProductsListVisible(), "Products list should be visible on home screen")
    }

    @Test
    fun `app package and activity are correct`() {
        // This test validates that the app started with the correct package/activity
        // If this fails, it means the appPackage or appActivity in BaseTest is wrong
        val homePage = HomePage(driver)
        assertTrue(homePage.isDisplayed(), "App should launch with correct package and activity")
    }
}