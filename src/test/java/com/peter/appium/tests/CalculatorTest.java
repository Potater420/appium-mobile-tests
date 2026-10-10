package com.peter.appium.tests;

import com.peter.appium.base.BaseTest;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CalculatorTest extends BaseTest {

    @Override
    protected UiAutomator2Options buildCapabilities() {
        return new UiAutomator2Options()
                // Driver that automates Android apps
                .setAutomationName("UiAutomator2")
                // Name shown for the target device
                .setDeviceName("Android Emulator")
                // Package and launch activity of the built-in Calculator app
                .setAppPackage("com.google.android.calculator")
                .setAppActivity("com.android.calculator2.Calculator");
    }

    @Test
    public void shouldAddTwoAndThreeAndShowFive() {
        // Locator by resource ID
        driver.findElement(AppiumBy.id("com.google.android.calculator:id/digit_2")).click();
        // Locator by accessibility ID (content-desc)
        driver.findElement(AppiumBy.accessibilityId("plus")).click();
        driver.findElement(AppiumBy.id("com.google.android.calculator:id/digit_3")).click();
        // Locator by XPath
        driver.findElement(AppiumBy.xpath("//*[@content-desc='equals']")).click();

        String result = driver.findElement(
                AppiumBy.id("com.google.android.calculator:id/result_final")).getText();
        Assert.assertEquals(result, "5");
    }
}