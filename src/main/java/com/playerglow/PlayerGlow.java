package com.playerglow;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class PlayerGlow implements ClientModInitializer {

	public static final Set<UUID> GLOWING_PLAYERS = new HashSet<>();

	@Override
	public void onInitializeClient() {

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {

			// /glow <player>
			dispatcher.register(
					literal("glow")
							.then(argument("player", StringArgumentType.word())
									.executes(context -> {

										String playerName =
												StringArgumentType.getString(context, "player");

										MinecraftClient client =
												MinecraftClient.getInstance();

										if (client.world == null) {
											return 0;
										}

										PlayerEntity target = null;

										for (PlayerEntity player : client.world.getPlayers()) {
											if (player.getName().getString()
													.equalsIgnoreCase(playerName)) {
												target = player;
												break;
											}
										}

										if (target == null) {
											context.getSource().sendError(
													Text.literal(
															"Player not found: " + playerName
													)
											);
											return 0;
										}

										UUID uuid = target.getUuid();

										if (GLOWING_PLAYERS.contains(uuid)) {
											GLOWING_PLAYERS.remove(uuid);

											context.getSource().sendFeedback(
													Text.literal(
															"Removed glow from "
																	+ target.getName().getString()
													)
											);
										} else {
											GLOWING_PLAYERS.add(uuid);

											context.getSource().sendFeedback(
													Text.literal(
															"Glowing "
																	+ target.getName().getString()
													)
											);
										}

										return 1;
									})
							)
			);

			// /glow all
			dispatcher.register(
					literal("glow")
							.then(literal("all")
									.executes(context -> {

										MinecraftClient client =
												MinecraftClient.getInstance();

										if (client.world == null) {
											return 0;
										}

										int count = 0;

										for (PlayerEntity player :
												client.world.getPlayers()) {

											GLOWING_PLAYERS.add(player.getUuid());
											count++;
										}

										context.getSource().sendFeedback(
												Text.literal(
														"Glowing " + count + " players."
												)
										);

										return 1;
									})
							)
			);

			// /unglow
			dispatcher.register(
					literal("unglow")
							.executes(context -> {

								int count = GLOWING_PLAYERS.size();

								GLOWING_PLAYERS.clear();

								context.getSource().sendFeedback(
										Text.literal(
												"Removed glow from "
														+ count + " players."
										)
								);

								return 1;
							})
			);
		});
	}
}