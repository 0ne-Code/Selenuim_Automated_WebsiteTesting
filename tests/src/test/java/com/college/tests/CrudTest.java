package com.college.tests;

import com.college.base.BaseTest;
import com.college.pages.RecordFormPage;
import com.college.pages.RecordsPage;

// import org.openqa.selenium.By;
// import org.openqa.selenium.support.ui.ExpectedConditions;
// import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

// import java.net.URLEncoder;
// import java.nio.charset.StandardCharsets;
// import java.time.Duration;
import java.util.UUID;

public class CrudTest extends BaseTest {

    private void searchForRecord(RecordsPage recordsPage, String name) {
        recordsPage.search(name);
    }

    @Test
    public void adminCanAddRecord() {
        loginAs("admin");

        RecordsPage recordsPage = new RecordsPage(driver);
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String name = "Test Student " + uniqueId;
        String email = "student" + uniqueId + "example.com";                                    //can test

        recordsPage.clickAddButton();

        RecordFormPage formPage = new RecordFormPage(driver);
        formPage.fillForm(name, "Computer Science", email);
        formPage.save();

        searchForRecord(recordsPage, name);

        Assert.assertTrue(  
                recordsPage.isRecordPresent(name),
                "The newly added student should appear in the records table."
        );
    }

    @Test
    public void adminCanEditRecord() {
        loginAs("admin");

        RecordsPage recordsPage = new RecordsPage(driver);
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String originalName = "Edit Student " + uniqueId;
        String updatedName = "Updated Student " + uniqueId;
        String email = "edit" + uniqueId + "@example.com";                                       //can test

        recordsPage.clickAddButton();

        RecordFormPage formPage = new RecordFormPage(driver);
        formPage.fillForm(originalName, "Computer Science", email);
        formPage.save();

        searchForRecord(recordsPage, originalName);
        Assert.assertTrue(
                recordsPage.isRecordPresent(originalName),
                "The student should exist before editing."
        );

        recordsPage.clickEditFor(originalName);

        formPage.fillForm(updatedName, "Computer Science", email);
        formPage.save();

        searchForRecord(recordsPage, updatedName);

        Assert.assertTrue(
                recordsPage.isRecordPresent(updatedName),
                "The student's updated name should appear in the table."
        );
    }

    @Test
    public void adminCanDeleteRecord() {
        loginAs("admin");

        RecordsPage recordsPage = new RecordsPage(driver);
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String name = "Delete Student " + uniqueId;
        String email = "delete" + uniqueId + "@example.com";

        recordsPage.clickAddButton();

        RecordFormPage formPage = new RecordFormPage(driver);
        formPage.fillForm(name, "Computer Science", email);
        formPage.save();

        searchForRecord(recordsPage, name);
        Assert.assertTrue(
                recordsPage.isRecordPresent(name),
                "The student should exist before deletion."
        );

        recordsPage.clickDeleteFor(name);
        recordsPage.acceptDeleteConfirmation();

        Assert.assertFalse(
                recordsPage.isRecordPresent(name),
                "The deleted student should no longer appear in the table."
        );
    }

    @Test
    public void searchFindsExistingStudent() {
        loginAs("admin");

        RecordsPage recordsPage = new RecordsPage(driver);
        recordsPage.search("Student 01");                                                   //can test

        Assert.assertTrue(
                recordsPage.isRecordPresent("Student 01"),
                "Searching for an existing student should find the record."
        );
    }

    @Test
    public void searchWithNoMatchShowsNoResults() {
        loginAs("admin");

        RecordsPage recordsPage = new RecordsPage(driver);
        recordsPage.search("NoSuchStudent_XYZ_987654");

        Assert.assertTrue(
                recordsPage.isNoResultsVisible(),
                "A search with no matches should show the no-results message."
        );
    }
}