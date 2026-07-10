package com.insuranceclaim.bdd.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class DashboardPage extends BasePage {

    public void open() {
        openApp();
    }

    public int getStatCardCount() {
        waitForVisible(By.className("stat-grid"));
        return driver().findElements(By.className("stat-card")).size();
    }

    public List<String> getStatLabels() {
        waitForVisible(By.className("stat-grid"));
        return driver().findElements(By.className("stat-label")).stream()
                .map(WebElement::getText).toList();
    }

    public List<String> getStatValues() {
        waitForVisible(By.className("stat-grid"));
        return driver().findElements(By.className("stat-value")).stream()
                .map(e -> e.getText().trim()).toList();
    }

    public String getStatValue(String label) {
        List<WebElement> statCards = driver().findElements(By.className("stat-card"));
        for (WebElement card : statCards) {
            String cardLabel = card.findElement(By.className("stat-label")).getText();
            if (cardLabel.equalsIgnoreCase(label)) {
                return card.findElement(By.className("stat-value")).getText().trim();
            }
        }
        throw new RuntimeException("Stat card not found for label: " + label);
    }

    public boolean hasEmptyMessage() {
        return isElementPresent(
                By.xpath("//p[contains(@class,'muted') and contains(text(),'No claims yet')]"));
    }

    public boolean hasClaimRows() {
        return !driver().findElements(By.className("list-row")).isEmpty();
    }

    public boolean hasAgentWorkflowSection() {
        return isElementPresent(By.xpath("//h3[normalize-space(text())='Agent Workflow']"));
    }

    public int getGraphNodeCount() {
        waitForVisible(By.className("graph-node"));
        return driver().findElements(By.className("graph-node")).size();
    }

    public String getSubheading() {
        waitForVisible(By.className("page-head"));
        return waitForVisible(
                By.xpath("//div[contains(@class,'page-head')]//p[contains(@class,'muted')]")).getText().trim();
    }

    public void clickFirstClaimRow() {
        WebElement listRow = waitForClickable(By.className("list-row"));
        scrollIntoView(listRow);
        listRow.click();
        sleep(500);
    }
}
