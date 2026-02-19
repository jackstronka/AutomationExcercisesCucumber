package com.example.utilities;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.Dimension;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


public class WebDriverFactory {

    // Main entry point – template
    public static WebDriver create() {
        String browser = ConfigReader.get("browser");
        boolean headless = Boolean.parseBoolean(ConfigReader.get("headless", "false"));
        boolean maximize = Boolean.parseBoolean(ConfigReader.get("maximizeWindow", "false"));
        int width = Integer.parseInt(ConfigReader.get("windowWidth", "1200"));
        int height = Integer.parseInt(ConfigReader.get("windowHeight", "800"));

        return create(browser, headless, maximize, width, height);
    }

    // Technical implementation
    private static WebDriver create(String browser, boolean headless, boolean maximize, int width, int height) {
        WebDriver driver;

        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                String downloadPath = new File(System.getProperty("user.dir"), "target/downloads").getAbsolutePath();
                new File(downloadPath).mkdirs();
                // Disable password save / autofill so it does not block buttons (e.g. Place Order)
                Map<String, Object> prefs = new HashMap<>();
                prefs.put("credentials_enable_service", false);
                prefs.put("profile.password_manager_enabled", false);
                prefs.put("autofill.profile_enabled", false);
                prefs.put("autofill.credit_card_enabled", false);
                prefs.put("download.default_directory", downloadPath);
                chromeOptions.setExperimentalOption("prefs", prefs);
                String loadStrategy = ConfigReader.get("pageLoadStrategy", "eager");
                if ("eager".equalsIgnoreCase(loadStrategy)) {
                    chromeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);
                } else if ("none".equalsIgnoreCase(loadStrategy)) {
                    chromeOptions.setPageLoadStrategy(PageLoadStrategy.NONE);
                }
                if (headless) {
                    chromeOptions.addArguments("--headless=new");
                    chromeOptions.addArguments("--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
                }
                driver = new ChromeDriver(chromeOptions);
                break;

            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                String ffDownloadPath = new File(System.getProperty("user.dir"), "target/downloads").getAbsolutePath();
                new File(ffDownloadPath).mkdirs();
                firefoxOptions.addPreference("browser.download.folderList", 2);
                firefoxOptions.addPreference("browser.download.dir", ffDownloadPath);
                firefoxOptions.addPreference("browser.helperApps.neverAsk.saveToDisk", "application/pdf,text/plain,application/octet-stream");
                if (headless) {
                    firefoxOptions.addArguments("--headless");
                }
                driver = new FirefoxDriver(firefoxOptions);
                break;

            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(Long.parseLong(ConfigReader.get("implicitWait", "0")))
        );
        driver.manage().timeouts().pageLoadTimeout(
                Duration.ofSeconds(Long.parseLong(ConfigReader.get("pageLoadTimeout", "30")))
        );

        if (!maximize) {
            driver.manage().window().setSize(new Dimension(width, height));
        }
        return driver;
    }
}