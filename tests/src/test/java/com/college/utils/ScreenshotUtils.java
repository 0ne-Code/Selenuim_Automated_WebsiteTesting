package com.college.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ScreenshotUtils {

    public static File takeScreenshot(WebDriver driver, String filePath) {
        File source = ((TakesScreenshot) driver)
                .getScreenshotAs(OutputType.FILE);

        Path destination = Path.of(filePath);

        try {
            Files.createDirectories(destination.getParent());
            Files.copy(source.toPath(), destination,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            return destination.toFile();

        } catch (IOException e) {
            throw new RuntimeException("Failed to save screenshot.", e);
        }
    }

    public static String takeScreenshotAsBase64(WebDriver driver) {
        return ((TakesScreenshot) driver)
                .getScreenshotAs(OutputType.BASE64);
    }
}