package com.vault.shadowdom.hooks;

import com.microsoft.playwright.ConsoleMessage;
import com.microsoft.playwright.Page;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;

/**
 * Cucumber lifecycle hooks.
 *
 * @BeforeAll / @AfterAll  →  browser start/stop once per test run
 * @Before / @After        →  fresh context + page per scenario
 */
public class CucumberHooks {

    private final ScenarioContext scenarioContext;

    public CucumberHooks(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    // ── Static (run-level) hooks ─────────────────────────────────────────────

    @BeforeAll
    public static void launchBrowser() {
        System.out.println("\n[VAULT] Launching Chromium browser...");
        PlaywrightManager.initBrowser();
    }

    @AfterAll
    public static void closeBrowser() {
        System.out.println("\n[VAULT] Closing browser.");
        PlaywrightManager.closeBrowser();
    }

    // ── Per-scenario hooks ───────────────────────────────────────────────────

    @Before
    public void beforeScenario(Scenario scenario) {
        System.out.printf("%n[VAULT] ▶ Starting scenario: %s%n", scenario.getName());

        PlaywrightManager manager = scenarioContext.getPlaywrightManager();
        manager.newContext();

        // Wire up console error listener so steps can assert on it
        Page page = manager.getPage();
        page.onConsoleMessage((ConsoleMessage msg) -> {
            if ("error".equalsIgnoreCase(msg.type())) {
                System.err.printf("  [CONSOLE ERROR] %s%n", msg.text());
                scenarioContext.setLastConsoleError(msg.text());
            }
        });
    }

    @After
    public void afterScenario(Scenario scenario) {
        Page page = scenarioContext.getPage();

        // Attach screenshot on failure
        if (scenario.isFailed() && page != null) {
            try {
                byte[] screenshot = page.screenshot(
                        new Page.ScreenshotOptions().setFullPage(true)
                );
                scenario.attach(screenshot, "image/png", "Failure Screenshot");
                System.out.printf("  [VAULT] Screenshot attached for failed scenario: %s%n",
                        scenario.getName());
            } catch (Exception e) {
                System.err.println("  [VAULT] Could not capture screenshot: " + e.getMessage());
            }
        }

        scenarioContext.getPlaywrightManager().closeContext();
        System.out.printf("[VAULT] ■ Finished scenario: %s → %s%n",
                scenario.getName(), scenario.getStatus());
    }
}
