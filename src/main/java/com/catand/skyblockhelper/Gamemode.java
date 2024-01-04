package com.catand.skyblockhelper;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;

import java.awt.*;

@Getter
public enum Gamemode {
	NORMAL("normal", "", "默认", "", MinecraftColorCode.BLACK.getColor()),
	IRONMAN("ironman", "ironman", "铁人", "♻", MinecraftColorCode.GRAY.getColor()),
	STRANDED("island", "stranded", "搁浅", "☀", MinecraftColorCode.GREEN.getColor()),
	BINGO("bingo", "bingo", "宾果", "Ⓑ", MinecraftColorCode.GOLD.getColor());
	private final String name;
	private final String jsonName;
	private final String chineseName;
	private final String icon;
	private final Color color;

	Gamemode(String name, String jsonName, String chineseName, String icon, Color color) {
		this.name = name;
		this.jsonName = jsonName;
		this.chineseName = chineseName;
		this.icon = icon;
		this.color = color;
	}

	public static Gamemode getGamemode(String name) {
		return switch (name) {
			case "normal" -> Gamemode.NORMAL;
			case "ironman" -> Gamemode.IRONMAN;
			case "stranded" -> Gamemode.STRANDED;
			case "bingo" -> Gamemode.BINGO;
			default -> Gamemode.NORMAL;
		};
	}

	public static Gamemode getGamemode(JSONObject profile) {
		if (!profile.containsKey("game_mode")) {
			return Gamemode.NORMAL;
		}
		return switch (profile.getString("game_mode")) {
			case "normal" -> Gamemode.NORMAL;
			case "ironman" -> Gamemode.IRONMAN;
			case "stranded" -> Gamemode.STRANDED;
			case "bingo" -> Gamemode.BINGO;
			default -> Gamemode.NORMAL;
		};
	}
}