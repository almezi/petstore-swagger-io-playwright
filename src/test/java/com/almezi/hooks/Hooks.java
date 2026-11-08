package com.almezi.hooks;

import com.almezi.context.TestContext;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import java.util.HashMap;
import java.util.Map;

public class Hooks {

    private static final String BASE_URL = System.getProperty("base.url", "https://petstore.swagger.io/v2/");

    private static volatile Playwright shared;

    private static Playwright sharedPlaywright() {
        Playwright local = shared;
        if (local == null) {
            synchronized (Hooks.class) {
                local = shared;
                if (local == null) {
                    local = Playwright.create();
                    shared = local;
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> shared.close()));
                }
            }
        }
        return local;
    }

    @Before
    public void setUp(Scenario scenario) {
        TestContext context = TestContext.get();
        context.reset();

        Playwright playwright = sharedPlaywright();
        context.setPlaywright(playwright);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        headers.put("api_key", System.getProperty("api.key", "special-key"));

        APIRequestContext requestContext = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(BASE_URL)
                        .setExtraHTTPHeaders(headers)
        );
        context.setRequestContext(requestContext);
    }

    @After
    public void tearDown(Scenario scenario) {
        TestContext context = TestContext.get();
        try {
            if (context.getResponse() != null) {
                String log = String.format("HTTP %d\nPayload / Response:\n%s",
                        context.getResponse().status(),
                        context.getResponseBody() != null ? context.getResponseBody() : "");
                scenario.attach(log.getBytes(), "text/plain", "API Response");
            }
        } finally {
            if (context.getRequestContext() != null) {
                context.getRequestContext().dispose();
            }
            TestContext.remove();
        }
    }
}
