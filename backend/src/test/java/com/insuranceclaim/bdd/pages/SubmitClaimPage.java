package com.insuranceclaim.bdd.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class SubmitClaimPage extends BasePage {

    public void open() {
        openApp();
        navigateTo("Submit Claim");
    }

    public int getStepperItemCount() {
        return driver().findElements(By.className("stepper-item")).size();
    }

    public boolean isFirstStepActive() {
        WebElement firstCircle = waitForVisible(By.className("stepper-circle"));
        return firstCircle.getAttribute("class").contains("stepper-active");
    }

    public void waitForSampleButtons() {
        waitForVisible(By.xpath("//button[contains(text(),'Load approved')]"));
    }

    public boolean hasSampleButton(String partialText) {
        return isElementPresent(By.xpath("//button[contains(text(),'" + partialText + "')]"));
    }

    public boolean hasCustomerNameInput() {
        return isElementPresent(By.cssSelector("input[placeholder='Full name']"));
    }

    public boolean hasPolicyNumberInput() {
        return isElementPresent(By.cssSelector("input[placeholder='e.g. POL-1001']"));
    }

    public boolean hasClaimTypeSelect() {
        return isElementPresent(By.tagName("select"));
    }

    public boolean hasAmountInput() {
        return isElementPresent(By.cssSelector("input[type='number']"));
    }

    public boolean hasDateInput() {
        return isElementPresent(By.cssSelector("input[type='date']"));
    }

    public boolean hasDescriptionTextarea() {
        return isElementPresent(By.tagName("textarea"));
    }

    public boolean isNextButtonDisabled() {
        sleep(500);
        WebElement nextBtn = waitForVisible(By.xpath("//button[contains(text(),'Next')]"));
        return !nextBtn.isEnabled();
    }

    public List<WebElement> getPolicyItems() {
        return driver().findElements(By.className("policy-item"));
    }

    public void clickFirstPolicy() {
        sleep(1000);
        WebElement firstPolicy = waitForClickable(By.className("policy-item"));
        scrollIntoView(firstPolicy);
        firstPolicy.click();
        sleep(500);
    }

    public String getCustomerNameValue() {
        WebElement nameInput = waitForVisible(By.cssSelector("input[placeholder='Full name']"));
        return nameInput.getAttribute("value");
    }

    public void fillClaimForm(String customerName, String policyNumber,
                              String claimType, String amount,
                              String incidentDate, String description) {
        sleep(500);
        WebElement nameInput = waitForVisible(By.cssSelector("input[placeholder='Full name']"));
        clearAndType(nameInput, customerName);

        WebElement policyInput = driver().findElement(By.cssSelector("input[placeholder='e.g. POL-1001']"));
        clearAndType(policyInput, policyNumber);

        WebElement claimTypeEl = driver().findElement(By.tagName("select"));
        new Select(claimTypeEl).selectByVisibleText(claimType);

        WebElement amountInput = driver().findElement(By.cssSelector("input[type='number']"));
        clearAndType(amountInput, amount);

        WebElement dateInput = driver().findElement(By.cssSelector("input[type='date']"));
        clearAndType(dateInput, incidentDate);

        WebElement descInput = driver().findElement(By.tagName("textarea"));
        clearAndType(descInput, description);
    }

    public void clickNextButton() {
        WebElement nextBtn = waitForClickable(By.xpath("//button[contains(text(),'Next')]"));
        scrollIntoView(nextBtn);
        nextBtn.click();
        waitForVisible(By.className("doc-grid"));
    }

    public boolean isDocGridVisible() {
        return isElementPresent(By.className("doc-grid"));
    }

    public void submitWorkflow() {
        clickButtonContaining("Run Multi-Agent Workflow");
        waitForVisible(By.className("decision-hero"));
    }

    public String getDecisionTitle() {
        return waitForVisible(By.className("decision-hero-title")).getText();
    }

    public String getDecisionClaimId() {
        return waitForVisible(By.className("decision-hero-id")).getText();
    }

    public boolean hasNodeProgression() {
        return isElementPresent(By.className("node-progression"));
    }

    public boolean hasMiniAudit() {
        return isElementPresent(By.className("mini-audit-v2"));
    }

    public String getReviewSummaryText() {
        return waitForVisible(By.className("submit-summary")).getText();
    }

    public void loadSample(String sampleName) {
        sleep(1000);
        clickButtonContaining(sampleName);
        sleep(500);
    }

    public void loadSampleAndSubmit(String sampleName) {
        loadSample(sampleName);
        clickNextButton();
        submitWorkflow();
    }

    public boolean isCustomerNameInputVisible() {
        return isElementPresent(By.cssSelector("input[placeholder='Full name']"));
    }

    public void clickBackButton() {
        clickButton("Back");
        sleep(500);
    }

    public void clickSubmitAnotherClaim() {
        clickButtonContaining("Submit Another");
        sleep(500);
    }
}
