package com.college.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class RecordFormPage {

    private final WebDriver driver;

    private final By nameField = By.id("name");
    private final By courseField = By.id("course");
    private final By emailField = By.id("email");
    private final By saveButton = By.id("save-btn");

    public RecordFormPage(WebDriver driver) {
        this.driver = driver;
    }

    public void fillForm(String name, String course, String email) {
        driver.findElement(nameField).sendKeys(name);
        driver.findElement(courseField).sendKeys(course);
        driver.findElement(emailField).sendKeys(email);
    }

    public void save() {
        driver.findElement(saveButton).click();
    }
}