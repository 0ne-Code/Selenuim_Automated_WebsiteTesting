
package com.college.tests;

import com.college.base.BaseTest;
import com.college.config.ConfigReader;
import com.college.pages.LoginPage;
import com.college.pages.RecordsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AccessControlTest extends BaseTest {

    private RecordsPage recordsPage;

    private void loginAsAdmin() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(
            ConfigReader.get("admin.username"),
            ConfigReader.get("admin.password")
        );

        loginPage.waitForRecordsPage();
        recordsPage = new RecordsPage(driver);
    }

    private void loginAsUser() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(
            ConfigReader.get("user.username"),
            ConfigReader.get("user.password")
        );

        loginPage.waitForRecordsPage();
        recordsPage = new RecordsPage(driver);
    }

    @Test
    public void adminSeesAdminPanelLink() {
        loginAsAdmin();

        Assert.assertTrue(
            recordsPage.isAdminLinkVisible(),
            "Admin should see the Admin Panel link."
        );
    }

    @Test
    public void adminSeesAddButton() {
        loginAsAdmin();

        Assert.assertTrue(
            recordsPage.isAddButtonVisible(),
            "Admin should see the Add Record button."
        );
    }

    @Test
    public void userDoesNotSeeAdminPanelLink() {
        loginAsUser();

        Assert.assertFalse(
            recordsPage.isAdminLinkVisible(),
            "Normal user should not see the Admin Panel link."
        );
    }

    @Test
    public void userDoesNotSeeAddButton() {
        loginAsUser();

        Assert.assertFalse(
            recordsPage.isAddButtonVisible(),
            "Normal user should not see the Add Record button."
        );
    }

    @Test
    public void userGets403OnAdminUrl() {
        loginAsUser();

        recordsPage.openUrl(
            ConfigReader.get("base.url") + "/admin"
        );

        //
        //TEMPORARY
        //
        // System.out.println("URL after opening admin: " + driver.getCurrentUrl());
        // System.out.println("Title: " + driver.getTitle());
        // System.out.println("Page source: " + driver.getPageSource());

        Assert.assertTrue(
            recordsPage.isAccessDenied(),
            "Normal user should receive a 403 Access Denied page."
        );
    }

    @Test
    public void userGets403OnAddRecordUrl() {
        loginAsUser();

        recordsPage.openUrl(
            ConfigReader.get("base.url") + "/records/add"
        );

        //
        //TEMPORARY
        //
        // System.out.println("URL after opening add record: " + driver.getCurrentUrl());

        Assert.assertTrue(
            recordsPage.isAccessDenied(),
            "Normal user should receive a 403 Access Denied page."
        );
    }

    @Test
    public void unauthenticatedVisitIsRedirectedToLogin() {
        // BaseTest opens the home page before each test.
        // Navigate directly to a protected page without logging in.
        recordsPage = new RecordsPage(driver);
        recordsPage.openUrl(
            ConfigReader.get("base.url") + "/records"
        );

        Assert.assertTrue(
            driver.getCurrentUrl().contains("/login"),
            "An unauthenticated visitor should be redirected to login."
        );
    }
}