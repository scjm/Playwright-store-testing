package com.vault.shadowdom.hooks;

import com.microsoft.playwright.*;

/**
 * Manages the Playwright browser, context, and page lifecycle.
 * A single instance is shared across the entire Cucumber scenario
 * via dependency injection through the ScenarioContext.
 */
public class PlaywrightManager {

    private static Playwright playwright;
    private static Browser browser;

    private BrowserContext context;
    private Page page;

    // ── Browser config ───────────────────────────────────────────────────────

    private static final boolean HEADLESS = false;   // flip to false to watch tests run
    private static final int     TIMEOUT_MS = 15_000;

    // ── Static browser initialisation (shared across scenarios) ─────────────

    public static void initBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(HEADLESS)
                        .setSlowMo(HEADLESS ? 0 : 300)  // slows visible run for debugging
        );
    }

    public static void closeBrowser() {
        if (browser != null)  browser.close();
        if (playwright != null) playwright.close();
    }

    // ── Per-scenario context & page ──────────────────────────────────────────

    public void newContext() {
        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1280, 800)
                .setIgnoreHTTPSErrors(true)
        );
        context.setDefaultTimeout(TIMEOUT_MS);

        page = context.newPage();
        page.setDefaultTimeout(TIMEOUT_MS);
    }

    public void closeContext() {
        if (page != null)    page.close();
        if (context != null) context.close();
    }

    // ── Accessors ────────────────────────────────────────────────────────────

    public Page getPage() {
        return page;
    }

    public BrowserContext getContext() {
        return context;
    }
}
