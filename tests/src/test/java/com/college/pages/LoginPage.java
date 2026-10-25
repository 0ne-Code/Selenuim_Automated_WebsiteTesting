
package com.college.pages;

import com.college.config.ConfigReader;
import com.college.utils.WaitUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPage {

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By loginButton = By.id("login-btn");
    private final By errorMessage = By.id("error-msg");
    private final By logoutButton = By.id("logout");

    public LoginPage(WebDriver driver) {
        this.driver = driver;

        int timeout = Integer.parseInt(
                ConfigReader.get("timeout.seconds")
        );

        this.waitUtils = new WaitUtils(driver, timeout);
    }

    public void login(String username, String password) {
        WebElement usernameInput =
                waitUtils.waitForVisible(usernameField);
        usernameInput.clear();
        usernameInput.sendKeys(username);

        WebElement passwordInput =
                waitUtils.waitForVisible(passwordField);
        passwordInput.clear();
        passwordInput.sendKeys(password);

        waitUtils.waitForClickable(loginButton).click();
    }

    public void waitForRecordsPage() {
        waitUtils.waitForUrlContains("/records");
    }

    public String getErrorMessage() {
        return waitUtils.waitForVisible(errorMessage).getText();
    }

    public boolean isErrorDisplayed() {
        try {
            waitUtils.waitForVisible(errorMessage);
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public boolean isUsernameRequired() {
        return waitUtils.waitForVisible(usernameField)
                .getDomAttribute("required") != null;
    }

    public boolean isPasswordRequired() {
        return waitUtils.waitForVisible(passwordField)
                .getDomAttribute("required") != null;
    }

    public void clickLogout() {
        waitUtils.waitForClickable(logoutButton).click();
        waitUtils.waitForUrlContains("/login");
    }
}