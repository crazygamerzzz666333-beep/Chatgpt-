package com.chatgpt.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import com.chatgpt.ChatGPTFabricMod;
import com.chatgpt.api.OpenAIClient;
import com.chatgpt.config.ChatGPTConfig;

import java.util.HashMap;
import java.util.Map;

public class GPTCommand {
	private static final Map<String, Long> COOLDOWNS = new HashMap<>();
	private static final long COOLDOWN_SECONDS = 5;

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("gpt")
				.requires(source -> source.hasPermissionLevel(0))
				.then(
					CommandManager.argument("question", StringArgumentType.greedyString())
						.executes(context -> {
							ServerCommandSource source = context.getSource();
							String question = StringArgumentType.getString(context, "question");
							return executeGPT(source, question);
						})
				)
		);
	}

	private static int executeGPT(ServerCommandSource source, String question) {
		String playerName = source.getName();
		long currentTime = System.currentTimeMillis() / 1000;

		// Check cooldown
		if (COOLDOWNS.containsKey(playerName)) {
			long lastUsed = COOLDOWNS.get(playerName);
			long timeLeft = COOLDOWN_SECONDS - (currentTime - lastUsed);

			if (timeLeft > 0) {
				source.sendError(Text.literal("❌ Please wait " + timeLeft + " second(s) before using /gpt again."));
				return 0;
			}
		}

		// Check if API key is set
		if (!ChatGPTConfig.isApiKeyValid()) {
			source.sendError(Text.literal("❌ API key missing. Edit config/chatgptfabric/config.json and add your OpenAI API key."));
			return 0;
		}

		// Update cooldown
		COOLDOWNS.put(playerName, currentTime);

		// Send thinking message
		source.sendFeedback(() -> Text.literal("🤔 ChatGPT is thinking..."), false);

		// Send async request
		OpenAIClient.askChatGPT(question).thenAccept(response -> {
			try {
				// Split long responses into multiple messages
				String[] lines = response.split("\n");
				for (String line : lines) {
					if (line.length() > 256) {
						// Split very long lines
						String[] chunks = splitString(line, 256);
						for (String chunk : chunks) {
							if (!chunk.isEmpty()) {
								source.sendFeedback(() -> formatResponse(chunk), false);
							}
						}
					} else if (!line.isEmpty()) {
						source.sendFeedback(() -> formatResponse(line), false);
					}
				}
			} catch (Exception e) {
				ChatGPTFabricMod.LOGGER.error("Error displaying ChatGPT response", e);
				source.sendError(Text.literal("❌ Error displaying response."));
			}
		}).exceptionally(throwable -> {
			ChatGPTFabricMod.LOGGER.error("Error in ChatGPT command", throwable);
			source.sendError(Text.literal("❌ An error occurred while contacting ChatGPT."));
			return null;
		});

		return 1;
	}

	private static MutableText formatResponse(String text) {
		return Text.literal("🤖 ").append(Text.literal(text));
	}

	private static String[] splitString(String text, int chunkSize) {
		int chunks = (int) Math.ceil((double) text.length() / chunkSize);
		String[] result = new String[chunks];

		for (int i = 0; i < chunks; i++) {
			int start = i * chunkSize;
			int end = Math.min(start + chunkSize, text.length());
			result[i] = text.substring(start, end);
		}

		return result;
	}
}
