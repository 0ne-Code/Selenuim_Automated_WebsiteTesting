package com.college.tests;

import com.college.base.BaseTest;
import com.college.config.ConfigReader;
import com.college.pages.RecordsPage;

import org.testng.Assert;
import org.testng.annotations.Test;

public class AccessControlTest extends BaseTest {

    @Test
    public void adminSeesAdminPanelLink() {
        loginAs("admin");

        RecordsPage recordsPage = new RecordsPage(driver);

        Assert.assertTrue(
                recordsPage.isAdminLinkVisible(),
                "Admin should see the admin panel link."
        );
    }

    @Test
    public void adminSeesAddButton() {
        loginAs("admin");

        RecordsPage recordsPage = new RecordsPage(driver);

        Assert.assertTrue(
                recordsPage.isAddButtonVisible(),
                "Admin should see the Add Record button."
        );
    }

    @Test
    public void userDoesNotSeeAdminPanelLink() {
        loginAs("user");

        RecordsPage recordsPage = new RecordsPage(driver);

        Assert.assertFalse(
                recordsPage.isAdminLinkVisible(),
                "Regular users should not see the admin panel link."
        );
    }

    @Test
    public void userDoesNotSeeAddButton() {
        loginAs("user");

        RecordsPage recordsPage = new RecordsPage(driver);

        Assert.assertFalse(
                recordsPage.isAddButtonVisible(),
                "Regular users should not see the Add Record button."
        );
    }

    @Test
    public void userGets403OnAdminUrl() {
        loginAs("user");

        RecordsPage recordsPage = new RecordsPage(driver);
        recordsPage.openUrl(ConfigReader.get("base.url") + "/admin");

        Assert.assertTrue(
                recordsPage.isAccessDenied(),
                "Regular users should receive a 403 response on the admin page."
        );
    }

    @Test
    public void userGets403OnAddRecordUrl() {
        loginAs("user");

        RecordsPage recordsPage = new RecordsPage(driver);
        recordsPage.openUrl(
                ConfigReader.get("base.url") + "/records/add"
        );

        Assert.assertTrue(
                recordsPage.isAccessDenied(),
                "Regular users should receive a 403 response on the add-record page."
        );
    }

    @Test
    public void unauthenticatedVisitIsRedirectedToLogin() {
        RecordsPage recordsPage = new RecordsPage(driver);

        recordsPage.openUrl(
                ConfigReader.get("base.url") + "/records"
        );

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/login"),
                "Unauthenticated visitors should be redirected to login."
        );
    }
}