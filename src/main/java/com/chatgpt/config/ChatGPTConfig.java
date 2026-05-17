package com.chatgpt.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.chatgpt.ChatGPTFabricMod;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ChatGPTConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_DIR = Paths.get("config", "chatgptfabric");
	private static final File CONFIG_FILE = CONFIG_DIR.resolve("config.json").toFile();
	
	private static String apiKey = "";
	private static String model = "gpt-4-mini";
	private static String systemPrompt = "You are a helpful Minecraft assistant. Keep responses concise and friendly.";
	private static int maxTokens = 512;
	private static int timeoutSeconds = 30;

	public static void loadConfig() {
		try {
			// Create config directory if it doesn't exist
			if (!CONFIG_DIR.toFile().exists()) {
				Files.createDirectories(CONFIG_DIR);
				ChatGPTFabricMod.LOGGER.info("Created config directory: {}", CONFIG_DIR);
			}

			// Create default config if it doesn't exist
			if (!CONFIG_FILE.exists()) {
				createDefaultConfig();
				ChatGPTFabricMod.LOGGER.info("Created default config file: {}", CONFIG_FILE.getAbsolutePath());
			}

			// Load config from file
			try (FileReader reader = new FileReader(CONFIG_FILE)) {
				JsonObject config = GSON.fromJson(reader, JsonObject.class);
				
				apiKey = config.has("api_key") ? config.get("api_key").getAsString() : "";
				model = config.has("model") ? config.get("model").getAsString() : "gpt-4-mini";
				systemPrompt = config.has("system_prompt") ? config.get("system_prompt").getAsString() : 
					"You are a helpful Minecraft assistant. Keep responses concise and friendly.";
				maxTokens = config.has("max_tokens") ? config.get("max_tokens").getAsInt() : 512;
				timeoutSeconds = config.has("timeout_seconds") ? config.get("timeout_seconds").getAsInt() : 30;
				
				ChatGPTFabricMod.LOGGER.info("Loaded ChatGPT Fabric config from: {}", CONFIG_FILE.getAbsolutePath());
				
				if (apiKey.isEmpty() || apiKey.equals("PUT_API_KEY_HERE")) {
					ChatGPTFabricMod.LOGGER.warn("⚠️  API key not set! Edit config/chatgptfabric/config.json and add your OpenAI API key.");
				}
			}
		} catch (IOException e) {
			ChatGPTFabricMod.LOGGER.error("Failed to load ChatGPT config", e);
		}
	}

	private static void createDefaultConfig() {
		try {
			JsonObject defaultConfig = new JsonObject();
			defaultConfig.addProperty("api_key", "PUT_API_KEY_HERE");
			defaultConfig.addProperty("model", "gpt-4-mini");
			defaultConfig.addProperty("system_prompt", "You are a helpful Minecraft assistant. Keep responses concise and friendly.");
			defaultConfig.addProperty("max_tokens", 512);
			defaultConfig.addProperty("timeout_seconds", 30);

			try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
				GSON.toJson(defaultConfig, writer);
			}
		} catch (IOException e) {
			ChatGPTFabricMod.LOGGER.error("Failed to create default config", e);
		}
	}

	public static void reloadConfig() {
		ChatGPTFabricMod.LOGGER.info("Reloading ChatGPT Fabric config...");
		loadConfig();
		ChatGPTFabricMod.LOGGER.info("Config reloaded successfully!");
	}

	// Getters
	public static String getApiKey() {
		return apiKey;
	}

	public static String getModel() {
		return model;
	}

	public static String getSystemPrompt() {
		return systemPrompt;
	}

	public static int getMaxTokens() {
		return maxTokens;
	}

	public static int getTimeoutSeconds() {
		return timeoutSeconds;
	}

	public static boolean isApiKeyValid() {
		return !apiKey.isEmpty() && !apiKey.equals("PUT_API_KEY_HERE");
	}
}
