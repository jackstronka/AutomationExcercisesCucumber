package com.example.steps;

import com.example.context.ScenarioContext;
import com.example.hooks.Hooks;
import com.example.pages.AccountCreatedPage;
import com.example.pages.HomePage;
import com.example.pages.LoginPage;
import com.example.pages.SignupPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

public class LoginSteps {

    @Given("I have a registered user ready for login")
    public void iHaveARegisteredUserReadyForLogin() {
        completeRegistrationAndLogout(
                "Login User",
                "Test123!",
                "Mr", "15", "6", "1990",
                "Jan", "Kowalski", "ul. Test 1", "India", "Mazowieckie", "Warsaw", "00-001", "123456789"
        );
    }

    @Given("I have a user registered with email {string}")
    public void iHaveAUserRegisteredWithEmail(String email) {
        ensureUserExistsAndNavigateToHome(email, "Test User", "Test123!",
                "Mr", "15", "6", "1990",
                "Jan", "Kowalski", "ul. Test 1", "India", "Mazowieckie", "Warsaw", "00-001", "123456789");
    }

    private void completeRegistrationAndLogout(String name, String password,
                                               String title, String day, String month, String year,
                                               String firstName, String lastName, String address,
                                               String country, String state, String city, String zipcode, String mobile) {
        ScenarioContext.put(ScenarioContext.REGISTERED_PASSWORD, password);
        HomePage homePage = ScenarioContext.get(ScenarioContext.HOME_PAGE);
        homePage.clickSignupLogin();
        LoginPage loginPage = new LoginPage(Hooks.getDriver());
        ScenarioContext.put(ScenarioContext.LOGIN_PAGE, loginPage);

        String registeredEmail = CommonSteps.generateUniqueEmail(
                name.toLowerCase().replace(" ", "") + "@example.com"
        );
        ScenarioContext.put(ScenarioContext.REGISTERED_EMAIL, registeredEmail);
        loginPage.enterNewUserNameAndEmail(name, registeredEmail);
        loginPage.clickSignupButton();

        SignupPage signupPage = new SignupPage(Hooks.getDriver());
        ScenarioContext.put(ScenarioContext.SIGNUP_PAGE, signupPage);
        completeSignupAndLogout(signupPage, password, title, day, month, year,
                firstName, lastName, address, country, state, city, zipcode, mobile);
    }

    private void ensureUserExistsAndNavigateToHome(String email, String name, String password,
                                                    String title, String day, String month, String year,
                                                    String firstName, String lastName, String address,
                                                    String country, String state, String city, String zipcode, String mobile) {
        HomePage homePage = ScenarioContext.get(ScenarioContext.HOME_PAGE);
        homePage.clickSignupLogin();
        LoginPage loginPage = new LoginPage(Hooks.getDriver());
        ScenarioContext.put(ScenarioContext.LOGIN_PAGE, loginPage);
        loginPage.enterNewUserNameAndEmail(name, email);
        loginPage.clickSignupButton();

        SignupPage signupPage = new SignupPage(Hooks.getDriver());
        ScenarioContext.put(ScenarioContext.SIGNUP_PAGE, signupPage);

        if (signupPage.isEnterAccountInformationSectionVisible()) {
            completeSignupAndLogout(signupPage, password, title, day, month, year,
                    firstName, lastName, address, country, state, city, zipcode, mobile);
        }

        homePage = new HomePage(Hooks.getDriver());
        homePage.navigateToHome();
        ScenarioContext.put(ScenarioContext.HOME_PAGE, homePage);
    }

    private void completeSignupAndLogout(SignupPage signupPage, String password,
                                         String title, String day, String month, String year,
                                         String firstName, String lastName, String address,
                                         String country, String state, String city, String zipcode, String mobile) {
        signupPage.fillAccountInformation(password, title, day, month, year);
        signupPage.subscribeToNewsletter();
        signupPage.agreeToSpecialOffers();
        signupPage.fillAddressDetails(firstName, lastName, address, country, state, city, zipcode, mobile);
        signupPage.clickCreateAccount();
        AccountCreatedPage accountCreatedPage = new AccountCreatedPage(Hooks.getDriver());
        ScenarioContext.put(ScenarioContext.ACCOUNT_CREATED_PAGE, accountCreatedPage);
        accountCreatedPage.clickContinue();
        LoginPage loginPage = new LoginPage(Hooks.getDriver());
        ScenarioContext.put(ScenarioContext.LOGIN_PAGE, loginPage);
        loginPage.clickLogout();
    }

    @When("I enter email and password from the registered user")
    public void iEnterEmailAndPasswordFromRegisteredUser() {
        String email = ScenarioContext.get(ScenarioContext.REGISTERED_EMAIL);
        String password = ScenarioContext.get(ScenarioContext.REGISTERED_PASSWORD);
        LoginPage loginPage = ScenarioContext.get(ScenarioContext.LOGIN_PAGE);
        loginPage.enterLoginCredentials(email, password);
    }

    @When("I enter incorrect email address and password")
    public void iEnterIncorrectEmailAddressAndPassword() {
        LoginPage loginPage = ScenarioContext.get(ScenarioContext.LOGIN_PAGE);
        loginPage.enterLoginCredentials("wrong@example.com", "WrongPassword123!");
    }

    @When("I click the Login button on login form")
    public void iClickTheLoginButtonOnLoginForm() {
        LoginPage loginPage = ScenarioContext.get(ScenarioContext.LOGIN_PAGE);
        loginPage.clickLoginButton();
    }
}
