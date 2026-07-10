package com.insuranceclaim.bdd.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ClaimDetailPage extends BasePage {

    public boolean isDrawerVisible() {
        return isElementPresent(By.className("drawer-overlay"));
    }

    public String getDrawerClaimId() {
        return waitForVisible(By.className("drawer-id")).getText();
    }

    public String getDrawerCustomerName() {
        waitForVisible(By.className("drawer-header"));
        return driver().findElement(By.cssSelector(".drawer-header h2")).getText();
    }

    public boolean hasDecisionBadges() {
        WebElement section = waitForVisible(By.className("detail-decision"));
        return !section.findElements(By.className("badge")).isEmpty();
    }

    public int getFieldCount() {
        waitForVisible(By.className("detail-grid"));
        return driver().findElements(By.cssSelector(".detail-grid .field")).size();
    }

    public boolean hasAgentNodeOutputsSection() {
        List<WebElement> headings = driver().findElements(By.tagName("h3"));
        return headings.stream().anyMatch(h -> h.getText().equals("Agent Node Outputs"));
    }

    public int getNodeOutCount() {
        return driver().findElements(By.className("node-out")).size();
    }

    public boolean hasWorkflowPathSection() {
        List<WebElement> headings = driver().findElements(By.tagName("h3"));
        return headings.stream().anyMatch(h -> h.getText().equals("Workflow Path"));
    }

    public boolean hasGraphNodes() {
        return !driver().findElements(By.className("graph-node")).isEmpty();
    }

    public boolean hasAuditTrailSection() {
        List<WebElement> headings = driver().findElements(By.tagName("h3"));
        return headings.stream().anyMatch(h -> h.getText().equals("Audit Trail"));
    }

    public boolean hasTimelineItems() {
        return !driver().findElements(By.cssSelector(".timeline li")).isEmpty();
    }

    public void closeByXButton() {
        WebElement closeBtn = waitForClickable(By.cssSelector(".icon-btn[aria-label='Close']"));
        closeBtn.click();
        sleep(500);
    }

    public void closeByOverlayClick() {
        WebElement overlay = driver().findElement(By.className("drawer-overlay"));
        // Click on the overlay edges (not the drawer panel)
        org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver());
        actions.moveToElement(overlay, -400, 0).click().perform();
        sleep(500);
    }
}
