package com.college.pages;

import com.college.config.ConfigReader;
import com.college.utils.WaitUtils;

// import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class RecordsPage {

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    private final By adminLink = By.id("nav-admin");
    private final By addButton = By.id("add-btn");
    private final By accessDeniedHeading =
            By.cssSelector("main.error-page h1");
    private final By recordsTable = By.id("records-table");
    private final By pageInfo = By.id("page-info");
    private final By previousPageButton = By.id("prev-page");
    private final By nextPageButton = By.id("next-page");
    private final By searchBox = By.id("search-box");
    private final By searchButton = By.id("search-btn");
    private final By noResultsMessage = By.id("no-results");

    public RecordsPage(WebDriver driver) {
        this.driver = driver;

        int timeout = Integer.parseInt(
                ConfigReader.get("timeout.seconds")
        );

        this.waitUtils = new WaitUtils(driver, timeout);
    }

    public boolean isAdminLinkVisible() {
        return !driver.findElements(adminLink).isEmpty()
                && driver.findElement(adminLink).isDisplayed();
    }

    public boolean isAddButtonVisible() {
        return !driver.findElements(addButton).isEmpty()
                && driver.findElement(addButton).isDisplayed();
    }

    public boolean isAccessDenied() {
        try {
            return waitUtils.waitForVisible(accessDeniedHeading)
                    .getText().trim().equals("403");
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public void openUrl(String url) {
        driver.get(url);
    }

    public void clickAddButton() {
        waitUtils.waitForClickable(addButton).click();
    }
  
    public boolean isRecordPresent(String name) {
        try {
            return waitUtils.waitForVisible(recordsTable)
                    .getText().contains(name);
        } catch (org.openqa.selenium.StaleElementReferenceException e) {
            return driver.findElement(recordsTable)
                    .getText().contains(name);
        }
    }

    public void clickEditFor(String name) {
        By editButton = By.xpath(
                "//tr[td[contains(normalize-space(), '" + name
                        + "')]]//a[contains(@class, 'edit-btn')]"
        );

        waitUtils.waitForClickable(editButton).click();
    }

    public void search(String term) {
        WebElement searchInput = waitUtils.waitForVisible(searchBox);
        searchInput.clear();
        searchInput.sendKeys(term);

        waitUtils.waitForClickable(searchButton).click();
    }

    public void clickDeleteFor(String name) {
        By deleteButton = By.xpath(
                "//tr[td[contains(normalize-space(), '" + name
                        + "')]]//button[contains(@class, 'delete-btn')]"
        );

        waitUtils.waitForClickable(deleteButton).click();
    }

    public boolean isRecordsTableVisible() {
        try {
            return waitUtils.waitForVisible(recordsTable).isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public String getRecordsTableText() {
        return waitUtils.waitForVisible(recordsTable).getText();
    }

    public String getPageInfo() {
        return waitUtils.waitForVisible(pageInfo).getText();
    }

    public void clickNextPage() {
        waitUtils.waitForClickable(nextPageButton).click();
    }

    public void clickPreviousPage() {
        waitUtils.waitForClickable(previousPageButton).click();
    }

    public boolean isPreviousPageDisabled() {
        WebElement button = waitUtils.waitForVisible(previousPageButton);
        return !button.isEnabled();
    }

    public boolean isNextPageDisabled() {
        WebElement button = waitUtils.waitForVisible(nextPageButton);
        return !button.isEnabled();
    }

    public boolean isNoResultsVisible() {
        try {
            return waitUtils.waitForVisible(noResultsMessage).isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }
 
    public void acceptDeleteConfirmation() {
        waitUtils.waitForAlert().accept();
    }

    public void waitForPageInfoToChange(String previousPageInfo) {
        waitUtils.waitForPageInfoToChange(previousPageInfo);
    }

    public void waitForPageInfoToEqual(String expectedPageInfo) {
        waitUtils.waitForPageInfoToEqual(expectedPageInfo);
    } 
}