package com.almezi.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class TestContext {
    private static final ThreadLocal<TestContext> CONTEXT_HOLDER = ThreadLocal.withInitial(TestContext::new);

    private Playwright playwright;
    private APIRequestContext requestContext;
    private APIResponse response;
    private String responseBody;
    private Long lastPetId;
    private final Map<String, Object> session = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public static TestContext get() {
        return CONTEXT_HOLDER.get();
    }

    public static void remove() {
        CONTEXT_HOLDER.remove();
    }

    public Playwright getPlaywright() {
        return playwright;
    }

    public void setPlaywright(Playwright playwright) {
        this.playwright = playwright;
    }

    public APIRequestContext getRequestContext() {
        return requestContext;
    }

    public void setRequestContext(APIRequestContext requestContext) {
        this.requestContext = requestContext;
    }

    public APIResponse getResponse() {
        return response;
    }

    public void setResponse(APIResponse response) {
        this.response = response;
        if (response != null) {
            this.responseBody = response.text();
        } else {
            this.responseBody = null;
        }
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public Long getLastPetId() {
        return lastPetId;
    }

    public void setLastPetId(Long lastPetId) {
        this.lastPetId = lastPetId;
    }

    public long generateDynamicPetId() {
        long timestamp = System.currentTimeMillis() % 1_000_000_000L;
        long randomSuffix = ThreadLocalRandom.current().nextInt(1000, 9999);
        this.lastPetId = timestamp * 10000 + randomSuffix;
        return this.lastPetId;
    }

    public APIResponse postRaw(String endpoint, String body) {
        APIResponse resp = requestContext.post(endpoint, RequestOptions.create().setData(body));
        setResponse(resp);
        return resp;
    }

    public APIResponse postRaw(String endpoint, String body, Map<String, String> headers) {
        RequestOptions options = RequestOptions.create().setData(body);
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                options.setHeader(entry.getKey(), entry.getValue());
            }
        }
        APIResponse resp = requestContext.post(endpoint, options);
        setResponse(resp);
        return resp;
    }

    public Map<String, Object> getSession() {
        return session;
    }

    public Object getSessionAttribute(String key) {
        return session.get(key);
    }

    public void setSessionAttribute(String key, Object value) {
        session.put(key, value);
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void reset() {
        response = null;
        responseBody = null;
        lastPetId = null;
        session.clear();
    }
}
