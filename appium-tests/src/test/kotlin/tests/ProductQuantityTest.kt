package tests

import base.BaseTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pages.HomePage
import pages.ProductDetailPage

class ProductQuantityTest : BaseTest() {

    @Test
    fun `should increase product quantity to two on product detail screen`() {
        val homePage = HomePage(driver)
        assertTrue(homePage.isDisplayed(), "Home screen should be displayed before opening a product")

        homePage.openFirstProduct()

        val productDetailPage = ProductDetailPage(driver)
        assertTrue(productDetailPage.isDisplayed(), "Product detail screen should be displayed after opening a product")

        val initialQuantity = productDetailPage.getQuantity()
        assertEquals(1, initialQuantity, "Initial product quantity should be 1")

        productDetailPage.increaseQuantity()

        val updatedQuantity = productDetailPage.getQuantity()
        assertEquals(2, updatedQuantity, "Product quantity should be 2 after increment")
    }
}
