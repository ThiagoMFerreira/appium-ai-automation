package tests

import base.BaseTest
import io.appium.java_client.AppiumBy
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pages.CartPage
import pages.HomePage
import pages.ProductDetailPage

class CartFlowTest : BaseTest() {

    @Test
    fun `should add product to cart and validate it in cart screen`() {
        val homePage = HomePage(driver)
        assertTrue(homePage.isDisplayed(), "Home screen should be displayed before starting cart flow")

        homePage.openFirstProduct()

        val productDetailPage = ProductDetailPage(driver)
        assertTrue(productDetailPage.isDisplayed(), "Product detail screen should be displayed after opening a product")

        val productName = productDetailPage.getProductTitle()
        assertTrue(productName.isNotBlank(), "Product title should be visible on detail screen")

        productDetailPage.addToCart()

        driver.findElement(AppiumBy.id("com.saucelabs.mydemoapp.android:id/cartRL")).click()

        val cartPage = CartPage(driver)
        assertFalse(cartPage.isCartEmpty(), "Cart should not be empty after adding a product")
        assertTrue(
            cartPage.isProductInCart(productName),
            "The product '$productName' should be visible in the cart"
        )
    }
}
