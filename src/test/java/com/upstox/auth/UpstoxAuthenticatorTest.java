package com.upstox.auth;

import com.upstox.auth.storage.BaseStorage;
import com.upstox.auth.storage.FileStorage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpstoxAuthenticatorTest {

    @Test
    void testBuilder() {
        BaseStorage storage = new FileStorage("dummy.json");
        UpstoxAuthenticator auth = new UpstoxAuthenticator.Builder()
                .apiKey("key")
                .apiSecret("secret")
                .redirectUri("http://localhost")
                .mobileNo("1234567890")
                .pin("1234")
                .totpKey("totp")
                .storage(storage)
                .headless(false)
                .build();

        assertNotNull(auth);
        assertEquals("https://api.upstox.com/v2/login/authorization/dialog?response_type=code&client_id=key&redirect_uri=http://localhost", auth.generateAuthUrl());
    }
}
