# Appium Java Cheat Sheet

Android, `io.appium:java-client:10.0.0`, UiAutomator2, TestNG.

## Contents
1. [Setup and imports](#1-setup-and-imports)
2. [Desired Capabilities](#2-desired-capabilities)
3. [Starting the driver and server](#3-starting-the-driver-and-server)
4. [Element locator strategies](#4-element-locator-strategies)
5. [Common element actions](#5-common-element-actions)
6. [Common driver commands](#6-common-driver-commands)
7. [Waits](#7-waits)
8. [Gesture automation](#8-gesture-automation)
9. [Web testing (Chrome) and hybrid apps](#9-web-testing-chrome-and-hybrid-apps)
10. [Old API vs current API](#10-old-api-vs-current-api)
11. [Handy command line tools](#11-handy-command-line-tools)

---

## 1. Setup and imports

```xml
<!-- pom.xml -->
<dependency>
    <groupId>io.appium</groupId>
    <artifactId>java-client</artifactId>
    <version>10.0.0</version>
</dependency>
```

```java
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
```

## 2. Desired Capabilities

In java-client 10 capabilities are set with `UiAutomator2Options`, not `DesiredCapabilities`. `platformName` is set to Android automatically.

**Built-in app (package and activity)**
```java
UiAutomator2Options options = new UiAutomator2Options()
        .setAutomationName("UiAutomator2")
        .setDeviceName("Android Emulator")
        .setAppPackage("com.google.android.calculator")
        .setAppActivity("com.android.calculator2.Calculator");
```

**APK file (Appium installs it, then launches it)**
```java
String apkPath = Paths.get("apps", "ApiDemos-debug.apk").toAbsolutePath().toString();
UiAutomator2Options options = new UiAutomator2Options()
        .setAutomationName("UiAutomator2")
        .setDeviceName("Android Emulator")
        .setApp(apkPath);
```

**Real device (serial from `adb devices`)**
```java
options.setUdid("R58M12ABCDE");
```

**Platform version**
```java
options.setPlatformVersion("14");
```

**Keep app data between sessions / do not reinstall**
```java
options.setNoReset(true);
```

**Wipe app data at the start of a session**
```java
options.setFullReset(true);
```

**Session idle timeout**
```java
options.setNewCommandTimeout(Duration.ofSeconds(120));
```

**Any capability by name**
```java
options.setCapability("appium:autoGrantPermissions", true);
```

## 3. Starting the driver and server

**Connect to a server you started yourself (`appium` in a terminal)**
```java
URL serverUrl = new URI("http://127.0.0.1:4723").toURL();
AndroidDriver driver = new AndroidDriver(serverUrl, options);
```

**Let the test start and stop the server**
```java
AppiumDriverLocalService service = new AppiumServiceBuilder()
        .usingAnyFreePort()
        .build();
service.start();
AndroidDriver driver = new AndroidDriver(service.getUrl(), options);
// ...
driver.quit();
service.stop();
```

**End the session**
```java
driver.quit();
```

**TestNG base class pattern**
```java
@BeforeMethod
public void startSession() throws Exception {
    driver = new AndroidDriver(new URI("http://127.0.0.1:4723").toURL(), buildCapabilities());
}

@AfterMethod(alwaysRun = true)
public void endSession() {
    if (driver != null) {
        driver.quit();
    }
}
```

## 4. Element locator strategies

```java
// Resource ID
driver.findElement(AppiumBy.id("com.google.android.calculator:id/digit_2"));

// Accessibility ID (content-desc)
driver.findElement(AppiumBy.accessibilityId("Views"));

// XPath
driver.findElement(AppiumBy.xpath("//android.widget.TextView[@text='Views']"));

// Class name
driver.findElement(AppiumBy.className("android.widget.CheckBox"));

// UiAutomator selector (Android only)
driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().text(\"Views\")"));

// Image (base64 encoded template image)
driver.findElement(AppiumBy.image(base64EncodedImage));

// Several elements (returns an empty list if none match)
List<WebElement> checkBoxes = driver.findElements(AppiumBy.className("android.widget.CheckBox"));
```

**UiSelector variations**
```java
AppiumBy.androidUIAutomator("new UiSelector().textContains(\"View\")");
AppiumBy.androidUIAutomator("new UiSelector().description(\"Seek Bar\")");
AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.Button\").instance(1)");
AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"android:id/checkbox\")");
```

**Scroll until an element is on screen**
```java
driver.findElement(AppiumBy.androidUIAutomator(
        "new UiScrollable(new UiSelector().scrollable(true))"
        + ".scrollIntoView(new UiSelector().text(\"Views\"))"));
```

**Child element inside a parent**
```java
WebElement row = driver.findElement(AppiumBy.id("com.app:id/row"));
WebElement title = row.findElement(AppiumBy.id("com.app:id/title"));
```

Use Appium Inspector to read the ID, accessibility ID and XPath of an element.

## 5. Common element actions

```java
element.click();                           // tap
element.sendKeys("text to type");          // type
element.clear();                           // clear a field
String text = element.getText();           // visible text
String desc = element.getAttribute("content-desc");
boolean shown = element.isDisplayed();
boolean enabled = element.isEnabled();
boolean selected = element.isSelected();   // checkbox / toggle state
Rectangle box = element.getRect();         // x, y, width, height
```

## 6. Common driver commands

**Alerts**
```java
driver.executeScript("mobile: acceptAlert");    // native Android dialog
driver.executeScript("mobile: dismissAlert");
driver.switchTo().alert().accept();             // alert inside a web page (Chrome)
```

**Keys and keyboard**
```java
driver.pressKey(new KeyEvent(AndroidKey.BACK));
driver.pressKey(new KeyEvent(AndroidKey.HOME));
driver.pressKey(new KeyEvent(AndroidKey.ENTER));
driver.hideKeyboard();
boolean keyboardOpen = driver.isKeyboardShown();
```

**Orientation**
```java
driver.rotate(ScreenOrientation.LANDSCAPE);
driver.rotate(ScreenOrientation.PORTRAIT);
```

**Clipboard**
```java
driver.setClipboardText("1234");
String clipboard = driver.getClipboardText();
```

**App management**
```java
driver.installApp("C:\\path\\to\\app.apk");
boolean installed = driver.isAppInstalled("com.example.android.apis");
driver.activateApp("com.example.android.apis");   // bring to foreground
driver.terminateApp("com.example.android.apis");  // close it
driver.removeApp("com.example.android.apis");     // uninstall
```

**Current screen information**
```java
String currentPackage = driver.getCurrentPackage();
String currentActivity = driver.currentActivity();
String pageSource = driver.getPageSource();   // XML of the screen
```

**Screenshot**
```java
File screenshot = driver.getScreenshotAs(OutputType.FILE);
Files.copy(screenshot.toPath(), Paths.get("screenshots", "result.png"),
        StandardCopyOption.REPLACE_EXISTING);
```

**Notifications and device state**
```java
driver.openNotifications();
```

**Update driver settings**
```java
driver.setSetting("waitForIdleTimeout", 0);   // do not wait for the UI to go idle
```

## 7. Waits

```java
// Explicit wait: preferred
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
WebElement title = wait.until(
        ExpectedConditions.visibilityOfElementLocated(AppiumBy.id("com.app:id/title")));

// Implicit wait: applies to every findElement
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
```

Do not mix implicit and explicit waits, because the timeouts can add up.

## 8. Gesture automation

All of these use UiAutomator2 `mobile:` commands through `executeScript`, which is the recommended approach.

**Tap**
```java
driver.executeScript("mobile: clickGesture",
        Map.of("elementId", ((RemoteWebElement) element).getId()));
```

**Tap at coordinates**
```java
driver.executeScript("mobile: clickGesture", Map.of("x", 200, "y", 600));
```

**Double tap**
```java
driver.executeScript("mobile: doubleClickGesture",
        Map.of("elementId", ((RemoteWebElement) element).getId()));
```

**Long press**
```java
driver.executeScript("mobile: longClickGesture",
        Map.of("elementId", ((RemoteWebElement) element).getId(), "duration", 1500));
```

**Scroll** (returns `true` while more content can be scrolled, so loop until `false`)
```java
boolean canScrollMore = true;
while (canScrollMore) {
    canScrollMore = (Boolean) driver.executeScript("mobile: scrollGesture", Map.of(
            "elementId", ((RemoteWebElement) list).getId(),
            "direction", "down",
            "percent", 1.0));
}
```

**Swipe / slide**
```java
driver.executeScript("mobile: swipeGesture", Map.of(
        "elementId", ((RemoteWebElement) element).getId(),
        "direction", "left",    // "up", "down", "left", "right"
        "percent", 0.75));
```

**Slide a seek bar (drag between coordinates)**
```java
Rectangle bar = seekBar.getRect();
driver.executeScript("mobile: dragGesture", Map.of(
        "startX", bar.x + bar.width / 2,
        "startY", bar.y + bar.height / 2,
        "endX", bar.x + bar.width - 5,
        "endY", bar.y + bar.height / 2,
        "speed", 500));
```

**Drag and drop onto another element**
```java
Rectangle target = targetElement.getRect();
driver.executeScript("mobile: dragGesture", Map.of(
        "elementId", ((RemoteWebElement) source).getId(),
        "endX", target.x + target.width / 2,
        "endY", target.y + target.height / 2));
```

**Pinch (zoom)**
```java
driver.executeScript("mobile: pinchOpenGesture", Map.of(
        "elementId", ((RemoteWebElement) element).getId(), "percent", 0.75));
driver.executeScript("mobile: pinchCloseGesture", Map.of(
        "elementId", ((RemoteWebElement) element).getId(), "percent", 0.75));
```

**Custom gesture with W3C Actions** (for anything the commands above do not cover)
```java
PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
Sequence swipe = new Sequence(finger, 0)
        .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), 500, 1500))
        .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
        .addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), 500, 500))
        .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
driver.perform(List.of(swipe));
```

**Two fingers at once (multi-touch)**
```java
driver.perform(List.of(sequenceForFingerOne, sequenceForFingerTwo));
```

## 9. Web testing (Chrome) and hybrid apps

**Chrome on Android**
```java
UiAutomator2Options options = new UiAutomator2Options()
        .setAutomationName("UiAutomator2")
        .setDeviceName("Android Emulator");
options.setCapability(CapabilityType.BROWSER_NAME, "Chrome");   // no app or appPackage
```

**Start the server so it can fetch a matching ChromeDriver**
```
appium --allow-insecure=uiautomator2:chromedriver_autodownload
```

**Use it like Selenium**
```java
driver.get("https://the-internet.herokuapp.com/login");
driver.findElement(By.id("username")).sendKeys("tomsmith");
driver.findElement(By.cssSelector("button[type='submit']")).click();
String message = driver.findElement(By.id("flash")).getText();
driver.getCurrentUrl();
driver.getTitle();
driver.navigate().back();
driver.navigate().refresh();
```

**Hybrid apps: switch between native and WebView**
```java
Set<String> contexts = driver.getContextHandles();
String webView = contexts.stream().filter(c -> c.contains("WEBVIEW")).findFirst().orElse(null);
driver.context(webView);          // web locators (By.cssSelector, ...) now work
driver.context("NATIVE_APP");     // back to native locators (AppiumBy.id, ...)
```

## 10. Old API vs current API

Older tutorials use these calls. They do not compile with java-client 10.

| Old (java-client 7 and earlier) | Current (java-client 10) |
|---|---|
| `DesiredCapabilities` + `MobileCapabilityType.*` | `UiAutomator2Options` setters |
| `AndroidDriver<AndroidElement>` | `AndroidDriver` |
| `driver.findElementById("x")` | `driver.findElement(AppiumBy.id("x"))` |
| `driver.findElementByAccessibilityId("x")` | `driver.findElement(AppiumBy.accessibilityId("x"))` |
| `driver.findElementByXPath("x")` | `driver.findElement(AppiumBy.xpath("x"))` |
| `driver.findElementByAndroidUIAutomator("x")` | `driver.findElement(AppiumBy.androidUIAutomator("x"))` |
| `TouchAction` / `MultiTouchAction` | `mobile:` gesture commands or W3C `Sequence` |
| `PointOption`, `WaitOptions` | `PointerInput` and `Duration` |
| `new URL("http://127.0.0.1:4723/wd/hub")` | `new URI("http://127.0.0.1:4723").toURL()` |

## 11. Handy command line tools

```
appium                                      # start the server
appium driver doctor uiautomator2           # check the environment
adb devices                                 # list devices and emulators
adb shell dumpsys window | findstr mCurrentFocus   # current package/activity (Windows)
adb shell pm list packages                  # all installed package names
mvn test -Dtest=ChromeWebTest               # run a single test class
```
