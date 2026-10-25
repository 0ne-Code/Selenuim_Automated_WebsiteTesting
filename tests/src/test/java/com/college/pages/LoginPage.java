package com.college.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LoginPage {

    private final WebDriver driver;

    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By loginButton = By.id("login-btn");
    private final By errorMessage = By.id("error-msg");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }   
    
    public void login(String username, String password) {
        driver.findElement(usernameField).clear();
        driver.findElement(usernameField).sendKeys(username);

        driver.findElement(passwordField).clear();
        driver.findElement(passwordField).sendKeys(password);

        driver.findElement(loginButton).click();
    }

    public void waitForRecordsPage() {
        WebDriverWait wait = new WebDriverWait(
                driver, Duration.ofSeconds(10)
        );

        wait.until(d -> d.getCurrentUrl().contains("/records"));
    }

    public String getErrorMessage() {
        return driver.findElement(errorMessage).getText();
    }

    public boolean isErrorDisplayed() {
        WebDriverWait wait = new WebDriverWait(
                driver, Duration.ofSeconds(5)
        );

        try {
            return wait.until(d ->
                    !d.findElements(errorMessage).isEmpty()
                    && d.findElement(errorMessage).isDisplayed()
            );
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }
}