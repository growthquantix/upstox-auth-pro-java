package com.upstox.auth.storage;

import com.upstox.auth.model.TokenResponse;
import java.util.Optional;

public interface BaseStorage {
    void saveToken(TokenResponse token);
    Optional<TokenResponse> getToken();
}
