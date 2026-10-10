package com.peter.appium.tests;

import com.peter.appium.base.BaseTest;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class GestureTest extends BaseTest {

    private static final String APP_ID = "io.appium.android.apis:id/";

    @Override
    protected UiAutomator2Options buildCapabilities() {
        return apiDemosCapabilities();
    }

    @Test
    public void shouldOpenViewsScreenWithTapGesture() {
        // Locator by accessibility ID
        WebElement viewsItem = driver.findElement(AppiumBy.accessibilityId("Views"));
        // Tap gesture on the "Views" list item
        driver.executeScript("mobile: clickGesture",
                Map.of("elementId", ((RemoteWebElement) viewsItem).getId()));

        // Result: the Views screen is open, so its "Drag and Drop" item is visible
        Assert.assertTrue(driver.findElement(AppiumBy.accessibilityId("Drag and Drop")).isDisplayed());
    }

    @Test
    public void shouldScrollViewsListToTheBottom() {
        driver.findElement(AppiumBy.accessibilityId("Views")).click();
        // Locator by class name
        WebElement viewsList = driver.findElement(AppiumBy.className("android.widget.ListView"));
        Assert.assertFalse(driver.findElements(AppiumBy.accessibilityId("Drag and Drop")).isEmpty());

        // Scroll gesture, repeated until the list cannot scroll any further
        boolean canScrollMore = true;
        while (canScrollMore) {
            canScrollMore = (Boolean) driver.executeScript("mobile: scrollGesture", Map.of(
                    "elementId", ((RemoteWebElement) viewsList).getId(),
                    "direction", "down",
                    "percent", 1.0));
        }

        // Result: the first items scrolled out of view
        Assert.assertTrue(driver.findElements(AppiumBy.accessibilityId("Drag and Drop")).isEmpty());
    }

    @Test
    public void shouldMoveSeekBarWithSlideGesture() {
        driver.findElement(AppiumBy.accessibilityId("Views")).click();
        // Locator by UiAutomator: scroll the list until "Seek Bar" is on screen
        driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))"
                        + ".scrollIntoView(new UiSelector().description(\"Seek Bar\"))")).click();

        WebElement seekBar = driver.findElement(AppiumBy.id(APP_ID + "seek"));
        WebElement progressText = driver.findElement(AppiumBy.id(APP_ID + "progress"));
        String progressBefore = progressText.getText();

        // Slide gesture from the middle of the bar to its right end
        Rectangle bar = seekBar.getRect();
        driver.executeScript("mobile: dragGesture", Map.of(
                "startX", bar.x + bar.width / 2,
                "startY", bar.y + bar.height / 2,
                "endX", bar.x + bar.width - 5,
                "endY", bar.y + bar.height / 2,
                "speed", 500));

        // Result: the progress text changed
        Assert.assertNotEquals(progressText.getText(), progressBefore);
    }

    @Test
    public void shouldDropFirstDotOnSecondDot() {
        driver.findElement(AppiumBy.accessibilityId("Views")).click();
        driver.findElement(AppiumBy.accessibilityId("Drag and Drop")).click();

        WebElement firstDot = driver.findElement(AppiumBy.id(APP_ID + "drag_dot_1"));
        WebElement secondDot = driver.findElement(AppiumBy.id(APP_ID + "drag_dot_2"));
        Rectangle target = secondDot.getRect();

        // Drag and drop gesture: pick up the first dot and release it on the second
        driver.executeScript("mobile: dragGesture", Map.of(
                "elementId", ((RemoteWebElement) firstDot).getId(),
                "endX", target.x + target.width / 2,
                "endY", target.y + target.height / 2));

        // Result: the app confirms the drop
        String result = driver.findElement(AppiumBy.id(APP_ID + "drag_result_text")).getText();
        Assert.assertEquals(result, "Dropped!");
    }
}