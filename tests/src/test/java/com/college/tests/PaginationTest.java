package com.college.tests;

import com.college.base.BaseTest;
import com.college.config.ConfigReader;
import com.college.pages.LoginPage;
import com.college.pages.RecordsPage;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class PaginationTest extends BaseTest {

    @Test
    public void recordsTableIsVisible() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(
                ConfigReader.get("admin.username"),
                ConfigReader.get("admin.password")
        );

        loginPage.waitForRecordsPage();

        RecordsPage recordsPage = new RecordsPage(driver);

        Assert.assertTrue(
                recordsPage.isRecordsTableVisible(),
                "The records table should be visible."
        );

        Assert.assertTrue(
                recordsPage.isRecordPresent("Student 01"),
                "The table should contain student records."
        );
    }

    @Test
    public void nextAndPreviousPageWork() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(
                ConfigReader.get("admin.username"),
                ConfigReader.get("admin.password")
        );

        loginPage.waitForRecordsPage();

        RecordsPage recordsPage = new RecordsPage(driver);

        WebDriverWait wait = new WebDriverWait(
                driver, Duration.ofSeconds(10)
        );

        String firstPage = recordsPage.getPageInfo();

        Assert.assertTrue(
                firstPage.contains("Page 1 of"),
                "The first page should be displayed initially."
        );

        Assert.assertTrue(
                recordsPage.isPreviousPageDisabled(),
                "Previous should be disabled on the first page."
        );

        recordsPage.clickNextPage();

        wait.until(d ->
                !recordsPage.getPageInfo().equals(firstPage)
        );

        String secondPage = recordsPage.getPageInfo();

        Assert.assertNotEquals(
                secondPage,
                firstPage,
                "Clicking Next should change the page."
        );

        recordsPage.clickPreviousPage();

        wait.until(d ->
                recordsPage.getPageInfo().equals(firstPage)
        );

        Assert.assertEquals(
                recordsPage.getPageInfo(),
                firstPage,
                "Clicking Previous should return to the first page."
        );
    }
      
    @Test
    public void nextButtonIsDisabledOnLastPage() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(
                ConfigReader.get("admin.username"),
                ConfigReader.get("admin.password")
        );

        loginPage.waitForRecordsPage();

        RecordsPage recordsPage = new RecordsPage(driver);

        WebDriverWait wait = new WebDriverWait(
                driver, Duration.ofSeconds(10)
        );

        while (!recordsPage.isNextPageDisabled()) {
            String currentPage = recordsPage.getPageInfo();

            recordsPage.clickNextPage();

            wait.until(d ->
                    !recordsPage.getPageInfo().equals(currentPage)
            );
        }

        Assert.assertTrue(
                recordsPage.isNextPageDisabled(),
                "Next should be disabled on the last page."
        );
    }
}