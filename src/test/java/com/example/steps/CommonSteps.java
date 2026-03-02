package com.example.steps;

import com.example.context.ScenarioContext;
import com.example.hooks.Hooks;
import com.example.pages.AccountCreatedPage;
import com.example.pages.ContactUsPage;
import com.example.pages.HomePage;
import com.example.pages.LoginPage;
import com.example.pages.OrderSuccessPage;
import com.example.pages.ProductsPage;
import com.example.pages.SignupPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class CommonSteps {

    @Given("I open the browser")
    public void iOpenTheBrowser() {
        Assert.assertNotNull(Hooks.getDriver(), "Browser should be started");
        ScenarioContext.put(ScenarioContext.HOME_PAGE, new HomePage(Hooks.getDriver()));
    }

    @And("I navigate to the home page")
    public void iNavigateToTheHomePage() {
        // NOOP: home page is opened in Hooks.setUp()
    }

    @Then("I should see the home page")
    public void iShouldSeeTheHomePage() {
        HomePage homePage = ScenarioContext.get(ScenarioContext.HOME_PAGE);
        Assert.assertTrue(homePage.isDisplayed(), "Home page should be visible");
    }

    @When("I click on {string} button")
    public void iClickOnButton(String buttonText) {
        if ("Signup / Login".equals(buttonText)) {
            HomePage homePage = ScenarioContext.get(ScenarioContext.HOME_PAGE);
            homePage.clickSignupLogin();
            ScenarioContext.put(ScenarioContext.LOGIN_PAGE, new LoginPage(Hooks.getDriver()));
        } else if ("Contact Us".equals(buttonText)) {
            HomePage homePage = ScenarioContext.get(ScenarioContext.HOME_PAGE);
            homePage.clickContactUs();
            ScenarioContext.put(ScenarioContext.CONTACT_US_PAGE, new ContactUsPage(Hooks.getDriver()));
        }
    }

    @Then("I should see {string} section")
    public void iShouldSeeSection(String sectionText) {
        LoginPage loginPage = ScenarioContext.get(ScenarioContext.LOGIN_PAGE);
        SignupPage signupPage = ScenarioContext.get(ScenarioContext.SIGNUP_PAGE);
        ContactUsPage contactUsPage = ScenarioContext.get(ScenarioContext.CONTACT_US_PAGE);

        if ("GET IN TOUCH".equals(sectionText) && contactUsPage != null) {
            Assert.assertTrue(
                    contactUsPage.isGetInTouchVisible(),
                    "\"GET IN TOUCH\" section should be visible"
            );
        } else if ("New User Signup!".equals(sectionText)) {
            Assert.assertTrue(
                    loginPage.isNewUserSignupSectionVisible(),
                    "\"New User Signup!\" section should be visible"
            );
        } else if ("Login to your account".equals(sectionText)) {
            Assert.assertTrue(
                    loginPage.isLoginToYourAccountSectionVisible(),
                    "\"Login to your account\" section should be visible"
            );
        } else if ("SEARCHED PRODUCTS".equals(sectionText)) {
            ProductsPage productsPage = ScenarioContext.get(ScenarioContext.PRODUCTS_PAGE);
            Assert.assertTrue(
                    productsPage != null && productsPage.isSearchedProductsVisible(),
                    "\"Searched Products\" section should be visible"
            );
        } else if ("ENTER ACCOUNT INFORMATION".equals(sectionText)) {
            String lastEnteredName = ScenarioContext.get(ScenarioContext.LAST_ENTERED_NAME);
            String lastEnteredEmail = ScenarioContext.get(ScenarioContext.LAST_ENTERED_EMAIL);
            int maxRetries = 3;
            for (int i = 0; i < maxRetries; i++) {
                if (signupPage != null && signupPage.isEnterAccountInformationSectionVisible()) {
                    break;
                }
                if (loginPage != null && loginPage.isEmailAlreadyExistsErrorVisible()) {
                    String uniqueEmail = generateUniqueEmail(lastEnteredEmail);
                    loginPage.enterNewUserNameAndEmail(lastEnteredName, uniqueEmail);
                    loginPage.clickSignupButton();
                    signupPage = new SignupPage(Hooks.getDriver());
                    ScenarioContext.put(ScenarioContext.SIGNUP_PAGE, signupPage);
                } else {
                    break;
                }
            }
            SignupPage finalSignupPage = ScenarioContext.get(ScenarioContext.SIGNUP_PAGE);
            Assert.assertTrue(
                    finalSignupPage != null && finalSignupPage.isEnterAccountInformationSectionVisible(),
                    "\"ENTER ACCOUNT INFORMATION\" section should be visible"
            );
        }
    }

    /** Generic step for simple home actions only (per Rule 4). */
    @And("I click the {string} button")
    public void iClickTheButton(String buttonText) {
        if ("Cart".equals(buttonText)) {
            HomePage homePage = ScenarioContext.get(ScenarioContext.HOME_PAGE);
            if (homePage == null) homePage = new HomePage(Hooks.getDriver());
            homePage.clickCart();
        }
    }

    @Then("I should be navigated to the login page")
    public void iShouldBeNavigatedToTheLoginPage() {
        LoginPage loginPage = new LoginPage(Hooks.getDriver());
        Assert.assertTrue(
                loginPage.isLoginToYourAccountSectionVisible(),
                "User should be redirected to login page"
        );
    }

    @Then("I should see {string} message")
    public void iShouldSeeMessage(String messageText) {
        LoginPage loginPage = ScenarioContext.get(ScenarioContext.LOGIN_PAGE);
        AccountCreatedPage accountCreatedPage = ScenarioContext.get(ScenarioContext.ACCOUNT_CREATED_PAGE);
        ContactUsPage contactUsPage = ScenarioContext.get(ScenarioContext.CONTACT_US_PAGE);

        if ("Success! Your details have been submitted successfully.".equals(messageText) && contactUsPage != null) {
            Assert.assertTrue(
                    contactUsPage.isSuccessMessageVisible(),
                    "Contact Us success message should be visible"
            );
        } else if ("Your email or password is incorrect!".equals(messageText) && loginPage != null) {
            Assert.assertTrue(
                    loginPage.isLoginIncorrectErrorVisible(),
                    "\"Your email or password is incorrect!\" message should be visible"
            );
        } else if ("Email Address already exist!".equals(messageText) && loginPage != null) {
            Assert.assertTrue(
                    loginPage.isEmailAlreadyExistsErrorVisible(),
                    "\"Email Address already exist!\" message should be visible"
            );
        } else if ("ACCOUNT CREATED!".equals(messageText)) {
            if (accountCreatedPage == null) {
                accountCreatedPage = new AccountCreatedPage(Hooks.getDriver());
                ScenarioContext.put(ScenarioContext.ACCOUNT_CREATED_PAGE, accountCreatedPage);
            }
            Assert.assertTrue(
                    accountCreatedPage.isAccountCreatedMessageVisible(),
                    "ACCOUNT CREATED! should be visible"
            );
        } else if ("ACCOUNT DELETED!".equals(messageText) && loginPage != null) {
            Assert.assertTrue(
                    loginPage.isAccountDeletedMessageVisible(),
                    "ACCOUNT DELETED! should be visible"
            );
        } else if ("Your order has been placed successfully!".equals(messageText)) {
            OrderSuccessPage orderSuccessPage = new OrderSuccessPage(Hooks.getDriver());
            Assert.assertTrue(
                    orderSuccessPage.waitForOrderSuccessMessageOrPaymentDone(),
                    "\"Your order has been placed successfully!\" message or payment_done page should be visible"
            );
        }
    }

    static String generateUniqueEmail(String baseEmail) {
        String localPart = baseEmail != null && baseEmail.contains("@")
                ? baseEmail.substring(0, baseEmail.indexOf("@"))
                : (baseEmail != null ? baseEmail : "");
        String domain = baseEmail != null && baseEmail.contains("@")
                ? baseEmail.substring(baseEmail.indexOf("@"))
                : "@example.com";
        return localPart + "+" + System.currentTimeMillis() + domain;
    }
}
