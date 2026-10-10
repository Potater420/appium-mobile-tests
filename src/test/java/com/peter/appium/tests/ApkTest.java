package com.peter.appium.tests;

import com.peter.appium.base.BaseTest;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ApkTest extends BaseTest {

    @Override
    protected UiAutomator2Options buildCapabilities() {
        return apiDemosCapabilities();
    }

    @Test
    public void shouldEnterWifiNameInPreferenceDependencies() {
        // Locator by accessibility ID
        driver.findElement(AppiumBy.accessibilityId("Preference")).click();
        driver.findElement(AppiumBy.accessibilityId("3. Preference dependencies")).click();
        // Locator by resource ID
        driver.findElement(AppiumBy.id("android:id/checkbox")).click();
        // Locator by XPath
        driver.findElement(AppiumBy.xpath("//android.widget.TextView[@text='WiFi settings']")).click();

        String dialogTitle = driver.findElement(AppiumBy.id("android:id/alertTitle")).getText();
        Assert.assertEquals(dialogTitle, "WiFi settings");

        driver.findElement(AppiumBy.id("android:id/edit")).sendKeys("MyHomeWifi");
        driver.findElement(AppiumBy.id("android:id/button1")).click();
    }
}