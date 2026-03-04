# VAULT Shadow DOM Tests

Cucumber + Playwright + TestNG test suite targeting the Shadow DOM page at [https://ml-minds.com/shadow-dom](https://ml-minds.com/shadow-dom).

---

## Stack

| Layer | Library |
|-------|---------|
| Test framework | TestNG 7 |
| BDD | Cucumber 7 (cucumber-testng) |
| Browser automation | Playwright for Java 1.44 |
| Assertions | AssertJ |
| Build | Maven |

---

## Project Structure

```
vault-shadow-tests/
├── pom.xml
├── testng.xml
└── src/test/
    ├── java/com/vault/shadowdom/
    │   ├── hooks/
    │   │   ├── PlaywrightManager.java   ← browser/context lifecycle
    │   │   ├── ScenarioContext.java     ← shared state via Cucumber DI
    │   │   └── CucumberHooks.java       ← @Before/@After scenario hooks
    │   ├── pages/
    │   │   └── ShadowDomPage.java       ← Page Object with shadow piercing
    │   ├── steps/
    │   │   └── ShadowDomSteps.java      ← All step definitions
    │   └── runners/
    │       └── ShadowDomTestRunner.java ← TestNG entry point
    └── resources/
        └── features/
            └── shadow_dom.feature       ← Gherkin scenarios
```

---

## Prerequisites

- Java 11+
- Maven 3.8+
- Internet access (Playwright downloads Chromium on first run)

---

## Running the Tests

```bash
# Clone and enter the project
cd vault-shadow-tests

# Run all scenarios
mvn test

# Run only @smoke scenarios
mvn test -Dcucumber.filter.tags="@smoke"

# Run only @form scenarios
mvn test -Dcucumber.filter.tags="@form"

# Run with visible browser (set HEADLESS=false in PlaywrightManager.java first)
mvn test
```

---

## Test Reports

After a run, reports are written to `target/cucumber-reports/`:

| File | Description |
|------|-------------|
| `report.html` | Human-readable HTML report — open in browser |
| `report.json` | Machine-readable, good for CI dashboards |
| `report.xml` | JUnit XML for Jenkins/GitHub Actions |
| `timeline/` | Visual timeline of scenario execution |

---

## Scenarios Covered

| Tag | What it tests |
|-----|---------------|
| `@smoke` | Page loads, shadow host present |
| `@visibility` | Shadow container visible, root accessible |
| `@form` | Input typing, clearing, form submit |
| `@content` | Shadow inner text present, not empty |
| `@button` | Button enabled, no disabled attribute |
| `@nested` | Nested shadow root traversal |
| `@keyboard` | Tab navigation into shadow root |
| `@parameterised` | Data-driven input with multiple emails |

---

## Key Design Decisions

### Shadow DOM Piercing
Playwright automatically pierces **open** shadow roots using standard CSS selectors.  
The `pierce/` pseudo-class is used explicitly where clarity matters:
```java
page.locator("pierce/input")   // explicitly pierces shadow roots
page.locator("input")          // also works — Playwright auto-pierces
```

### Dependency Injection
`ScenarioContext` is shared between `CucumberHooks` and `ShadowDomSteps` via **Cucumber's built-in PicoContainer DI** — no extra DI framework needed. Both classes declare it as a constructor parameter and receive the same instance per scenario.

### Browser Lifecycle
- Browser is launched **once** per test run (`@BeforeAll`)
- A fresh `BrowserContext` and `Page` are created **per scenario** (`@Before`)
- Failures get a **full-page screenshot** automatically attached to the Cucumber report

---

## Switching to Headed Mode (Debug)

In `PlaywrightManager.java`, change:
```java
private static final boolean HEADLESS = true;
// → 
private static final boolean HEADLESS = false;
```
# Playwright-store-testing
