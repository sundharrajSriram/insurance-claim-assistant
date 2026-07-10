package com.insuranceclaim.bdd.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class HistoryPage extends BasePage {

    public void open() {
        openApp();
        navigateTo("History");
        sleep(500);
    }

    public List<String> getFilterTabLabels() {
        return driver().findElements(By.className("filter-tab")).stream()
                .map(WebElement::getText).toList();
    }

    public String getActiveFilterLabel() {
        List<WebElement> active = driver().findElements(By.className("filter-tab-active"));
        return active.isEmpty() ? "" : active.get(0).getText();
    }

    public boolean hasSearchInput() {
        return isElementPresent(By.cssSelector("input[placeholder='Search id, name, policy...']"));
    }

    public void search(String query) {
        WebElement searchInput = waitForVisible(
                By.cssSelector("input[placeholder='Search id, name, policy...']"));
        clearAndType(searchInput, query);
        sleep(500);
    }

    public String getTableBodyText() {
        return waitForVisible(By.cssSelector(".table-history tbody")).getText();
    }

    public List<String> getTableHeaders() {
        waitForVisible(By.className("table-history"));
        return driver().findElements(By.cssSelector(".table-history th")).stream()
                .map(h -> h.getText().toUpperCase()).toList();
    }

    public boolean tableContainsText(String text) {
        waitForVisible(By.className("table-history"));
        List<WebElement> cells = driver().findElements(By.cssSelector(".table-history td"));
        return cells.stream().anyMatch(td -> td.getText().contains(text));
    }

    public void clickFilterTab(String tabName) {
        List<WebElement> filterTabs = driver().findElements(By.className("filter-tab"));
        for (WebElement tab : filterTabs) {
            if (tab.getText().toUpperCase().contains(tabName.toUpperCase())) {
                tab.click();
                break;
            }
        }
        sleep(500);
    }

    public boolean hasBadgeWithText(String badgeText) {
        List<WebElement> badges = driver().findElements(By.className("badge"));
        return badges.stream().anyMatch(b -> b.getText().equalsIgnoreCase(badgeText));
    }

    public void clickExpandButton() {
        WebElement expandBtn = waitForClickable(By.className("expand-btn"));
        scrollIntoView(expandBtn);
        expandBtn.click();
        sleep(500);
    }

    public String getExpandPanelText() {
        WebElement panel = waitForVisible(By.className("history-expand"));
        return panel.getText();
    }

    public boolean hasNodeBadges() {
        WebElement panel = waitForVisible(By.className("history-expand"));
        return !panel.findElements(By.className("hist-node-badge")).isEmpty();
    }
}
