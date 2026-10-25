package com.college.pages;

import com.college.config.ConfigReader;
import com.college.utils.WaitUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class RecordFormPage {

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    private final By nameField = By.id("name");
    private final By courseField = By.id("course");
    private final By emailField = By.id("email");
    private final By saveButton = By.id("save-btn");
    private final By cancelButton = By.id("cancel-btn");

    public RecordFormPage(WebDriver driver) {
        this.driver = driver;

        int timeout = Integer.parseInt(
                ConfigReader.get("timeout.seconds")
        );

        this.waitUtils = new WaitUtils(driver, timeout);
    }

    public void fillForm(String name, String course, String email) {
        WebElement nameInput = waitUtils.waitForVisible(nameField);
        nameInput.clear();
        nameInput.sendKeys(name);

        WebElement courseInput = waitUtils.waitForVisible(courseField);
        courseInput.clear();
        courseInput.sendKeys(course);

        WebElement emailInput = waitUtils.waitForVisible(emailField);
        emailInput.clear();
        emailInput.sendKeys(email);
    }

    public void save() {
        waitUtils.waitForClickable(saveButton).click();
    }

    public void cancel() {
        waitUtils.waitForClickable(cancelButton).click();
    }
}