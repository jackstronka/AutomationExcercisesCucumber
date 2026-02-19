package com.example.hooks;

import com.example.context.ScenarioContext;
import com.example.utilities.ConfigReader;
import com.example.utilities.WebDriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class Hooks {

    private static final Logger log = LoggerFactory.getLogger(Hooks.class);

    /** One WebDriver per thread for parallel scenario execution. */
    private static final ThreadLocal<WebDriver> driverHolder = new ThreadLocal<>();

    public static WebDriver getDriver() {
        return driverHolder.get();
    }

    @Before
    public void setUp() {
        ScenarioContext.clear();
        WebDriver driver = WebDriverFactory.create();
        driverHolder.set(driver);

        boolean maximize = Boolean.parseBoolean(
                ConfigReader.get("maximizeWindow", "false")
        );
        if (maximize) {
            driver.manage().window().maximize();
        }

        driver.get(ConfigReader.get("baseUrl"));
        dismissCookieOverlay();
        dismissAdOverlays();
        removeGoogleVignetteFromUrl();
    }

    @BeforeStep
    public void beforeStep() {
        dismissCookieOverlay();
        dismissAdOverlays();
        removeGoogleVignetteFromUrl();
    }

    /**
     * Removes #google_vignette from URL (added automatically by Google ads).
     */
    private void removeGoogleVignetteFromUrl() {
        try {
            WebDriver driver = getDriver();
            if (driver == null) return;
            String url = driver.getCurrentUrl();
            if (url != null && url.contains("#google_vignette")) {
                ((JavascriptExecutor) driver).executeScript(
                        "window.history.replaceState(null, '', window.location.pathname + window.location.search);"
                );
            }
        } catch (Exception e) {
            log.debug("removeGoogleVignetteFromUrl: {}", e.getMessage());
        }
    }

    /**
     * Hides/removes ads – including Google Vignette ads, Google Ads iframes.
     */
    private void dismissAdOverlays() {
        try {
            WebDriver driver = getDriver();
            if (driver == null) return;
            String removeScript = ""
                    + "document.querySelectorAll('ins.adsbygoogle, .adsbygoogle-noablate, "
                    + "iframe[id*=\"aswift\"], iframe[id*=\"google_ads_iframe\"], iframe[id*=\"ad_iframe\"]').forEach(function(e){ e.remove(); });";
            ((JavascriptExecutor) driver).executeScript(removeScript);
            String hideScript = ""
                    + "document.querySelectorAll('iframe[title*=\"Advertisement\"], iframe[title*=\"Reklama\"], [id*=\"google_ads\"]').forEach(function(e){ "
                    + "e.style.setProperty('display','none','important'); e.style.setProperty('visibility','hidden','important'); });";
            ((JavascriptExecutor) driver).executeScript(hideScript);
        } catch (Exception e) {
            log.debug("dismissAdOverlays: {}", e.getMessage());
        }
    }

    /**
     * Dismisses cookie consent overlay (fc-dialog-overlay) that may block elements.
     */
    private void dismissCookieOverlay() {
        try {
            WebDriver driver = getDriver();
            if (driver == null) return;
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            if (driver.findElements(By.cssSelector(".fc-dialog-overlay, .fc-consent")).isEmpty()) {
                return;
            }
            By[] acceptSelectors = {
                    By.cssSelector(".fc-cta-consent"),
                    By.cssSelector(".fc-consent .fc-primary-button"),
                    By.xpath("//button[contains(translate(., 'ACCEPT', 'accept'), 'accept')]"),
                    By.xpath("//a[contains(translate(., 'ACCEPT', 'accept'), 'accept')]")
            };
            for (By sel : acceptSelectors) {
                if (!driver.findElements(sel).isEmpty()) {
                    wait.until(ExpectedConditions.elementToBeClickable(sel)).click();
                    return;
                }
            }
            ((JavascriptExecutor) driver).executeScript(
                    "var el = document.querySelector('.fc-dialog-overlay'); if(el) el.style.display='none';"
            );
        } catch (Exception e) {
            log.debug("dismissCookieOverlay: {} (overlay may not be visible)", e.getMessage());
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        WebDriver driver = getDriver();
        if (scenario.isFailed() && driver != null) {
            try {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment("Screenshot on failure",
                        new java.io.ByteArrayInputStream(screenshot));
            } catch (Exception e) {
                log.warn("Failed to attach screenshot to Allure: {}", e.getMessage());
            }
        }
        if (driver != null) {
            driver.quit();
            driverHolder.remove();
        }
    }
}