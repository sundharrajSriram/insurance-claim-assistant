package com.insuranceclaim.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Base class for all Selenium UI tests.
 * Manages Chrome WebDriver lifecycle and provides common helper methods.
 *
 * Prerequisites:
 *   - Backend must be running on port 8080 (mvnw spring-boot:run)
 *   - Frontend must be running on port 5173 (npm run dev)
 */
public abstract class BaseSeleniumTest {

    protected static final String BASE_URL = "http://localhost:5173";
    protected static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);
    protected static final Duration POLL_INTERVAL = Duration.ofMillis(300);

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeAll
    static void setupDriver() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-extensions");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        wait = new WebDriverWait(driver, WAIT_TIMEOUT, POLL_INTERVAL);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ── Navigation helpers ──────────────────────────────────────────

    protected void openApp() {
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("app")));
    }

    protected void navigateTo(String tabLabel) {
        List<WebElement> navItems = driver.findElements(By.className("nav-item"));
        for (WebElement item : navItems) {
            if (item.getText().contains(tabLabel)) {
                item.click();
                break;
            }
        }
        sleep(500); // allow React state to settle
    }

    // ── Wait helpers ────────────────────────────────────────────────

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void waitForText(By locator, String text) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    protected boolean isElementPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    protected void waitForElementAbsent(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    // ── Interaction helpers ─────────────────────────────────────────

    protected void clearAndType(WebElement element, String text) {
        element.clear();
        element.sendKeys(text);
    }

    protected void clickButton(String buttonText) {
        List<WebElement> buttons = driver.findElements(By.tagName("button"));
        for (WebElement btn : buttons) {
            if (btn.getText().trim().equals(buttonText) && btn.isDisplayed() && btn.isEnabled()) {
                scrollIntoView(btn);
                btn.click();
                return;
            }
        }
        throw new RuntimeException("Button not found or not clickable: " + buttonText);
    }

    protected void clickButtonContaining(String partialText) {
        List<WebElement> buttons = driver.findElements(By.tagName("button"));
        for (WebElement btn : buttons) {
            if (btn.getText().contains(partialText) && btn.isDisplayed() && btn.isEnabled()) {
                scrollIntoView(btn);
                btn.click();
                return;
            }
        }
        throw new RuntimeException("Button not found containing: " + partialText);
    }

    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", element);
        sleep(200);
    }

    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ── Assertion helpers ───────────────────────────────────────────

    protected String getPageHeading() {
        return waitForVisible(By.tagName("h1")).getText();
    }

    protected List<WebElement> getStatCards() {
        return driver.findElements(By.className("stat-card"));
    }

    protected List<WebElement> getBadges() {
        return driver.findElements(By.className("badge"));
    }

    protected int countElements(By locator) {
        return driver.findElements(locator).size();
    }
}
