package com.college.tests;

import com.college.base.BaseTest;
import com.college.pages.LoginPage;
import com.college.pages.RecordsPage;
import com.college.pages.RecordFormPage;
import com.college.config.ConfigReader;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class CrudTest extends BaseTest {

    @Test
    public void adminCanAddRecord() {

        // Log in as admin
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(
                ConfigReader.get("admin.username"),
                ConfigReader.get("admin.password")
        );
        loginPage.waitForRecordsPage();

        RecordsPage recordsPage = new RecordsPage(driver);

        // Generate unique student details for this test run
        String name = "Test Student " + System.currentTimeMillis();
        String course = "Computer Science";
        String email = "student" + System.currentTimeMillis() + "@example.com";

        // Open the add-record form
        recordsPage.clickAddButton();

        // Fill in and submit the form
        RecordFormPage formPage = new RecordFormPage(driver);
        formPage.fillForm(name, course, email);
        formPage.save();

        // Verify that the student was added
        // Search for the new student so pagination doesn't hide it
        String searchUrl = ConfigReader.get("base.url")
                + "/records?search="
                + URLEncoder.encode(name, StandardCharsets.UTF_8);

        driver.get(searchUrl);

        // Wait until the new student appears in the table
        WebDriverWait wait = new WebDriverWait(
                driver, Duration.ofSeconds(10)
        );

        wait.until(d -> d.findElement(By.id("records-table"))
                .getText()
                .contains(name));

        Assert.assertTrue(
                driver.findElement(By.id("records-table"))
                        .getText()
                        .contains(name),
                "The newly added student should appear in the records table."
        );
    }

    @Test
    public void adminCanEditRecord() {

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(
                ConfigReader.get("admin.username"),
                ConfigReader.get("admin.password")
        );
        loginPage.waitForRecordsPage();

        RecordsPage recordsPage = new RecordsPage(driver);

        String originalName = "Edit Student " + System.currentTimeMillis();
        String updatedName = originalName + " Updated";
        String email = "edit" + System.currentTimeMillis() + "@example.com";

        // Create a student first
        recordsPage.clickAddButton();

        RecordFormPage formPage = new RecordFormPage(driver);
        formPage.fillForm(originalName, "Computer Science", email);
        formPage.save();

        // Find the newly created student
        String searchUrl = ConfigReader.get("base.url")
                + "/records?search="
                + java.net.URLEncoder.encode(
                        originalName,
                        java.nio.charset.StandardCharsets.UTF_8
                );

        driver.get(searchUrl);

        new org.openqa.selenium.support.ui.WebDriverWait(
                driver, java.time.Duration.ofSeconds(10)
        ).until(d -> recordsPage.isRecordPresent(originalName));

        // Open the edit form
        recordsPage.clickEditFor(originalName);

        // Update the student's details
        driver.findElement(By.id("name")).clear();
        driver.findElement(By.id("name")).sendKeys(updatedName);

        driver.findElement(By.id("save-btn")).click();

        // Verify the updated name
        driver.get(
                ConfigReader.get("base.url")
                        + "/records?search="
                        + java.net.URLEncoder.encode(
                                updatedName,
                                java.nio.charset.StandardCharsets.UTF_8
                        )
        );

        new org.openqa.selenium.support.ui.WebDriverWait(
                driver, java.time.Duration.ofSeconds(10)
        ).until(d -> recordsPage.isRecordPresent(updatedName));

        Assert.assertTrue(
                recordsPage.isRecordPresent(updatedName),
                "The edited student should appear with the updated name."
        );
    }
  
    @Test
    public void adminCanDeleteRecord() {

        // Log in as admin
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(
                ConfigReader.get("admin.username"),
                ConfigReader.get("admin.password")
        );
        loginPage.waitForRecordsPage();

        RecordsPage recordsPage = new RecordsPage(driver);

        // Generate unique student details
        String name = "Delete Student " + System.currentTimeMillis();
        String email = "delete" + System.currentTimeMillis() + "@example.com";

        // Create a student to delete
        recordsPage.clickAddButton();

        RecordFormPage formPage = new RecordFormPage(driver);
        formPage.fillForm(name, "Computer Science", email);
        formPage.save();

        // Search for the new student
        String searchUrl = ConfigReader.get("base.url")
                + "/records?search="
                + java.net.URLEncoder.encode(
                        name,
                        java.nio.charset.StandardCharsets.UTF_8
                );

        driver.get(searchUrl);

        org.openqa.selenium.support.ui.WebDriverWait wait =
                new org.openqa.selenium.support.ui.WebDriverWait(
                        driver, java.time.Duration.ofSeconds(10)
                );

        wait.until(d -> recordsPage.isRecordPresent(name));

        // Delete the student
        recordsPage.clickDeleteFor(name);

        // Accept the JavaScript confirmation dialog
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions
                .alertIsPresent());

        driver.switchTo().alert().accept();

        // Verify the student no longer appears
        wait.until(d -> {
            String tableText = d.findElement(By.id("records-table")).getText();
            return !tableText.contains(name);
        });

        Assert.assertFalse(
                recordsPage.isRecordPresent(name),
                "The deleted student should no longer appear in the records table."
        );
    }

    
    @Test
    public void searchFindsExistingStudent() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(
                ConfigReader.get("admin.username"),
                ConfigReader.get("admin.password")
        );

        loginPage.waitForRecordsPage();

        RecordsPage recordsPage = new RecordsPage(driver);

        // Search for a student that already exists
        recordsPage.search("Student 01");

        WebDriverWait wait = new WebDriverWait(
                driver, Duration.ofSeconds(10)
        );

        wait.until(d ->
                d.findElement(By.id("records-table"))
                        .getText()
                        .contains("Student 01")
        );

        Assert.assertTrue(
                recordsPage.isRecordPresent("Student 01"),
                "The matching student should appear in the search results."
        );
    }

    @Test
    public void searchWithNoMatchShowsNoResults() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(
                ConfigReader.get("admin.username"),
                ConfigReader.get("admin.password")
        );

        loginPage.waitForRecordsPage();

        RecordsPage recordsPage = new RecordsPage(driver);

        // Search for a name that should not exist
        recordsPage.search("ZZZ_NoSuchStudent_987654");

        WebDriverWait wait = new WebDriverWait(
                driver, Duration.ofSeconds(10)
        );

        wait.until(d ->
                !d.findElements(By.id("no-results")).isEmpty()
                && d.findElement(By.id("no-results")).isDisplayed()
        );

        Assert.assertTrue(
                driver.findElement(By.id("no-results")).isDisplayed(),
                "A no-results message should appear for an unmatched search."
        );
    }
}