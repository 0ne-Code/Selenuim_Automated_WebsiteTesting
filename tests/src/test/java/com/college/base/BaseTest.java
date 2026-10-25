
package com.college.base;

import com.college.config.ConfigReader;
import com.college.pages.LoginPage;
import com.college.utils.ScreenshotUtils;
import com.college.utils.WaitUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;

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
            int timeout = Integer.parseInt(
                    ConfigReader.get("timeout.seconds")
            );

            WaitUtils waitUtils = new WaitUtils(driver, timeout);
            waitUtils.waitForVisible(By.id("username"));

        } catch (Exception e) {
            System.out.println("=== SELENIUM DEBUG INFO ===");
            System.out.println("Configured URL: " + baseUrl);

            if (driver != null) {
                System.out.println(
                        "Actual URL: " + driver.getCurrentUrl()
                );
                System.out.println(
                        "Page title: " + driver.getTitle()
                );

                String source = driver.getPageSource();
                System.out.println(
                        "Page source: "
                                + source.substring(
                                        0, Math.min(1500, source.length())
                                )
                );
            }

            throw new RuntimeException(
                    "Login field was not found. Check the debug information above.",
                    e
            );
        }
    }

    protected void loginAs(String role) {
        String usernameKey = role + ".username";
        String passwordKey = role + ".password";

        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(
                ConfigReader.get(usernameKey),
                ConfigReader.get(passwordKey)
        );

        loginPage.waitForRecordsPage();
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (driver == null) {
            return;
        }

        try {
            if (!result.isSuccess()) {
                String fileName = result.getTestClass()
                        .getRealClass()
                        .getSimpleName()
                        + "_"
                        + result.getMethod().getMethodName()
                        + "_"
                        + System.currentTimeMillis()
                        + ".png";

                File screenshot = ScreenshotUtils.takeScreenshot(
                        driver,
                        "target/screenshots/" + fileName
                );

                System.out.println(
                        "Failure screenshot saved: "
                                + screenshot.getAbsolutePath()
                );
            }
        } catch (Exception e) {
            System.err.println(
                    "Could not capture failure screenshot: "
                            + e.getMessage()
            );
        } finally {
            driver.quit();
        }
    }
}