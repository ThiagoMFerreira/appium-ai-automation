from appium import webdriver
from appium.webdriver.common.appiumby import AppiumBy
from appium.options.android import UiAutomator2Options
import time

options = UiAutomator2Options()
options.platform_name='Android'
options.device_name='emulator-5554'
options.app_package='com.saucelabs.mydemoapp.android'
options.app_activity='com.saucelabs.mydemoapp.android.view.activities.SplashActivity'
options.automation_name='UiAutomator2'
options.no_reset=True

print('Starting session')
driver = webdriver.Remote('http://127.0.0.1:4723', options=options)
time.sleep(2)

# select username
username = driver.find_element(AppiumBy.ID,'com.saucelabs.mydemoapp.android:id/username1TV')
print('username text', username.text)
username.click()
time.sleep(1)

# click login
login = driver.find_element(AppiumBy.ID,'com.saucelabs.mydemoapp.android:id/loginBtn')
print('login button text', login.text)
login.click()
time.sleep(3)

print('page source length after login', len(driver.page_source))
print('has product title', 'productTV' in driver.page_source)
print('has cart', 'cartRL' in driver.page_source)

# capture elements by possible IDs
for name in ['com.saucelabs.mydemoapp.android:id/productRV','com.saucelabs.mydemoapp.android:id/cartRL','com.saucelabs.mydemoapp.android:id/menuIV']:
    els = driver.find_elements(AppiumBy.ID,name)
    print(name, len(els))

# try click first product image if exists
els = driver.find_elements(AppiumBy.ID,'com.saucelabs.mydemoapp.android:id/productIV')
print('productIV count', len(els))
if els:
    els[0].click()
    time.sleep(2)
    print('after product click page source contains add?', 'Add' in driver.page_source, 'CHECKOUT' in driver.page_source)
    print(driver.page_source[:500])

# attempt to click cart button
cart = driver.find_element(AppiumBy.ACCESSIBILITY_ID,'View cart')
cart.click()
time.sleep(2)
print('after cart click contains checkout', 'CHECKOUT' in driver.page_source, 'Login' in driver.page_source)
print(driver.page_source[:1000])

driver.quit()
