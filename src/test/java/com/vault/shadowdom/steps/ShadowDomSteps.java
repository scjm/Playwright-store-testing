package com.vault.shadowdom.steps;

import com.vault.shadowdom.hooks.ScenarioContext;
import com.vault.shadowdom.pages.ShadowDomPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step definitions covering all three VAULT Shadow DOM widgets:
 *   Widget 1 — Shadow DOM Form
 *   Widget 2 — Shadow Counter
 *   Widget 3 — Shadow Select
 */
public class ShadowDomSteps {

    private final ScenarioContext ctx;
    private final ShadowDomPage page;

    public ShadowDomSteps(ScenarioContext ctx) {
        this.ctx = ctx;
        this.page = new ShadowDomPage(ctx.getPage());
    }

    // ── Background ────────────────────────────────────────────────────────────

    @Given("I navigate to the VAULT Shadow DOM page")
    public void iNavigateToTheVaultShadowDomPage() {
        page.navigate();
    }

    // ── Page load & visibility ────────────────────────────────────────────────

    @Then("the page title should contain {string}")
    public void thePageTitleShouldContain(String expected) {
        String actual = page.getTitle();
        System.out.printf("  [Assert] Title: '%s'%n", actual);
        assertThat(actual).as("Page title").containsIgnoringCase(expected);
    }

    @And("the shadow DOM container should be visible on the page")
    public void theShadowDomContainerShouldBeVisibleOnThePage() {
        assertThat(page.isShadowContainerVisible())
                .as("Shadow DOM container should be visible").isTrue();
    }

    @Then("a shadow host element should exist on the page")
    public void aShadowHostElementShouldExistOnThePage() {
        assertThat(page.isShadowHostPresent())
                .as("Shadow host element should exist").isTrue();
    }

    @And("the shadow root should be accessible")
    public void theShadowRootShouldBeAccessible() {
        assertThat(page.isShadowRootAccessible())
                .as("Shadow root should be accessible with children").isTrue();
    }

    @Then("the shadow DOM should contain visible text content")
    public void theShadowDomShouldContainVisibleTextContent() {
        String content = page.getShadowInnerText();
        System.out.printf("  [Assert] Shadow text (80 chars): '%s'%n",
                content.length() > 80 ? content.substring(0, 80) + "..." : content);
        assertThat(content).as("Shadow DOM text content").isNotBlank();
    }

    @And("the shadow content should not be empty")
    public void theShadowContentShouldNotBeEmpty() {
        assertThat(page.shadowContentIsNotEmpty()).as("Shadow content should not be empty").isTrue();
    }

    @Then("shadow DOM elements should have a computed style applied")
    public void shadowDomElementsShouldHaveAComputedStyleApplied() {
        assertThat(page.shadowElementsHaveComputedStyle())
                .as("Shadow DOM elements should have computed CSS style").isTrue();
    }

    @Then("no JS errors should be present in the console")
    public void noJsErrorsShouldBePresentInTheConsole() {
        assertThat(ctx.hasConsoleErrors())
                .as("No JS errors in console. Found: " + ctx.getLastConsoleError()).isFalse();
    }

    // ── Widget 1 — Form ───────────────────────────────────────────────────────

    @When("I type {string} into the shadow form input")
    public void iTypeIntoTheShadowFormInput(String text) {
        page.typeIntoFormInput(text);
        System.out.printf("  [Step] Typed '%s' into shadow form input%n", text);
    }

    @And("I clear the shadow form input")
    public void iClearTheShadowFormInput() {
        page.clearFormInput();
        System.out.println("  [Step] Shadow form input cleared");
    }

    @Then("the shadow form input should contain {string}")
    public void theShadowFormInputShouldContain(String expected) {
        String actual = page.getFormInputValue();
        System.out.printf("  [Assert] Form input value: '%s'%n", actual);
        assertThat(actual).as("Shadow form input value").isEqualTo(expected);
    }

    @Then("the shadow submit button should be enabled")
    public void theShadowSubmitButtonShouldBeEnabled() {
        assertThat(page.isSubmitButtonEnabled()).as("Submit button should be enabled").isTrue();
    }

    @When("I click the shadow DOM submit button")
    public void iClickTheShadowDomSubmitButton() {
        page.clickSubmitButton();
        System.out.println("  [Step] Clicked SUBMIT button");
    }

    // ── Widget 2 — Counter ────────────────────────────────────────────────────

    @Then("the shadow counter value should be {string}")
    public void theShadowCounterValueShouldBe(String expected) {
        String actual = page.getCounterValue();
        System.out.printf("  [Assert] Counter value: '%s' (expected '%s')%n", actual, expected);
        assertThat(actual).as("Shadow counter value").isEqualTo(expected);
    }

    @When("I click the increment button {int} times")
    public void iClickTheIncrementButtonTimes(int times) {
        System.out.printf("  [Step] Clicking + button %d times%n", times);
        page.clickIncrementTimes(times);
    }

    @And("I click the decrement button {int} times")
    public void iClickTheDecrementButtonTimes(int times) {
        System.out.printf("  [Step] Clicking - button %d times%n", times);
        page.clickDecrementTimes(times);
    }

    @And("I click the reset button")
    public void iClickTheResetButton() {
        page.clickReset();
        System.out.println("  [Step] Clicked RESET button");
    }

    // ── Widget 3 — Select ─────────────────────────────────────────────────────

    @Then("the shadow select should have more than {int} option")
    public void theShadowSelectShouldHaveMoreThanOption(int minCount) {
        int count = page.getSelectOptionCount();
        System.out.printf("  [Assert] Select option count: %d%n", count);
        assertThat(count).as("Shadow select option count").isGreaterThan(minCount);
    }

    @When("I select option at index {int} from the shadow dropdown")
    public void iSelectOptionAtIndexFromTheShadowDropdown(int index) {
        page.selectOptionByIndex(index);
    }

    @Then("the shadow select should not show the default placeholder")
    public void theShadowSelectShouldNotShowTheDefaultPlaceholder() {
        String selected = page.getSelectedOption();
        System.out.printf("  [Assert] Selected option: '%s'%n", selected);
        assertThat(selected)
                .as("A real option should be selected")
                .isNotBlank()
                .doesNotContainIgnoringCase("choose");
    }

    // ── Keyboard navigation ───────────────────────────────────────────────────

    @When("I focus the page body")
    public void iFocusThePageBody() {
        page.focusBody();
        System.out.println("  [Step] Page body focused");
    }

    @And("I press Tab to navigate into the shadow DOM")
    public void iPressTabToNavigateIntoTheShadowDom() {
        for (int i = 0; i < 5; i++) page.pressTab();
        System.out.println("  [Step] Tabbed 5 times");
    }

    @Then("a shadow DOM element should receive focus")
    public void aShadowDomElementShouldReceiveFocus() {
        assertThat(page.shadowElementHasFocus())
                .as("A shadow DOM element should have focus").isTrue();
    }

    // ── Nested ────────────────────────────────────────────────────────────────

    @Then("nested shadow DOM elements should be reachable via JavaScript piercing")
    public void nestedShadowDomElementsShouldBeReachableViaJavaScriptPiercing() {
        assertThat(page.nestedShadowElementsReachable())
                .as("Shadow DOM roots should be reachable").isTrue();
    }
}
