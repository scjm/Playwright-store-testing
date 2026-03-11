package com.vault.shadowdom.hooks;

import com.microsoft.playwright.Page;

/**
 * Shared scenario state passed between step-definition classes via
 * Cucumber's built-in PicoContainer dependency injection.
 *
 * Every step class that declares this as a constructor parameter
 * receives the SAME instance within a single scenario.
 */
public class ScenarioContext {

    private final PlaywrightManager playwrightManager;

    // Scenario-scoped mutable state
    private String lastConsoleError = null;

    public ScenarioContext() {
        this.playwrightManager = new PlaywrightManager();
    }

    // ── Playwright access ────────────────────────────────────────────────────

    public PlaywrightManager getPlaywrightManager() {
        return playwrightManager;
    }

    public Page getPage() {
        return playwrightManager.getPage();
    }

    // ── Console error tracking ───────────────────────────────────────────────

    public void setLastConsoleError(String error) {
        this.lastConsoleError = error;
    }

    public String getLastConsoleError() {
        return lastConsoleError;
    }

    public boolean hasConsoleErrors() {
        return lastConsoleError != null;
    }
}
