@ShadowDom
Feature: VAULT Shadow DOM Interactions
  As a QA engineer testing the VAULT store
  I want to interact with all three Shadow DOM widgets
  So that I can confirm they behave correctly for end users

  Background:
    Given I navigate to the VAULT Shadow DOM page

  # ─────────────────────────────────────────────────────────────
  # PAGE LOAD & VISIBILITY
  # ─────────────────────────────────────────────────────────────

  @smoke @visibility
  Scenario: Shadow DOM page loads successfully
    Then the page title should contain "VAULT"
    And the shadow DOM container should be visible on the page

  @smoke @visibility
  Scenario: Shadow host element is present and accessible
    Then a shadow host element should exist on the page
    And the shadow root should be accessible

  @smoke @content
  Scenario: Shadow DOM widgets render visible text content
    Then the shadow DOM should contain visible text content
    And the shadow content should not be empty

  # ─────────────────────────────────────────────────────────────
  # WIDGET 1 — SHADOW DOM FORM
  # ─────────────────────────────────────────────────────────────

  @widget1 @form @input
  Scenario: User can type into the shadow DOM form input
    When I type "ShadowTest@vault.com" into the shadow form input
    Then the shadow form input should contain "ShadowTest@vault.com"

  @widget1 @form @input
  Scenario: User can clear and retype in the shadow DOM form input
    When I type "initial text" into the shadow form input
    And I clear the shadow form input
    And I type "updated text" into the shadow form input
    Then the shadow form input should contain "updated text"

  @widget1 @form @submit
  Scenario: Submit button is enabled and clickable
    Then the shadow submit button should be enabled
    When I click the shadow DOM submit button
    Then no JS errors should be present in the console

  @widget1 @form @submit
  Scenario: User can fill the form and submit it
    When I type "test@example.com" into the shadow form input
    And I click the shadow DOM submit button
    Then no JS errors should be present in the console

  @widget1 @form @parameterised
  Scenario Outline: Shadow form input accepts various email formats
    When I type "<email>" into the shadow form input
    Then the shadow form input should contain "<email>"

    Examples:
      | email                    |
      | user@example.com         |
      | test.user+tag@domain.org |
      | qa_engineer@vault.io     |

  # ─────────────────────────────────────────────────────────────
  # WIDGET 2 — SHADOW COUNTER
  # Note: each scenario gets a fresh page load so counter starts at 0
  # ─────────────────────────────────────────────────────────────

  @widget2 @counter
  Scenario: Counter starts at zero on page load
    Then the shadow counter value should be "0"

  @widget2 @counter
  Scenario: Increment button increases the counter
    When I click the increment button 3 times
    Then the shadow counter value should be "3"

  @widget2 @counter
  Scenario: Clicking increment and decrement buttons does not throw JS errors
    When I click the increment button 2 times
    And I click the decrement button 1 times
    Then no JS errors should be present in the console

  # ─────────────────────────────────────────────────────────────
  # WIDGET 3 — SHADOW SELECT
  # ─────────────────────────────────────────────────────────────

  @widget3 @select
  Scenario: Shadow select dropdown has options available
    Then the shadow select should have more than 1 option

  @widget3 @select
  Scenario: User can select the second option in the shadow dropdown
    When I select option at index 1 from the shadow dropdown
    Then the shadow select should not show the default placeholder

  @widget3 @select
  Scenario: User can select multiple options in sequence without errors
    When I select option at index 1 from the shadow dropdown
    And I select option at index 2 from the shadow dropdown
    Then the shadow select should not show the default placeholder
    And no JS errors should be present in the console

  # ─────────────────────────────────────────────────────────────
  # KEYBOARD & ACCESSIBILITY
  # ─────────────────────────────────────────────────────────────

  @keyboard @accessibility
  Scenario: Shadow DOM elements are reachable by keyboard Tab navigation
    When I focus the page body
    And I press Tab to navigate into the shadow DOM
    Then a shadow DOM element should receive focus

  # ─────────────────────────────────────────────────────────────
  # NESTED SHADOW DOM
  # ─────────────────────────────────────────────────────────────

  @nested @advanced
  Scenario: Shadow DOM roots are accessible via JavaScript traversal
    Then nested shadow DOM elements should be reachable via JavaScript piercing
