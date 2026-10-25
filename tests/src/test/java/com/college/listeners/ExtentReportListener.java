package com.college.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.college.base.BaseTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.OutputType;
import java.io.File;
import java.nio.file.Files; 

public class ExtentReportListener implements ITestListener {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> test =
            new ThreadLocal<>();

    private static synchronized ExtentReports getExtentReports() {
        if (extent == null) {
            ExtentSparkReporter sparkReporter =
                    new ExtentSparkReporter("target/ExtentReport.html");

            sparkReporter.config().setDocumentTitle(
                    "DevOps Selenium Test Report"
            );
            sparkReporter.config().setReportName(
                    "Student Management Application Tests"
            );

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            extent.setSystemInfo("Project", "DevOps Selenium Project");
            extent.setSystemInfo("Testing Framework", "Selenium + TestNG");
        }

        return extent;
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest extentTest = getExtentReports().createTest(
                result.getMethod().getMethodName()
        );
        test.set(extentTest);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().pass("Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.get().fail(result.getThrowable());

        try {
            BaseTest baseTest = (BaseTest) result.getInstance();

            // Get the WebDriver used by this test
            java.lang.reflect.Field field =
                    BaseTest.class.getDeclaredField("driver");

            field.setAccessible(true);
            WebDriver driver = (WebDriver) field.get(baseTest);

            if (driver != null) {
                byte[] screenshot =
                        ((TakesScreenshot) driver).getScreenshotAs(
                                OutputType.BYTES
                        );

                test.get().addScreenCaptureFromBase64String(
                        java.util.Base64.getEncoder()
                                .encodeToString(screenshot),
                        "Failure Screenshot"
                );
            }
        } catch (Exception e) {
            test.get().warning(
                    "Could not attach failure screenshot: "
                            + e.getMessage()
            );
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.get().skip("Test skipped");
    }

    @Override
    public void onFinish(
            org.testng.ITestContext context
    ) {
        getExtentReports().flush();
        test.remove();
    }
}