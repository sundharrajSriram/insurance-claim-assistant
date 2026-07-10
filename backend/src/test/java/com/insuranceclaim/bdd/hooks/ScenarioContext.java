package com.insuranceclaim.bdd.hooks;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<WebDriverWait> waitThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, Object>> contextData = ThreadLocal.withInitial(HashMap::new);

    public static void setDriver(WebDriver driver) {
        driverThreadLocal.set(driver);
        waitThreadLocal.set(new WebDriverWait(driver, Duration.ofSeconds(10), Duration.ofMillis(300)));
    }

    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    public static WebDriverWait getWait() {
        return waitThreadLocal.get();
    }

    public static void put(String key, Object value) {
        contextData.get().put(key, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        return (T) contextData.get().get(key);
    }

    public static void clear() {
        driverThreadLocal.remove();
        waitThreadLocal.remove();
        contextData.get().clear();
        contextData.remove();
    }
}
