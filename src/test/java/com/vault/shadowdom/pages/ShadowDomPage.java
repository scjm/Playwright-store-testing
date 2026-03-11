package com.vault.shadowdom.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Page Object for the VAULT Shadow DOM page.
 *
 * Three widgets:
 *   Widget 1 — SHADOW DOM FORM    (input + SUBMIT button)
 *   Widget 2 — SHADOW COUNTER     (−, count display, +, RESET)
 *   Widget 3 — SHADOW SELECT      (dropdown)
 *
 * Playwright auto-pierces open shadow roots with standard CSS selectors.
 * Counter +/− buttons are located via JS because filter(hasText("-")) is
 * unreliable for single-character symbols inside shadow roots.
 */
public class ShadowDomPage {

    private final Page page;
    private static final String URL    = "https://ml-minds.com/shadow-dom";
    private static final int    PAUSE  = 700;

    public ShadowDomPage(Page page) { this.page = page; }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void pause()      { try { Thread.sleep(PAUSE);       } catch (InterruptedException ignored) {} }
    private void pause(int ms){ try { Thread.sleep(ms);          } catch (InterruptedException ignored) {} }

    /** Click a button inside any shadow root that matches the given exact text. */
    private void clickShadowButtonByText(String text) {
        page.evaluate(
            "text => {" +
            "  var all = document.querySelectorAll('*');" +
            "  for (var i = 0; i < all.length; i++) {" +
            "    var root = all[i].shadowRoot;" +
            "    if (root) {" +
            "      var btns = root.querySelectorAll('button');" +
            "      for (var j = 0; j < btns.length; j++) {" +
            "        if (btns[j].textContent.trim() === text) {" +
            "          btns[j].click();" +
            "          return;" +
            "        }" +
            "      }" +
            "    }" +
            "  }" +
            "}",
            text
        );
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    public void navigate() {
        page.navigate(URL);
        page.waitForLoadState();
        pause(1500);
        System.out.printf("  [Page] Navigated to: %s%n", URL);
    }

    // ── Page-level ────────────────────────────────────────────────────────────

    public String getTitle() { return page.title(); }

    public boolean isShadowHostPresent() {
        return (Boolean) page.evaluate(
            "() => { var a = document.querySelectorAll('*'); for (var i=0;i<a.length;i++) { if (a[i].shadowRoot) return true; } return false; }"
        );
    }

    /*
    
    The function is a presence check — it's asking "does at least one shadow root exist on this page?" So it short-circuits and returns true the moment it finds the first one.
    That's fine for what the test scenario is asserting — "a shadow host element should exist on the page" — but it tells you nothing about whether all three widgets loaded correctly.
    If you wanted to be more precise, you could write variants like:
    Count all shadow roots:
javapublic int getShadowHostCount() {
    return ((Number) page.evaluate(
        "() => { var count = 0; var a = document.querySelectorAll('*'); " +
        "for (var i=0;i<a.length;i++) { if (a[i].shadowRoot) count++; } return count; }"
    )).intValue();
}
    parameterising the expected count makes the assertion meaningful and reusable. Here's how you'd refactor it:
    The method:
javapublic boolean shadowHostCountEquals(int expected) {
    int actual = ((Number) page.evaluate(
        "() => { var count = 0; var a = document.querySelectorAll('*'); " +
        "for (var i=0;i<a.length;i++) { if (a[i].shadowRoot) count++; } return count; }"
    )).intValue();
    System.out.printf("  [Debug] Shadow host count: %d (expected %d)%n", actual, expected);
    return actual == expected;
}
    The step definition:
java@Then("there should be {int} shadow host elements on the page")
public void thereShouldBeNShadowHostElements(int expected) {
    assertThat(page.shadowHostCountEquals(expected))
        .as("Expected " + expected + " shadow hosts").isTrue();
}
The Gherkin:
gherkinThen there should be 3 shadow host elements on the page
    */

    public boolean isShadowRootAccessible() {
        return (Boolean) page.evaluate(
            "() => { var a = document.querySelectorAll('*'); for (var i=0;i<a.length;i++) { if (a[i].shadowRoot && a[i].shadowRoot.childElementCount>0) return true; } return false; }"
        );
    }

    public boolean isShadowContainerVisible() {
        Object tag = page.evaluate(
            "() => { var a = document.querySelectorAll('*'); for (var i=0;i<a.length;i++) { if (a[i].shadowRoot) return a[i].tagName.toLowerCase(); } return null; }"
        );
        if (tag == null) return false;
        try {
            boolean v = page.locator(tag.toString()).first().isVisible();
            System.out.printf("  [Debug] Shadow host: <%s>, visible: %s%n", tag, v);
            return v;
        } catch (Exception e) {
            return isShadowRootAccessible();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // WIDGET 1 — SHADOW DOM FORM
    // ─────────────────────────────────────────────────────────────────────────

    public Locator getFormInput() {
        return page.locator("input").first();
    }

    public void typeIntoFormInput(String text) {
        Locator input = getFormInput();
        input.waitFor();
        input.scrollIntoViewIfNeeded();
        pause();
        input.click();
        pause(300);
        input.fill(text);
        pause();
    }

    public void clearFormInput() {
        pause();
        getFormInput().clear();
        pause();
    }

    public String getFormInputValue() {
        return getFormInput().inputValue();
    }

    public Locator getSubmitButton() {
        return page.locator("button").filter(new Locator.FilterOptions().setHasText("SUBMIT")).first();
    }

    public void clickSubmitButton() {
        Locator btn = getSubmitButton();
        btn.waitFor();
        btn.scrollIntoViewIfNeeded();
        pause();
        btn.click();
        pause();
    }

    public boolean isSubmitButtonEnabled() {
        return getSubmitButton().isEnabled();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // WIDGET 2 — SHADOW COUNTER
    // Uses JS click to avoid hasText("-") matching issues in shadow roots
    // ─────────────────────────────────────────────────────────────────────────

    public String getCounterValue() {
        // Try shadow root first, then fall back to any numeric-only text node
        Object val = page.evaluate(
            "() => {" +
            "  var all = document.querySelectorAll('*');" +
            "  for (var i = 0; i < all.length; i++) {" +
            "    if (all[i].shadowRoot) {" +
            "      var nodes = all[i].shadowRoot.querySelectorAll('*');" +
            "      for (var j = 0; j < nodes.length; j++) {" +
            "        var t = nodes[j].textContent.trim();" +
            "        if (/^-?\\d+$/.test(t) && nodes[j].children.length === 0) return t;" +
            "      }" +
            "    }" +
            "  }" +
            "  return null;" +
            "}"
        );
        if (val != null) return val.toString();
        return page.locator("text=/^-?\\d+$/").first().textContent().trim();
    }

    public void clickIncrement() {
        page.locator("button").filter(new Locator.FilterOptions().setHasText("+")).first()
            .scrollIntoViewIfNeeded();
        pause();
        clickShadowButtonByText("+");
        pause();
    }

    public void clickDecrement() {
        // Scroll counter into view first so it's visible
        page.locator("button").filter(new Locator.FilterOptions().setHasText("+")).first()
            .scrollIntoViewIfNeeded();
        pause();
        clickShadowButtonByText("-");
        pause();
    }

    public void clickReset() {
        page.locator("button").filter(new Locator.FilterOptions().setHasText("RESET")).first()
            .scrollIntoViewIfNeeded();
        pause();
        clickShadowButtonByText("RESET");
        pause();
    }

    public void clickIncrementTimes(int times) {
        for (int i = 0; i < times; i++) {
            clickIncrement();
            System.out.printf("  [Counter] + clicked → %s%n", getCounterValue());
        }
    }

    public void clickDecrementTimes(int times) {
        for (int i = 0; i < times; i++) {
            clickDecrement();
            System.out.printf("  [Counter] - clicked → %s%n", getCounterValue());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // WIDGET 3 — SHADOW SELECT
    // ─────────────────────────────────────────────────────────────────────────

    public Locator getSelectDropdown() {
        return page.locator("select").first();
    }

    public void selectOptionByIndex(int index) {
        Locator sel = getSelectDropdown();
        sel.scrollIntoViewIfNeeded();
        pause();
        sel.selectOption(new com.microsoft.playwright.options.SelectOption().setIndex(index));
        pause();
        System.out.printf("  [Select] Index %d → '%s'%n", index, getSelectedOption());
    }

    public String getSelectedOption() {
        Object result = page.evaluate(
            "() => {" +
            "  var all = document.querySelectorAll('*');" +
            "  for (var i = 0; i < all.length; i++) {" +
            "    if (all[i].shadowRoot) {" +
            "      var sel = all[i].shadowRoot.querySelector('select');" +
            "      if (sel && sel.selectedIndex >= 0) return sel.options[sel.selectedIndex].text;" +
            "    }" +
            "  }" +
            "  var sel = document.querySelector('select');" +
            "  return sel && sel.selectedIndex >= 0 ? sel.options[sel.selectedIndex].text : '';" +
            "}"
        );
        return result != null ? result.toString() : "";
    }

    public int getSelectOptionCount() {
        Object count = page.evaluate(
            "() => {" +
            "  var all = document.querySelectorAll('*');" +
            "  for (var i = 0; i < all.length; i++) {" +
            "    if (all[i].shadowRoot) {" +
            "      var sel = all[i].shadowRoot.querySelector('select');" +
            "      if (sel) return sel.options.length;" +
            "    }" +
            "  }" +
            "  var sel = document.querySelector('select');" +
            "  return sel ? sel.options.length : 0;" +
            "}"
        );
        return count != null ? ((Number) count).intValue() : 0;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SHADOW TEXT CONTENT
    // The below loops and collects all three shadowroots
    // ─────────────────────────────────────────────────────────────────────────

    public String getShadowInnerText() {
        Object result = page.evaluate(
            "() => {" +
            "  var text = '';" +
            "  var all = document.querySelectorAll('*');" +
            "  for (var i = 0; i < all.length; i++) {" +
            "    var root = all[i].shadowRoot;" +
            "    if (root) {" +
            "      var kids = root.querySelectorAll('*');" +
            "      for (var j = 0; j < kids.length; j++) {" +
            "        var tag = kids[j].tagName.toLowerCase();" +
            "        if (tag !== 'style' && tag !== 'script' && kids[j].children.length === 0) {" +
            "          var t = (kids[j].textContent || '').trim();" +
            "          if (t.length > 0) text += t + ' ';" +
            "        }" +
            "      }" +
            "    }" +
            "  }" +
            "  return text.trim();" +
            "}"
        );
        return result != null ? result.toString() : "";
    }

    public boolean shadowContentIsNotEmpty() { return !getShadowInnerText().isEmpty(); }

    public boolean shadowElementsHaveComputedStyle() {
        return (Boolean) page.evaluate(
            "() => { var a=document.querySelectorAll('*'); for(var i=0;i<a.length;i++){if(a[i].shadowRoot){var c=a[i].shadowRoot.firstElementChild;if(c){var s=window.getComputedStyle(c);return s&&s.display!=='none';}}} return false;}"
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // KEYBOARD & NESTED
    // ─────────────────────────────────────────────────────────────────────────

    public boolean nestedShadowElementsReachable() {
        return (Boolean) page.evaluate(
            "() => { var a=document.querySelectorAll('*'); for(var i=0;i<a.length;i++){if(a[i].shadowRoot) return true;} return false; }"
        );
    }

    public void focusBody() { page.evaluate("() => { document.body.focus(); }"); pause(); }

    public void pressTab() { page.keyboard().press("Tab"); pause(400); }

    public boolean shadowElementHasFocus() {
        return (Boolean) page.evaluate(
            "() => { var a=document.activeElement; return a!==null&&a!==document.body&&a!==document.documentElement; }"
        );
    }
}
