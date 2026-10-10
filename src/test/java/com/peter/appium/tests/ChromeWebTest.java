package com.peter.appium.tests;

import com.peter.appium.base.BaseTest;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.CapabilityType;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ChromeWebTest extends BaseTest {

    private static final String LOGIN_PAGE_URL = "https://the-internet.herokuapp.com/login";

    @Override
    protected UiAutomator2Options buildCapabilities() {
        UiAutomator2Options options = new UiAutomator2Options()
                // Driver that automates Android apps and browsers
                .setAutomationName("UiAutomator2")
                // Name shown for the target device
                .setDeviceName("Android Emulator");
        // Launch Chrome instead of a native app; no app or appPackage is set
        options.setCapability(CapabilityType.BROWSER_NAME, "Chrome");
        return options;
    }

    @Test
    public void shouldLogInThroughChromeLoginForm() {
        // Open the page in Chrome
        driver.get(LOGIN_PAGE_URL);

        // Locator by ID
        driver.findElement(By.id("username")).sendKeys("tomsmith");
        driver.findElement(By.id("password")).sendKeys("SuperSecretPassword!");
        // Locator by CSS selector
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Result: the site confirms the login in a flash message
        String message = driver.findElement(By.id("flash")).getText();
        Assert.assertTrue(message.contains("You logged into a secure area!"));
    }
}