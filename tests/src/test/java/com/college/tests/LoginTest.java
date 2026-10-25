package com.college.tests;

import com.college.base.BaseTest;
import com.college.config.ConfigReader;
import com.college.pages.LoginPage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;

public class LoginTest extends BaseTest {

    @Test
    public void adminCanLogin() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(
                ConfigReader.get("admin.username"),
                ConfigReader.get("admin.password")
        );

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/records"),
                "Admin should be redirected to the records page."
        );

        Assert.assertTrue(
                driver.getPageSource().contains("admin"),
                "The page should identify the logged-in admin."
        );
    }

    @Test
    public void userCanLogin() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(
                ConfigReader.get("user.username"),
                ConfigReader.get("user.password")
        );

        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(d -> d.getCurrentUrl().contains("/records"));

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/records"),
                "User should be redirected to the records page."
        );

        Assert.assertTrue(
                driver.getPageSource().contains("user"),
                "The page should identify the logged-in user."
        );
    }

    @Test
    public void invalidPasswordShowsError() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(
                ConfigReader.get("admin.username"),
                "wrongpassword"
        );

        Assert.assertTrue(
                loginPage.isErrorDisplayed(),
                "An error should appear for an incorrect password."
        );

        Assert.assertEquals(
                loginPage.getErrorMessage(),
                "Invalid username or password"
        );
    }

    
    @Test
    public void emptyCredentialsAreBlockedByBrowser() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("", "");

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/login"),
                "Empty credentials should not navigate away from login."
        );

        Assert.assertTrue(
                driver.findElement(
                        org.openqa.selenium.By.id("username")
                ).getDomAttribute("required") != null,
                "Username field should have required validation."
        );

        Assert.assertTrue(
                driver.findElement(
                        org.openqa.selenium.By.id("password")
                ).getDomAttribute("required") != null,
                "Password field should have required validation."
        );
    }


    
    @Test
    public void logoutReturnsToLoginPage() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(
            ConfigReader.get("admin.username"),
            ConfigReader.get("admin.password")
        );

        // Wait for the login redirect to finish.
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(d -> d.getCurrentUrl().contains("/records"));

        Assert.assertTrue(
            driver.getCurrentUrl().contains("/records"),
            "Login failed. Current URL: " + driver.getCurrentUrl()
        );

        // Find and click Logout.
        WebElement logoutButton = new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(d -> d.findElement(By.id("logout")));

        logoutButton.click();

        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(d -> d.getCurrentUrl().contains("/login"));

        Assert.assertTrue(
            driver.getCurrentUrl().contains("/login"),
            "Logout should return to the login page."
        );
    }

}