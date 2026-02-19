package com.example.steps;

import com.example.context.ScenarioContext;
import com.example.hooks.Hooks;
import com.example.pages.AccountCreatedPage;
import com.example.pages.LoginPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class AccountSteps {

    @Then("I should see that I am logged in")
    public void iShouldSeeThatIAmLoggedIn() {
        LoginPage loginPage = ScenarioContext.get(ScenarioContext.LOGIN_PAGE);
        Assert.assertTrue(
                loginPage.isLoggedInAsUser(),
                "User should be logged in (Logged in as username)"
        );
    }

    @When("I click Continue after account is created")
    public void iClickContinueAfterAccountIsCreated() {
        AccountCreatedPage accountCreatedPage = ScenarioContext.get(ScenarioContext.ACCOUNT_CREATED_PAGE);
        if (accountCreatedPage == null) {
            accountCreatedPage = new AccountCreatedPage(Hooks.getDriver());
            ScenarioContext.put(ScenarioContext.ACCOUNT_CREATED_PAGE, accountCreatedPage);
        }
        accountCreatedPage.clickContinue();
    }

    @When("I click Continue after account is deleted")
    public void iClickContinueAfterAccountIsDeleted() {
        LoginPage loginPage = ScenarioContext.get(ScenarioContext.LOGIN_PAGE);
        loginPage.clickContinueAfterDelete();
    }

    @When("I click the Delete Account button when logged in")
    public void iClickTheDeleteAccountButtonWhenLoggedIn() {
        LoginPage loginPage = ScenarioContext.get(ScenarioContext.LOGIN_PAGE);
        loginPage.clickDeleteAccount();
    }

    @When("I click Logout in header")
    public void iClickLogoutInHeader() {
        LoginPage loginPage = ScenarioContext.get(ScenarioContext.LOGIN_PAGE);
        loginPage.clickLogout();
        ScenarioContext.put(ScenarioContext.LOGIN_PAGE, new LoginPage(Hooks.getDriver()));
    }
}
