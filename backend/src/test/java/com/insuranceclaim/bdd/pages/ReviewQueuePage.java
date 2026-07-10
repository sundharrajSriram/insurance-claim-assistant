package com.insuranceclaim.bdd.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ReviewQueuePage extends BasePage {

    public void open() {
        openApp();
        navigateTo("Manual Review");
        sleep(500);
    }

    public String getReviewerFieldValue() {
        WebElement reviewerInput = waitForVisible(By.cssSelector(".reviewer-field input"));
        return reviewerInput.getAttribute("value");
    }

    public void setReviewerName(String name) {
        WebElement reviewerInput = waitForVisible(By.cssSelector(".reviewer-field input"));
        clearAndType(reviewerInput, name);
        sleep(300);
    }

    public boolean isEmptyStateVisible() {
        List<WebElement> reviewCards = driver().findElements(By.className("review-card"));
        if (reviewCards.isEmpty()) {
            return isElementPresent(By.className("empty"));
        }
        return false;
    }

    public String getEmptyStateMessage() {
        return waitForVisible(By.className("empty")).getText();
    }

    public int getReviewCardCount() {
        return driver().findElements(By.className("review-card")).size();
    }

    public WebElement getFirstReviewCard() {
        return waitForVisible(By.className("review-card"));
    }

    public String getReviewCardCustomerName() {
        WebElement card = getFirstReviewCard();
        return card.findElement(By.className("review-top")).getText();
    }

    public String getReviewCardMeta() {
        WebElement card = getFirstReviewCard();
        return card.findElement(By.className("review-meta")).getText();
    }

    public int getScoreBarCount() {
        WebElement card = getFirstReviewCard();
        WebElement scores = card.findElement(By.className("review-scores"));
        return scores.findElements(By.className("scorebar")).size();
    }

    public List<String> getActionButtonTexts() {
        WebElement card = getFirstReviewCard();
        WebElement actions = card.findElement(By.className("review-actions"));
        return actions.findElements(By.tagName("button")).stream()
                .map(WebElement::getText).toList();
    }

    public void addComment(String comment) {
        WebElement card = getFirstReviewCard();
        WebElement commentArea = card.findElement(By.tagName("textarea"));
        scrollIntoView(commentArea);
        clearAndType(commentArea, comment);
        sleep(300);
    }

    public void clickApprove() {
        WebElement card = getFirstReviewCard();
        WebElement approveBtn = card.findElement(By.className("btn-approve"));
        scrollIntoView(approveBtn);
        approveBtn.click();
        sleep(3000);
    }

    public void clickReject() {
        WebElement card = getFirstReviewCard();
        WebElement rejectBtn = card.findElement(By.className("btn-reject"));
        scrollIntoView(rejectBtn);
        rejectBtn.click();
        sleep(3000);
    }
}
