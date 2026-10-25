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
}