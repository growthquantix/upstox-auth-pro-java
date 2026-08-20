package io.github.growthquantix.upstoxauth.storage;

import io.github.growthquantix.upstoxauth.model.TokenResponse;
import java.util.Optional;

public interface BaseStorage {
    void saveToken(TokenResponse token);
    Optional<TokenResponse> getToken();
}
