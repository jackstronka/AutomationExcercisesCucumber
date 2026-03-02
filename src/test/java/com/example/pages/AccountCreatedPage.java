package com.example.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page Object for /account_created – account creation confirmation.
 * Contains: Account Created!, congratulations message and Continue button.
 */
public class AccountCreatedPage extends BasePage {

    private final By accountCreatedHeading =
            By.xpath("//*[contains(.,'Account Created') or contains(.,'ACCOUNT CREATED')]");
    private final By continueButton =
            By.xpath("//*[contains(.,'Account Created') or contains(.,'ACCOUNT CREATED')]//a[contains(.,'Continue') or contains(.,'CONTINUE')]");

    public AccountCreatedPage(WebDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return isElementPresent(accountCreatedHeading);
    }

    public boolean isAccountCreatedMessageVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(accountCreatedHeading));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Click Continue after account created")
    public void clickContinue() {
        clickViaJavaScript(continueButton);
    }
}
