package com.upstox.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.upstox.auth.exceptions.UpstoxAuthException;
import com.upstox.auth.model.TokenResponse;
import com.upstox.auth.storage.BaseStorage;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jboss.aerogear.security.otp.Totp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class UpstoxAuthenticator {
    private static final Logger logger = LoggerFactory.getLogger(UpstoxAuthenticator.class);
    private final String apiKey;
    private final String apiSecret;
    private final String redirectUri;
    private final String mobileNo;
    private final String pin;
    private final String totpKey;
    private final BaseStorage storage;
    private final boolean headless;
    private final ObjectMapper mapper = new ObjectMapper();
    private final OkHttpClient httpClient = new OkHttpClient();

    private UpstoxAuthenticator(Builder builder) {
        this.apiKey = builder.apiKey;
        this.apiSecret = builder.apiSecret;
        this.redirectUri = builder.redirectUri;
        this.mobileNo = builder.mobileNo;
        this.pin = builder.pin;
        this.totpKey = builder.totpKey;
        this.storage = builder.storage;
        this.headless = builder.headless;
    }

    public String generateAuthUrl() {
        return String.format(
            "https://api.upstox.com/v2/login/authorization/dialog?response_type=code&client_id=%s&redirect_uri=%s",
            apiKey, redirectUri
        );
    }

    public String getAuthCode() {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setArgs(java.util.Arrays.asList("--no-sandbox", "--disable-setuid-sandbox", "--disable-blink-features=AutomationControlled")));

            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"));

            // Mask webdriver
            context.addInitScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

            Page page = context.newPage();
            String authUrl = generateAuthUrl();

            logger.info("Navigating to Upstox login...");
            page.navigate(authUrl);

            // 1. Enter Mobile
            page.waitForSelector("#mobileNum");
            page.fill("#mobileNum", mobileNo);
            page.click("button:has-text('Get OTP')");

            // 2. Enter TOTP
            page.waitForSelector("#otpNum");
            Totp totp = new Totp(totpKey);
            page.fill("#otpNum", totp.now());
            page.click("button:has-text('Continue')");

            // 3. Enter PIN
            page.waitForSelector("input[type='password']");
            page.locator("input[type='password']").first().fill(pin);
            page.click("button:has-text('Continue')");

            // 4. Capture Code from Redirect
            logger.info("Waiting for redirect...");
            for (int i = 0; i < 60; i++) {
                String currentUrl = page.url();
                if (currentUrl.contains("code=")) {
                    try {
                        URL url = new URL(currentUrl);
                        String query = url.getQuery();
                        for (String param : query.split("&")) {
                            if (param.startsWith("code=")) {
                                return param.split("=")[1];
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Error parsing URL: {}", currentUrl);
                    }
                }
                page.waitForTimeout(500);
            }
            throw new UpstoxAuthException("Timed out waiting for auth code redirect.");
        } catch (Exception e) {
            throw new UpstoxAuthException("Automation failed", e);
        }
    }

    public TokenResponse exchangeCodeForToken(String code) {
        RequestBody formBody = new FormBody.Builder()
            .add("code", code)
            .add("client_id", apiKey)
            .add("client_secret", apiSecret)
            .add("redirect_uri", redirectUri)
            .add("grant_type", "authorization_code")
            .build();

        Request request = new Request.Builder()
            .url("https://api.upstox.com/v2/login/authorization/token")
            .header("Accept", "application/json")
            .post(formBody)
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new UpstoxAuthException("Token exchange failed: " + response.body().string());
            }
            TokenResponse token = mapper.readValue(response.body().string(), TokenResponse.class);
            if (storage != null) {
                storage.saveToken(token);
            }
            return token;
        } catch (IOException e) {
            throw new UpstoxAuthException("Network error during token exchange", e);
        }
    }

    public String getAccessToken() {
        if (storage != null) {
            Optional<TokenResponse> existing = storage.getToken();
            if (existing.isPresent() && !existing.get().isExpired()) {
                return existing.get().getAccessToken();
            }
        }

        String code = getAuthCode();
        return exchangeCodeForToken(code).getAccessToken();
    }

    public static class Builder {
        private String apiKey;
        private String apiSecret;
        private String redirectUri;
        private String mobileNo;
        private String pin;
        private String totpKey;
        private BaseStorage storage;
        private boolean headless = true;

        public Builder apiKey(String apiKey) { this.apiKey = apiKey; return this; }
        public Builder apiSecret(String apiSecret) { this.apiSecret = apiSecret; return this; }
        public Builder redirectUri(String redirectUri) { this.redirectUri = redirectUri; return this; }
        public Builder mobileNo(String mobileNo) { this.mobileNo = mobileNo; return this; }
        public Builder pin(String pin) { this.pin = pin; return this; }
        public Builder totpKey(String totpKey) { this.totpKey = totpKey; return this; }
        public Builder storage(BaseStorage storage) { this.storage = storage; return this; }
        public Builder headless(boolean headless) { this.headless = headless; return this; }

        public UpstoxAuthenticator build() {
            return new UpstoxAuthenticator(this);
        }
    }
}
