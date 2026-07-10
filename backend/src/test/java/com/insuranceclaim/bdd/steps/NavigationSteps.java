package com.insuranceclaim.bdd.steps;

import com.insuranceclaim.bdd.hooks.ScenarioContext;
import com.insuranceclaim.bdd.pages.BasePage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NavigationSteps {

    private final BasePage basePage = new BasePage() {};

    @Given("the application is open")
    public void theApplicationIsOpen() {
        basePage.openApp();
    }

    @When("I navigate to {string}")
    public void iNavigateTo(String tabLabel) {
        basePage.navigateTo(tabLabel);
    }

    @Then("the page heading should be {string}")
    public void thePageHeadingShouldBe(String expected) {
        String actual = basePage.getPageHeading();
        assertEquals(expected, actual,
                "Expected page heading '" + expected + "' but got '" + actual + "'");
    }

    @Then("the brand mark should display {string}")
    public void theBrandMarkShouldDisplay(String expected) {
        WebElement brandMark = basePage.waitForVisible(By.className("brand-mark"));
        assertEquals(expected, brandMark.getText());
    }

    @Then("the brand name should display {string}")
    public void theBrandNameShouldDisplay(String expected) {
        WebElement brandName = basePage.waitForVisible(By.className("brand-name"));
        assertEquals(expected, brandName.getText());
    }

    @Then("there should be exactly {int} navigation items")
    public void thereShouldBeExactlyNNavigationItems(int count) {
        List<WebElement> navItems = ScenarioContext.getDriver().findElements(By.className("nav-item"));
        assertEquals(count, navItems.size());
    }

    @Then("the navigation should contain {string}")
    public void theNavigationShouldContain(String label) {
        List<WebElement> navItems = ScenarioContext.getDriver().findElements(By.className("nav-item"));
        boolean found = navItems.stream().anyMatch(item -> item.getText().contains(label));
        assertTrue(found, "Nav item with label '" + label + "' was not found");
    }

    @Then("the {string} nav item should have the active class")
    public void theNavItemShouldHaveActiveClass(String label) {
        List<WebElement> navItems = ScenarioContext.getDriver().findElements(By.className("nav-item"));
        WebElement target = navItems.stream()
                .filter(item -> item.getText().contains(label))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nav item '" + label + "' not found"));
        String cssClasses = target.getAttribute("class");
        assertTrue(cssClasses.contains("nav-item-active"),
                "Expected '" + label + "' to have class 'nav-item-active', but was: " + cssClasses);
    }
}
