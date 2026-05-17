package com.chatgpt.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import com.chatgpt.config.ChatGPTConfig;

public class GPTReloadCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("gptreload")
				.requires(source -> source.hasPermissionLevel(3))
				.executes(context -> {
					ServerCommandSource source = context.getSource();
					return executeReload(source);
				})
		);
	}

	private static int executeReload(ServerCommandSource source) {
		try {
			ChatGPTConfig.reloadConfig();
			source.sendFeedback(() -> Text.literal("✅ ChatGPT Fabric config reloaded successfully!"), true);
			return 1;
		} catch (Exception e) {
			source.sendError(Text.literal("❌ Failed to reload config: " + e.getMessage()));
			return 0;
		}
	}
}
