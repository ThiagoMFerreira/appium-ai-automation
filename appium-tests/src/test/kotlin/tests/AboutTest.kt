package tests

import base.BaseTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pages.HomePage

class AboutTest : BaseTest() {

    @Test
    fun shouldNavigateToAboutPageAndVerifyContent() {
        val homePage = HomePage(driver)

        // 1. Abrir o menu e clicar na opção "About"
        val aboutPage = homePage.tapAbout()

        // 2. Verificar que a página "About" está carregada
        assertTrue(aboutPage.isDisplayed(), "A página About não foi exibida corretamente.")
    }
}
