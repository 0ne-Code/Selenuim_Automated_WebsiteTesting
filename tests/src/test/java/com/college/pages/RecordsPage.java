package com.college.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class RecordsPage {

    private final WebDriver driver;

    private final By adminLink = By.id("nav-admin");
    private final By addButton = By.id("add-btn");
    private final By accessDeniedMessage = By.cssSelector("main.error-page h1");
    private final By recordsTable = By.id("records-table");
    private final By pageInfo = By.id("page-info");
    private final By previousPageButton = By.id("prev-page");
    private final By nextPageButton = By.id("next-page");    

    public RecordsPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isAdminLinkVisible() {
        return !driver.findElements(adminLink).isEmpty();
    }

    public boolean isAddButtonVisible() {
        return !driver.findElements(addButton).isEmpty();
    }

    public boolean isAccessDenied() {
        WebDriverWait wait = new WebDriverWait(
            driver, Duration.ofSeconds(5)
        );

        try {
            return wait.until(d ->
                !d.findElements(accessDeniedMessage).isEmpty()
                && d.findElement(accessDeniedMessage).isDisplayed()
                && d.findElement(accessDeniedMessage).getText().trim().equals("403")
            );
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public void openUrl(String url) {
        driver.get(url);
    }

    public void clickAddButton() {
        driver.findElement(addButton).click();
    }

    public boolean isRecordPresent(String name) {
        return driver.findElement(recordsTable)
                .getText()
                .contains(name);
    }
    
    public void clickEditFor(String name) {
        driver.findElement(
                By.xpath("//tr[td[contains(normalize-space(), '" + name
                        + "')]]//a[contains(@class, 'edit-btn')]")
        ).click();
    }

    public void search(String term) {
        driver.findElement(By.id("search-box")).clear();
        driver.findElement(By.id("search-box")).sendKeys(term);
        driver.findElement(By.id("search-btn")).click();
    }
    
    public void clickDeleteFor(String name) {
        driver.findElement(
                By.xpath("//tr[td[contains(normalize-space(), '" + name
                        + "')]]//button[contains(@class, 'delete-btn')]")
        ).click();
    }

    public boolean isRecordsTableVisible() {
        return driver.findElement(recordsTable).isDisplayed();
    }

    public String getPageInfo() {
        return driver.findElement(pageInfo).getText();
    }

    public void clickNextPage() {
        driver.findElement(nextPageButton).click();
    }

    public void clickPreviousPage() {
        driver.findElement(previousPageButton).click();
    }

    public boolean isPreviousPageDisabled() {
        WebElement button = driver.findElement(previousPageButton);

        return button.getTagName().equals("button")
                && !button.isEnabled();
    }

    public boolean isNextPageDisabled() {
        WebElement button = driver.findElement(nextPageButton);

        return button.getTagName().equals("button")
                && !button.isEnabled();
    }
}