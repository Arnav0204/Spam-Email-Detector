package org.example.Configuration;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {
    private static final Dotenv dotenv = Dotenv.load();

    public static String getSecretKey() {
        return dotenv.get("APP_SECRET_KEY");
    }

    public static String getUsername(){
        return dotenv.get("DATABASE_USER_NAME");
    }

    public static String getPassword(){
        return dotenv.get("DATABASE_PASSWORD");
    }

    public static String getUrl(){
        return dotenv.get("DATABASE_URL");
    }

    public static String getApiUrl(){
        return  dotenv.get("API_URL");
    }
}
