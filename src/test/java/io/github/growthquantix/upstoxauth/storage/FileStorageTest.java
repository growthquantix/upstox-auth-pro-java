package io.github.growthquantix.upstoxauth.storage;

import io.github.growthquantix.upstoxauth.model.TokenResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageTest {

    @TempDir
    Path tempDir;

    @Test
    void testSaveAndGetToken() {
        Path filePath = tempDir.resolve("token.json");
        FileStorage storage = new FileStorage(filePath.toString());

        TokenResponse token = new TokenResponse();
        token.setAccessToken("test-access-token");
        token.setTokenType("Bearer");
        token.setExpiresIn(3600L);

        storage.saveToken(token);

        Optional<TokenResponse> retrieved = storage.getToken();
        assertTrue(retrieved.isPresent());
        assertEquals("test-access-token", retrieved.get().getAccessToken());
        assertEquals("Bearer", retrieved.get().getTokenType());
        assertEquals(3600L, retrieved.get().getExpiresIn());
    }

    @Test
    void testGetTokenEmpty() {
        FileStorage storage = new FileStorage("non-existent.json");
        Optional<TokenResponse> retrieved = storage.getToken();
        assertFalse(retrieved.isPresent());
    }

    @Test
    void testTokenExpiration() {
        TokenResponse token = new TokenResponse();
        token.setExpiresIn(3600L); // 1 hour
        token.setCreatedAt(System.currentTimeMillis() - 4000000); // More than 1 hour ago

        assertTrue(token.isExpired());

        token.setCreatedAt(System.currentTimeMillis());
        assertFalse(token.isExpired());
    }
}
