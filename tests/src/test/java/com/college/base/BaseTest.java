
package com.college.base;

import com.college.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import java.time.Duration;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();

        boolean headless = Boolean.parseBoolean(
                ConfigReader.get("headless")
        );

        if (headless) {
            options.addArguments(
                    "--headless=new",
                    "--window-size=1920,1080",
                    "--no-sandbox",
                    "--disable-dev-shm-usage"
            );
        }

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        String baseUrl = ConfigReader.get("base.url");
        driver.get(baseUrl);

        try {
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(d -> !d.findElements(
                            By.id("username")
                    ).isEmpty());
        } catch (Exception e) {
            System.out.println("=== SELENIUM DEBUG INFO ===");
            System.out.println("Configured URL: " + baseUrl);
            System.out.println("Actual URL: " + driver.getCurrentUrl());
            System.out.println("Page title: " + driver.getTitle());
            System.out.println("Page source: "
                    + driver.getPageSource().substring(
                            0,
                            Math.min(1500, driver.getPageSource().length())
                    ));
            throw new RuntimeException(
                    "Login field was not found. Check the debug information above.",
                    e
            );
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
