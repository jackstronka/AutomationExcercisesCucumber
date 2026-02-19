package com.example.steps;

import com.example.context.ScenarioContext;
import com.example.hooks.Hooks;
import com.example.pages.CartPage;
import com.example.pages.HomePage;
import com.example.pages.ProductDetailPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class ProductQuantitySteps {

    @When("I click on View Product for first product on home page")
    public void iClickOnViewProductForFirstProductOnHomePage() {
        HomePage homePage = ScenarioContext.get(ScenarioContext.HOME_PAGE);
        homePage.clickFirstHomeViewProduct();
        ScenarioContext.put(ScenarioContext.PRODUCT_DETAIL_PAGE, new ProductDetailPage(Hooks.getDriver()));
    }

    @When("I set product quantity to {int}")
    public void iSetProductQuantityTo(int quantity) {
        ProductDetailPage productDetailPage = ScenarioContext.get(ScenarioContext.PRODUCT_DETAIL_PAGE);
        productDetailPage.setQuantity(quantity);
    }

    @And("I click Add to cart on product detail page")
    public void iClickAddToCartOnProductDetailPage() {
        ProductDetailPage productDetailPage = ScenarioContext.get(ScenarioContext.PRODUCT_DETAIL_PAGE);
        productDetailPage.clickAddToCart();
    }

    @And("I click View Cart button")
    public void iClickViewCartButton() {
        ProductDetailPage productDetailPage = ScenarioContext.get(ScenarioContext.PRODUCT_DETAIL_PAGE);
        productDetailPage.clickViewCartInModal();
    }

    @Then("I should see the product in cart with quantity {int}")
    public void iShouldSeeTheProductInCartWithQuantity(int expectedQuantity) {
        CartPage cartPage = new CartPage(Hooks.getDriver());
        cartPage.waitForProductToAppear();
        Assert.assertTrue(
                cartPage.isAnyProductVisible(),
                "At least one product should be visible in cart"
        );
        int actualQuantity = cartPage.getFirstProductQuantity();
        Assert.assertEquals(
                actualQuantity,
                expectedQuantity,
                "Product quantity in cart should equal " + expectedQuantity
        );
    }
}

