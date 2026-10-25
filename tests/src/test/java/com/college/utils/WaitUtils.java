package com.college.utils;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;

public class WaitUtils {

    private final WebDriverWait wait;

    public WaitUtils(WebDriver driver, int timeoutSeconds) {
        this.wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(timeoutSeconds)
        );
    }

    public WebElement waitForVisible(By locator) {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );
    }

    public WebElement waitForClickable(By locator) {
        return wait.until(
                ExpectedConditions.elementToBeClickable(locator)
        );
    }

    public boolean waitForUrlContains(String text) {
        return wait.until(
                ExpectedConditions.urlContains(text)
        );
    }

    public File waitForFileExists(String filePath) {
        return wait.until(driver -> {
            File file = new File(filePath);

            if (file.exists() && file.isFile()) {
                return file;
            }

            return null;
        });
    }

    public Alert waitForAlert() {
        return wait.until(
                ExpectedConditions.alertIsPresent()
        );
    }
    
    public String waitForPageInfoToChange(String previousPageInfo) {
        return wait.until(driver -> {
            String current = driver.findElement(By.id("page-info")).getText();

            if (!current.equals(previousPageInfo)) {
                return current;
            }

            return null;
        });
    }

    public String waitForPageInfoToEqual(String expectedPageInfo) {
        return wait.until(driver -> {
            String current = driver.findElement(By.id("page-info")).getText();

            if (current.equals(expectedPageInfo)) {
                return current;
            }

            return null;
        });
    }
}