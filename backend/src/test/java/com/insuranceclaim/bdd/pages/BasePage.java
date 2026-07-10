package com.insuranceclaim.bdd.pages;

import com.insuranceclaim.bdd.hooks.ScenarioContext;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public abstract class BasePage {

    protected static final String BASE_URL = "http://localhost:5173";

    protected WebDriver driver() {
        return ScenarioContext.getDriver();
    }

    protected WebDriverWait getWait() {
        return ScenarioContext.getWait();
    }

    public void openApp() {
        driver().get(BASE_URL);
        getWait().until(ExpectedConditions.presenceOfElementLocated(By.className("app")));
    }

    public void navigateTo(String tabLabel) {
        List<WebElement> navItems = driver().findElements(By.className("nav-item"));
        for (WebElement item : navItems) {
            if (item.getText().contains(tabLabel)) {
                item.click();
                break;
            }
        }
        sleep(500);
    }

    public String getPageHeading() {
        return waitForVisible(By.tagName("h1")).getText();
    }

    public WebElement waitForVisible(By locator) {
        return getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForClickable(By locator) {
        return getWait().until(ExpectedConditions.elementToBeClickable(locator));
    }

    public boolean isElementPresent(By locator) {
        return !driver().findElements(locator).isEmpty();
    }

    public void clearAndType(WebElement element, String text) {
        element.clear();
        element.sendKeys(text);
    }

    public void clickButton(String buttonText) {
        List<WebElement> buttons = driver().findElements(By.tagName("button"));
        for (WebElement btn : buttons) {
            if (btn.getText().trim().equals(buttonText) && btn.isDisplayed() && btn.isEnabled()) {
                scrollIntoView(btn);
                btn.click();
                return;
            }
        }
        throw new RuntimeException("Button not found or not clickable: " + buttonText);
    }

    public void clickButtonContaining(String partialText) {
        List<WebElement> buttons = driver().findElements(By.tagName("button"));
        for (WebElement btn : buttons) {
            if (btn.getText().contains(partialText) && btn.isDisplayed() && btn.isEnabled()) {
                scrollIntoView(btn);
                btn.click();
                return;
            }
        }
        throw new RuntimeException("Button not found containing: " + partialText);
    }

    public void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver()).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", element);
        sleep(200);
    }

    public void sleep(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
