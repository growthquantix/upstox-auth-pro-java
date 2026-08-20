package io.github.growthquantix.upstoxauth.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.growthquantix.upstoxauth.model.TokenResponse;
import io.github.growthquantix.upstoxauth.exceptions.UpstoxAuthException;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class FileStorage implements BaseStorage {
    private final String filePath;
    private final ObjectMapper mapper = new ObjectMapper();

    public FileStorage(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void saveToken(TokenResponse token) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), token);
        } catch (IOException e) {
            throw new UpstoxAuthException("Failed to save token to file: " + filePath, e);
        }
    }

    @Override
    public Optional<TokenResponse> getToken() {
        File file = new File(filePath);
        if (!file.exists()) {
            return Optional.empty();
        }
        try {
            return Optional.of(mapper.readValue(file, TokenResponse.class));
        } catch (IOException e) {
            throw new UpstoxAuthException("Failed to read token from file: " + filePath, e);
        }
    }
}
