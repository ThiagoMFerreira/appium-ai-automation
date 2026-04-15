from appium import webdriver
from appium.options.android import UiAutomator2Options

options = UiAutomator2Options()
options.platform_name = 'Android'
options.device_name = 'emulator-5554'
options.app_package = 'com.saucelabs.mydemoapp.android'
options.app_activity = 'com.saucelabs.mydemoapp.android.view.activities.SplashActivity'
options.automation_name = 'UiAutomator2'
options.no_reset = True

try:
    driver = webdriver.Remote('http://localhost:4723', options=options)
    print("✅ Sessão criada com sucesso!")
    
    # Verificar se estamos na tela inicial
    from appium.webdriver.common.appiumby import AppiumBy
    elements = driver.find_elements(AppiumBy.ID, 'com.saucelabs.mydemoapp.android:id/productTV')
    if elements:
        print("✅ Título 'Products' encontrado!")
        print(f"   Texto: {elements[0].text}")
    else:
        print("❌ Título 'Products' não encontrado")
    
    driver.quit()
    print("✅ Teste concluído com sucesso!")
    
except Exception as e:
    print(f"❌ Erro: {e}")
