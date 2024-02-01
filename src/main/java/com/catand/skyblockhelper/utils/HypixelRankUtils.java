package com.catand.skyblockhelper.utils;

import com.catand.skyblockhelper.data.MinecraftColorCode;

public class HypixelRankUtils {
	public static MinecraftColorCode getRankColor(String rank) {
		return switch (rank) {
			// default rank
			case "VIP", "VIP_PLUS" -> MinecraftColorCode.GREEN;
			case "MVP", "MVP_PLUS" -> MinecraftColorCode.AQUA;
			case "SUPERSTAR" -> MinecraftColorCode.GOLD;
			case "YOUTUBER" -> MinecraftColorCode.WHITE;
			case "MODERATOR" -> MinecraftColorCode.DARK_GREEN;
			case "GAME_MASTER" -> MinecraftColorCode.DARK_GREEN;
			case "ADMIN" -> MinecraftColorCode.RED;
			// special rank
			case "§c[OWNER]" -> MinecraftColorCode.RED;
			case "§6[MOJANG]" -> MinecraftColorCode.GOLD;
			case "§6[EVENTS]" -> MinecraftColorCode.GOLD;
			case "§d[PIG§b+++§d]" -> MinecraftColorCode.LIGHT_PURPLE;
			default -> MinecraftColorCode.BLACK;
		};
	}

	public static MinecraftColorCode getBracketColor(String rank) {
		return switch (rank) {
			// default rank
			case "VIP", "VIP_PLUS" -> MinecraftColorCode.GREEN;
			case "MVP", "MVP_PLUS" -> MinecraftColorCode.AQUA;
			case "SUPERSTAR" -> MinecraftColorCode.GOLD;
			case "YOUTUBER" -> MinecraftColorCode.RED;
			case "MODERATOR" -> MinecraftColorCode.DARK_GREEN;
			case "GAME_MASTER" -> MinecraftColorCode.DARK_GREEN;
			case "ADMIN" -> MinecraftColorCode.RED;
			// special rank
			case "§c[OWNER]" -> MinecraftColorCode.RED;
			case "§6[MOJANG]" -> MinecraftColorCode.GOLD;
			case "§6[EVENTS]" -> MinecraftColorCode.GOLD;
			case "§d[PIG§b+++§d]" -> MinecraftColorCode.LIGHT_PURPLE;
			default -> MinecraftColorCode.BLACK;
		};
	}

	public static String getName(String rank) {
		return switch (rank) {
			// default rank
			case "VIP", "VIP_PLUS" -> "VIP";
			case "MVP", "MVP_PLUS", "SUPERSTAR" -> "MVP";
			case "YOUTUBER" -> "YOUTUBER";
			case "MODERATOR", "GAME_MASTER" -> "GM";
			case "ADMIN" -> "ADMIN";
			// special rank
			case "§c[OWNER]" -> "OWNER";
			case "§6[MOJANG]" -> "MOJANG";
			case "§6[EVENTS]" -> "EVENTS";
			case "§d[PIG§b+++§d]" -> "PIG";
			default -> throw new IllegalStateException("Unexpected value: " + rank);
		};
	}

	public static int getPlusNumber(String rank) {
		return switch (rank) {
			// default rank
			case "VIP" -> 0;
			case "VIP_PLUS", "MVP_PLUS" -> 1;
			case "SUPERSTAR" -> 2;
			// special rank
			case "§d[PIG§b+++§d]" -> 3;
			default -> 0;
		};
	}
}
