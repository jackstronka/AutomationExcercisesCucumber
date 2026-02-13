@tc09 @regression @products
Feature: Verify Product quantity in Cart

  # TC09: Verify Product quantity in Cart

  Background:
    Given I open the browser
    And I navigate to the home page
    Then I should see the home page

  Scenario: TC09 - Verify Product quantity in Cart
    When I click on View Product for first product on home page
    Then I should be on the product detail page
    When I set product quantity to 4
    And I click Add to cart on product detail page
    And I click View Cart button
    Then I should see the product in cart with quantity 4

