package com.college.pages;

import com.college.config.ConfigReader;
import com.college.utils.WaitUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AdminPage {

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    private final By fileInput = By.id("file-input");
    private final By uploadButton = By.id("upload-btn");
    private final By uploadMessage = By.id("upload-msg");
    private final By downloadReportLink = By.id("download-report");

    public AdminPage(WebDriver driver) {
        this.driver = driver;

        int timeout = Integer.parseInt(
                ConfigReader.get("timeout.seconds")
        );

        this.waitUtils = new WaitUtils(driver, timeout);
    }

    public void openAdminPage() {
        driver.get(ConfigReader.get("base.url") + "/admin");
    }

    public void uploadCsv(String filePath) {
        WebElement input = waitUtils.waitForVisible(fileInput);
        input.sendKeys(filePath);

        waitUtils.waitForClickable(uploadButton).click();
    }

    public boolean isUploadMessageVisible() {
        try {
            return waitUtils.waitForVisible(uploadMessage).isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public String getUploadMessage() {
        return waitUtils.waitForVisible(uploadMessage).getText();
    }

    public boolean isDownloadReportVisible() {
        try {
            return waitUtils.waitForVisible(downloadReportLink)
                    .isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public String getDownloadReportUrl() {
        return waitUtils.waitForVisible(downloadReportLink)
                .getDomAttribute("href");
    }
}