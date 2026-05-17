package com.chatgpt;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatGPTFabricMod implements ModInitializer {
	public static final String MOD_ID = "chatgptfabric";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("ChatGPT Fabric mod initializing...");
	}
}
