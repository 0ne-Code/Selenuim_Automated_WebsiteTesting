package com.college.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class RecordsPage {

    private final WebDriver driver;

    private final By adminLink = By.id("nav-admin");
    private final By addButton = By.id("add-btn");
    private final By accessDeniedMessage = By.cssSelector("main.error-page h1");
    private final By recordsTable = By.id("records-table");
    
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
}