package com.college.tests;

import com.college.base.BaseTest;
import com.college.config.ConfigReader;
import com.college.pages.LoginPage;
import com.college.pages.RecordsPage;

import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test
    public void adminCanLogin() {
        loginAs("admin");

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/records"),
                "Admin should be redirected to the records page."
        );

        Assert.assertTrue(
                new RecordsPage(driver).isAdminLinkVisible(),
                "Admin should see the admin panel link."
        );
    }

    @Test
    public void userCanLogin() {
        loginAs("user");

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/records"),
                "User should be redirected to the records page."
        );

        Assert.assertFalse(
                new RecordsPage(driver).isAdminLinkVisible(),
                "Regular user should not see the admin panel link."
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
                "An error should appear after an invalid login."
        );

        Assert.assertEquals(
                loginPage.getErrorMessage(),
                "Invalid username or password",
                "The login error message should match."
        );
    }

    @Test
    public void emptyCredentialsAreBlockedByBrowser() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("", "");

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/login"),
                "The browser should remain on the login page."
        );

        Assert.assertTrue(
                loginPage.isUsernameRequired(),
                "Username should be a required field."
        );

        Assert.assertTrue(
                loginPage.isPasswordRequired(),
                "Password should be a required field."
        );
    }

    @Test
    public void logoutReturnsToLoginPage() {
        loginAs("admin");

        LoginPage loginPage = new LoginPage(driver);
        loginPage.clickLogout();

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/login"),
                "Logout should return the user to the login page."
        );
    }
}