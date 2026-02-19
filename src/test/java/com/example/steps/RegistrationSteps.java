package com.example.steps;

import com.example.context.ScenarioContext;
import com.example.hooks.Hooks;
import com.example.pages.LoginPage;
import com.example.pages.SignupPage;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;

public class RegistrationSteps {

    @When("I click the Signup button on signup form")
    public void iClickTheSignupButtonOnSignupForm() {
        LoginPage loginPage = ScenarioContext.get(ScenarioContext.LOGIN_PAGE);
        loginPage.clickSignupButton();
        ScenarioContext.put(ScenarioContext.SIGNUP_PAGE, new SignupPage(Hooks.getDriver()));
    }

    @When("I click Create Account on signup form")
    public void iClickCreateAccountOnSignupForm() {
        SignupPage signupPage = ScenarioContext.get(ScenarioContext.SIGNUP_PAGE);
        signupPage.clickCreateAccount();
    }

    @When("I enter a new user name {string} and email {string}")
    public void iEnterANewUserNameAndEmail(String name, String email) {
        ScenarioContext.put(ScenarioContext.LAST_ENTERED_NAME, name);
        ScenarioContext.put(ScenarioContext.LAST_ENTERED_EMAIL, email);
        LoginPage loginPage = ScenarioContext.get(ScenarioContext.LOGIN_PAGE);
        loginPage.enterNewUserNameAndEmail(name, email);
    }

    @When("I fill in the account information")
    public void iFillInTheAccountInformation(DataTable dataTable) {
        var row = dataTable.asMaps(String.class, String.class).get(0);
        SignupPage signupPage = ScenarioContext.get(ScenarioContext.SIGNUP_PAGE);
        signupPage.fillAccountInformation(
                row.get("password"),
                row.get("title"),
                row.get("day"),
                row.get("month"),
                row.get("year")
        );
    }

    @And("I subscribe to the newsletter")
    public void iSubscribeToTheNewsletter() {
        SignupPage signupPage = ScenarioContext.get(ScenarioContext.SIGNUP_PAGE);
        signupPage.subscribeToNewsletter();
    }

    @And("I agree to receive special offers")
    public void iAgreeToReceiveSpecialOffers() {
        SignupPage signupPage = ScenarioContext.get(ScenarioContext.SIGNUP_PAGE);
        signupPage.agreeToSpecialOffers();
    }

    @And("I fill in the address details")
    public void iFillInTheAddressDetails(DataTable dataTable) {
        var row = dataTable.asMaps(String.class, String.class).get(0);
        SignupPage signupPage = ScenarioContext.get(ScenarioContext.SIGNUP_PAGE);
        signupPage.fillAddressDetails(
                row.get("firstName"),
                row.get("lastName"),
                row.get("address"),
                row.get("country"),
                row.get("state"),
                row.get("city"),
                row.get("zipcode"),
                row.get("mobile")
        );
    }
}
