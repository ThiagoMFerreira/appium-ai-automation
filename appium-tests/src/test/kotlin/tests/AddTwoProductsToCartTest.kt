package tests

import base.BaseTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pages.HomePage
import pages.ProductDetailPage

class AddTwoProductsToCartTest : BaseTest() {

    @Test
    fun `add two products to cart and verify they are displayed`() {
        val homePage = HomePage(driver)

        // Add first product
        val productDetailPage = homePage.tapFirstProduct()
        assertTrue(productDetailPage.isDisplayed(), "Product detail page should be displayed")
        productDetailPage.tapAddToCart()

        // Go back to home
        driver.navigate().back()
        assertTrue(homePage.isDisplayed(), "Should be back on Home page")

        // Add second product (orange backpack)
        homePage.tapProductByInstance(2)
        assertTrue(productDetailPage.isDisplayed(), "Should be on Product Detail page")
        productDetailPage.tapAddToCart()

        // Go to cart
        val cartPage = homePage.tapCart()
        assertTrue(cartPage.isDisplayed(), "Should be on Cart page")
        assertTrue(cartPage.getCartItemCount() == 2, "Cart should contain 2 items")
    }
}
