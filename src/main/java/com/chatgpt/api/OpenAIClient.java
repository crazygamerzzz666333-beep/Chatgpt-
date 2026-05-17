package com.chatgpt.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.chatgpt.ChatGPTFabricMod;
import com.chatgpt.config.ChatGPTConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class OpenAIClient {
	private static final String API_URL = "https://api.openai.com/v1/chat/completions";
	private static final Gson GSON = new Gson();
	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
		.connectTimeout(Duration.ofSeconds(10))
		.build();

	public static CompletableFuture<String> askChatGPT(String question) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				if (!ChatGPTConfig.isApiKeyValid()) {
					return "❌ API key missing. Edit config/chatgptfabric/config.json and add your OpenAI API key.";
				}

				// Create request body
				JsonObject requestBody = new JsonObject();
				requestBody.addProperty("model", ChatGPTConfig.getModel());
				requestBody.addProperty("temperature", 0.7);
				requestBody.addProperty("max_tokens", ChatGPTConfig.getMaxTokens());

				// Add system message
				JsonArray messages = new JsonArray();
				JsonObject systemMessage = new JsonObject();
				systemMessage.addProperty("role", "system");
				systemMessage.addProperty("content", ChatGPTConfig.getSystemPrompt());
				messages.add(systemMessage);

				// Add user question
				JsonObject userMessage = new JsonObject();
				userMessage.addProperty("role", "user");
				userMessage.addProperty("content", question);
				messages.add(userMessage);

				requestBody.add("messages", messages);

				// Create HTTP request
				HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(API_URL))
					.timeout(Duration.ofSeconds(ChatGPTConfig.getTimeoutSeconds()))
					.header("Content-Type", "application/json")
					.header("Authorization", "Bearer " + ChatGPTConfig.getApiKey())
					.POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(requestBody)))
					.build();

				// Send request
				HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

				// Handle response
				return handleResponse(response);

			} catch (java.net.ConnectException e) {
				ChatGPTFabricMod.LOGGER.error("Connection failed: {}", e.getMessage());
				return "❌ Connection failed. Please check your internet connection.";
			} catch (java.net.SocketTimeoutException e) {
				ChatGPTFabricMod.LOGGER.error("Request timeout: {}", e.getMessage());
				return "❌ Request timed out. Please try again.";
			} catch (InterruptedException e) {
				ChatGPTFabricMod.LOGGER.error("Request interrupted: {}", e.getMessage());
				Thread.currentThread().interrupt();
				return "❌ Request was interrupted.";
			} catch (Exception e) {
				ChatGPTFabricMod.LOGGER.error("Error communicating with OpenAI API", e);
				return "❌ An error occurred: " + e.getMessage();
			}
		});
	}

	private static String handleResponse(HttpResponse<String> response) {
		int statusCode = response.statusCode();

		if (statusCode == 200) {
			try {
				JsonObject responseBody = GSON.fromJson(response.body(), JsonObject.class);
				String content = responseBody
					.getAsJsonArray("choices")
					.get(0)
					.getAsJsonObject()
					.getAsJsonObject("message")
					.get("content")
					.getAsString();

				// Truncate if too long (max 2000 chars for Minecraft chat)
				if (content.length() > 2000) {
					content = content.substring(0, 1997) + "...";
				}

				ChatGPTFabricMod.LOGGER.debug("ChatGPT response received: {} chars", content.length());
				return content;

			} catch (Exception e) {
				ChatGPTFabricMod.LOGGER.error("Failed to parse ChatGPT response", e);
				return "❌ Failed to parse response from ChatGPT.";
			}
		} else if (statusCode == 401) {
			ChatGPTFabricMod.LOGGER.error("OpenAI authentication failed (401)");
			return "❌ Authentication failed. Please check your API key.";
		} else if (statusCode == 429) {
			ChatGPTFabricMod.LOGGER.warn("Rate limited by OpenAI API (429)");
			return "❌ Rate limited. Please wait before trying again.";
		} else if (statusCode == 500 || statusCode == 502 || statusCode == 503) {
			ChatGPTFabricMod.LOGGER.error("OpenAI server error ({})", statusCode);
			return "❌ OpenAI server is experiencing issues. Please try again later.";
		} else {
			ChatGPTFabricMod.LOGGER.error("Unexpected status code from OpenAI: {}", statusCode);
			try {
				JsonObject error = GSON.fromJson(response.body(), JsonObject.class);
				String errorMessage = error.getAsJsonObject("error").get("message").getAsString();
				return "❌ Error: " + errorMessage;
			} catch (Exception e) {
				return "❌ Error: HTTP " + statusCode;
			}
		}
	}
}
