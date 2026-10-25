package com.college.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.testng.ITestListener;
import org.testng.ITestResult;

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