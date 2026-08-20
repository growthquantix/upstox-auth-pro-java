# 🚀 Upstox Auth Pro (Java)

[![Maven Central](https://img.shields.io/maven-central/v/io.github.growthquantix/unofficial-upstox-auth-pro.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.growthquantix%22%20AND%20a:%22unofficial-upstox-auth-pro%22)

> [!WARNING]
> **Unofficial Client:** This library is a community-driven open-source project. It is **not** affiliated with, endorsed by, or officially associated with Upstox or RKSV Securities.

**Upstox Auth Pro** is a high-performance Java library designed to automate the Upstox API login flow. It handles **Headless Login**, **2FA (TOTP) generation**, and **Authorization Code capture** entirely through code.

## ✨ Features

- 🕵️ **Stealth Automation:** Uses Playwright for Java with built-in headers to bypass bot detection.
- 🔐 **Integrated TOTP:** Automatically generates 2FA codes using Aerogear OTP.
- 📦 **Persistent Storage:** Built-in `FileStorage` using Jackson for JSON serialization.
- 🛠️ **Fluent API:** Clean Builder pattern for easy configuration.

## 📦 Installation (Maven)

Add this to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.growthquantix</groupId>
    <artifactId>unofficial-upstox-auth-pro</artifactId>
    <version>0.1.0</version>
</dependency>
```

## 🚀 Quick Start

```java
import io.github.growthquantix.upstoxauth.UpstoxAuthenticator;
import io.github.growthquantix.upstoxauth.storage.FileStorage;

public class Main {
    public static void main(String[] args) {
        UpstoxAuthenticator auth = new UpstoxAuthenticator.Builder()
            .apiKey("YOUR_API_KEY")
            .apiSecret("YOUR_API_SECRET")
            .redirectUri("https://your-redirect-url.com")
            .mobileNo("9876543210")
            .pin("123456")
            .totpKey("YOUR_TOTP_KEY")
            .storage(new FileStorage("upstox_token.json"))
            .headless(true)
            .build();

        // This will check storage first, and only automate login if needed
        String accessToken = auth.getAccessToken();
        System.out.println("Valid Access Token: " + accessToken);
    }
}
```

## 🔐 Getting Your Credentials

Please refer to the [Upstox Developer Portal](https://developer.upstox.com/) to get your API Key and Secret. For the TOTP Key, check your Upstox Security settings under 2FA.

## ⚖️ License

Distributed under the MIT License.
