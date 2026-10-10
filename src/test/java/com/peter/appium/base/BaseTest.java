package com.peter.appium.base;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;

public abstract class BaseTest {

    protected AndroidDriver driver;

    // Each test class supplies its own capabilities (which app to launch)
    protected abstract UiAutomator2Options buildCapabilities();

    // Capabilities for the ApiDemos APK, shared by the APK and gesture tests
    protected UiAutomator2Options apiDemosCapabilities() {
        // Full path of the APK stored in this project's apps folder
        String apkPath = Paths.get("apps", "ApiDemos-debug.apk").toAbsolutePath().toString();
        UiAutomator2Options options = new UiAutomator2Options()
                // Driver that automates Android apps
                .setAutomationName("UiAutomator2")
                // Name shown for the target device
                .setDeviceName("Android Emulator")
                // Appium installs this APK and then launches it
                .setApp(apkPath);
        // Some real phones block the hidden API setting; continue instead of failing
        options.setCapability("appium:ignoreHiddenApiPolicyError", true);
        return options;
    }

    @BeforeMethod
    public void startSession() throws Exception {
        // Address of the Appium server running on this PC
        URL appiumServerUrl = new URI("http://127.0.0.1:4723").toURL();
        driver = new AndroidDriver(appiumServerUrl, buildCapabilities());
    }

    @AfterMethod(alwaysRun = true)
    public void endSession() {
        if (driver != null) {
            driver.quit();
        }
    }
}