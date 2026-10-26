package com.college.tests;

import com.college.base.BaseTest;
import com.college.config.ConfigReader;
import com.college.pages.AdminPage;

import org.openqa.selenium.Cookie;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AdminTest extends BaseTest {

    private String getTestFilePath(String fileName) {
        return new File(
                "src/test/resources/testdata/" + fileName
        ).getAbsolutePath();
    }

    @Test
    public void adminCanUploadValidCsv() {
        loginAs("admin");

        AdminPage adminPage = new AdminPage(driver);
        adminPage.openAdminPage();

        adminPage.uploadCsv(
                getTestFilePath("test_students.csv")                                            //can test
        );

        Assert.assertTrue(
                adminPage.isUploadMessageVisible(),
                "The upload message should be visible."
        );

        String message = adminPage.getUploadMessage();

        Assert.assertTrue(
                message.contains("Uploaded:"),
                "The success message should confirm the upload."
        );

        Assert.assertTrue(
                message.contains("(2 rows)"),
                "The upload should report two rows."
        );
    }

    @Test
    public void invalidCsvHeadersAreRejected() {
        loginAs("admin");

        AdminPage adminPage = new AdminPage(driver);
        adminPage.openAdminPage();

        adminPage.uploadCsv(
                getTestFilePath("invalid_students.csv")                                         //can test
        );

        Assert.assertTrue(
                adminPage.isUploadMessageVisible(),
                "The validation message should be visible."
        );

        Assert.assertEquals(
                adminPage.getUploadMessage(),
                "CSV must contain Name, Course, and Email columns",
                "The app should reject CSV files with invalid headers."
        );
    }

    @Test
    public void adminCanAccessDownloadReport() {
        loginAs("admin");

        AdminPage adminPage = new AdminPage(driver);
        adminPage.openAdminPage();

        Assert.assertTrue(
                adminPage.isDownloadReportVisible(),
                "The admin should see the download report link."
        );

        Assert.assertTrue(
                adminPage.getDownloadReportUrl().endsWith("/admin/report"),                     //can test
                "The report link should point to the report endpoint."
        );
    }

    @Test
    public void reportEndpointReturnsCsvFile() throws Exception {
        loginAs("admin");

        Cookie sessionCookie = driver.manage()
                .getCookieNamed("session");

        Assert.assertNotNull(
                sessionCookie,
                "The logged-in session cookie should exist."
        );

        String cookieHeader = sessionCookie.getName()
                + "=" + sessionCookie.getValue();

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        ConfigReader.get("base.url") + "/admin/report"
                ))
                .header("Cookie", cookieHeader)
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        Assert.assertEquals(
                response.statusCode(),
                200,
                "The report endpoint should return HTTP 200."
        );

        Assert.assertTrue(
                response.headers()
                        .firstValue("Content-Type")
                        .orElse("")
                        .contains("text/csv"),                                                          //can test
                "The response should have a CSV content type."
        );

        Assert.assertTrue(
                response.headers()
                        .firstValue("Content-Disposition")
                        .orElse("")
                        .contains("report.csv"),
                "The response should specify report.csv as the filename."
        );

        Assert.assertTrue(
                response.body().startsWith("ID,Name,Course,Email"),
                "The CSV should start with the expected column headers."
        );
    }
}