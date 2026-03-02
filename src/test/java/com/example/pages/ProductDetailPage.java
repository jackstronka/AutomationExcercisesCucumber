package com.example.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page Object for product detail page (e.g. /product_details/1).
 */
public class ProductDetailPage extends BasePage {

    private final By productName = By.cssSelector(".product-information h2, .product-info h2");
    private final By productCategory = By.xpath("//div[contains(@class,'product-information') or contains(@class,'product-info')]//*[contains(.,'Category:')]");
    private final By productPrice = By.xpath("//div[contains(@class,'product-information') or contains(@class,'product-info')]//*[contains(.,'Rs.')]");
    private final By productAvailability = By.xpath("//div[contains(@class,'product-information') or contains(@class,'product-info')]//*[contains(.,'Availability:')]");
    private final By productCondition = By.xpath("//div[contains(@class,'product-information') or contains(@class,'product-info')]//*[contains(.,'Condition:')]");
    private final By productBrand = By.xpath("//div[contains(@class,'product-information') or contains(@class,'product-info')]//*[contains(.,'Brand:')]");
    private final By addToCartButton = By.cssSelector("button.btn.btn-default.cart");
    private final By viewCartLinkInModal = By.cssSelector(".modal a[href='/view_cart'], .modal-content a[href='/view_cart']");
    private final By quantityInput = By.id("quantity");

    public ProductDetailPage(WebDriver driver) {
        super(driver);
    }

    public boolean isProductNameVisible() {
        try {
            return getElement(productName).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isProductCategoryVisible() {
        return isElementPresent(productCategory);
    }

    public boolean isProductPriceVisible() {
        return isElementPresent(productPrice);
    }

    public boolean isProductAvailabilityVisible() {
        return isElementPresent(productAvailability);
    }

    public boolean isProductConditionVisible() {
        return isElementPresent(productCondition);
    }

    public boolean isProductBrandVisible() {
        return isElementPresent(productBrand);
    }

    public boolean areAllProductDetailsVisible() {
        return isProductNameVisible()
                && isProductCategoryVisible()
                && isProductPriceVisible()
                && isProductAvailabilityVisible()
                && isProductConditionVisible()
                && isProductBrandVisible();
    }

    @Step("Set quantity to {0}")
    public void setQuantity(int quantity) {
        writeText(quantityInput, String.valueOf(quantity));
    }

    @Step("Click Add to cart")
    public void clickAddToCart() {
        click(addToCartButton);
    }

    @Step("Click View Cart in modal")
    public void clickViewCartInModal() {
        wait.until(ExpectedConditions.elementToBeClickable(viewCartLinkInModal));
        clickViaJavaScript(viewCartLinkInModal);
    }
}
