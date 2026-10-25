package com.college.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.college.config.ConfigReader;

public class AdminPage {

    private final WebDriver driver;
    private final By fileInput = By.id("file-input");
    private final By uploadButton = By.id("upload-btn");
    private final By uploadMessage = By.id("upload-msg");
    private final By downloadReportLink = By.id("download-report");

    public AdminPage(WebDriver driver) {
        this.driver = driver;
    }

    public void openAdminPage() {
        driver.get(ConfigReader.get("base.url") + "/admin");
    }
    
    public void uploadCsv(String filePath) {
        driver.findElement(fileInput).sendKeys(filePath);
        driver.findElement(uploadButton).click();
    }

    public boolean isUploadMessageVisible() {
        return !driver.findElements(uploadMessage).isEmpty()
                && driver.findElement(uploadMessage).isDisplayed();
    }

    public String getUploadMessage() {
        return driver.findElement(uploadMessage).getText();
    }

    public boolean isDownloadReportVisible() {
        return driver.findElement(downloadReportLink).isDisplayed();
    }

    public String getDownloadReportUrl() {
        return driver.findElement(downloadReportLink).getDomAttribute("href");
    }
}