package com.college.tests;

import com.college.base.BaseTest;
import com.college.config.ConfigReader;
import com.college.pages.AdminPage;
import com.college.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.io.File;

public class AdminTest extends BaseTest {

    private void loginAsAdmin() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(
                ConfigReader.get("admin.username"),
                ConfigReader.get("admin.password")
        );

        loginPage.waitForRecordsPage();
    }

    private String getTestFilePath(String fileName) {
        return new File(
                "src/test/resources/testdata/" + fileName
        ).getAbsolutePath();
    }

    @Test
    public void adminCanUploadValidCsv() {
        loginAsAdmin();

        AdminPage adminPage = new AdminPage(driver);
        adminPage.openAdminPage();

        adminPage.uploadCsv(
                getTestFilePath("test_students.csv")
        );

        Assert.assertTrue(
                adminPage.isUploadMessageVisible(),
                "Upload result message should be visible"
        );

        Assert.assertTrue(
                adminPage.getUploadMessage().contains("Uploaded:")
                        && adminPage.getUploadMessage().contains("(2 rows)"),
                "The message should confirm that two rows were uploaded"
        );
    }

    @Test
    public void invalidCsvHeadersAreRejected() {
        loginAsAdmin();

        AdminPage adminPage = new AdminPage(driver);
        adminPage.openAdminPage();

        adminPage.uploadCsv(
                getTestFilePath("invalid_students.csv")
        );

        Assert.assertTrue(
                adminPage.isUploadMessageVisible(),
                "An error message should be visible"
        );

        Assert.assertEquals(
                adminPage.getUploadMessage(),
                "CSV must contain Name, Course, and Email columns"
        );
    }

    @Test
    public void adminCanAccessDownloadReport() {
        loginAsAdmin();

        AdminPage adminPage = new AdminPage(driver);
        adminPage.openAdminPage();

        Assert.assertTrue(
                adminPage.isDownloadReportVisible(),
                "The report download link should be visible"
        );

        Assert.assertTrue(
                adminPage.getDownloadReportUrl().endsWith("/admin/report"),
                "The report link should point to /admin/report"
        );
    }
 
    @Test
    public void reportEndpointReturnsCsvFile() throws Exception {
        loginAsAdmin();

        // Get the session cookie created by Selenium after login.
        String sessionCookie = driver.manage()
                .getCookieNamed("session")
                .getValue();

        // Request the report using the same login session.
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        ConfigReader.get("base.url") + "/admin/report"
                ))
                .header("Cookie", "session=" + sessionCookie)
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        Assert.assertEquals(
                response.statusCode(),
                200,
                "The report endpoint should return HTTP 200"
        );

        Assert.assertTrue(
                response.headers()
                        .firstValue("Content-Type")
                        .orElse("")
                        .contains("text/csv"),
                "The response should have a CSV content type"
        );

        Assert.assertTrue(
                response.headers()
                        .firstValue("Content-Disposition")
                        .orElse("")
                        .contains("filename=report.csv"),
                "The response should specify report.csv as the download filename"
        );

        Assert.assertTrue(
                response.body().startsWith("ID,Name,Course,Email"),
                "The CSV should contain the expected column headers"
        );
    }
}