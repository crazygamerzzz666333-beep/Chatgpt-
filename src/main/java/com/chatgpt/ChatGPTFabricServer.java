package com.chatgpt;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import com.chatgpt.command.GPTCommand;
import com.chatgpt.command.GPTReloadCommand;
import com.chatgpt.config.ChatGPTConfig;

public class ChatGPTFabricServer implements DedicatedServerModInitializer {
	@Override
	public void onServerInitialize() {
		ChatGPTFabricMod.LOGGER.info("ChatGPT Fabric server module initializing...");
		
		// Initialize config
		ChatGPTConfig.loadConfig();
		
		// Register commands
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			GPTCommand.register(dispatcher);
			GPTReloadCommand.register(dispatcher);
		});
		
		ChatGPTFabricMod.LOGGER.info("ChatGPT Fabric ready! Use /gpt <question> to chat with ChatGPT");
	}
}
