from appium import webdriver
from appium.webdriver.common.appiumby import AppiumBy
from appium.options.android import UiAutomator2Options
import time

options = UiAutomator2Options()
options.platform_name = 'Android'
options.device_name = 'emulator-5554'
options.app_package = 'com.saucelabs.mydemoapp.android'
options.app_activity = 'com.saucelabs.mydemoapp.android.view.activities.SplashActivity'
options.automation_name = 'UiAutomator2'
options.no_reset = True

print('Starting session')
driver = webdriver.Remote('http://127.0.0.1:4723', options=options)
time.sleep(3)
print('Finding product images')
els = driver.find_elements(AppiumBy.ID, 'com.saucelabs.mydemoapp.android:id/productIV')
print('Found', len(els))
if els:
    els[0].click()
    time.sleep(3)
    src = driver.page_source
    with open('debug_after_click.xml','w') as f:
        f.write(src)
    print('Saved debug_after_click.xml')
else:
    print('No product elements found')

driver.quit()
