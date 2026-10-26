package com.college.tests;

import com.college.base.BaseTest;
import com.college.pages.RecordsPage;

import org.testng.Assert;
import org.testng.annotations.Test;

public class PaginationTest extends BaseTest {

    @Test
    public void recordsTableIsVisible() {
        loginAs("admin");

        RecordsPage recordsPage = new RecordsPage(driver);

        Assert.assertTrue(
                recordsPage.isRecordsTableVisible(),
                "The records table should be visible."
        );

        Assert.assertTrue(
                recordsPage.isRecordPresent("Student 01"),                                      //can test
                "The first student should appear in the records table."
        );
    }

    @Test
    public void nextAndPreviousPageWork() {
        loginAs("admin");

        RecordsPage recordsPage = new RecordsPage(driver);
        String firstPage = recordsPage.getPageInfo();

        Assert.assertTrue(
                firstPage.contains("1 of"),                                                     //can test
                "The initial page should be page 1."
        );

        Assert.assertTrue(
                recordsPage.isPreviousPageDisabled(),
                "Previous should be disabled on the first page."
        );

        recordsPage.clickNextPage();
        recordsPage.waitForPageInfoToChange(firstPage);

        String secondPage = recordsPage.getPageInfo();

        Assert.assertNotEquals(
                secondPage,
                firstPage,
                "Clicking Next should change the page."
        );

        recordsPage.clickPreviousPage();
        recordsPage.waitForPageInfoToEqual(firstPage);

        Assert.assertEquals(
                recordsPage.getPageInfo(),
                firstPage,
                "Clicking Previous should return to the first page."
        );
    }

    @Test
    public void nextButtonIsDisabledOnLastPage() {
        loginAs("admin");

        RecordsPage recordsPage = new RecordsPage(driver);

        while (!recordsPage.isNextPageDisabled()) {
            String currentPage = recordsPage.getPageInfo();

            recordsPage.clickNextPage();
            recordsPage.waitForPageInfoToChange(currentPage);
        }

        Assert.assertTrue(
                recordsPage.isNextPageDisabled(),
                "Next should be disabled on the last page."
        );
    }
}