package com.catand.skyblockhelper.data;

import lombok.Getter;

import java.awt.*;

@Getter
public enum MinecraftColorCode {
	BLACK("§0", "black", Color.BLACK),
	DARK_BLUE("§1", "dark_blue", new Color(0, 0, 170)),
	DARK_GREEN("§2", "dark_green", new Color(0, 170, 0)),
	DARK_AQUA("§3", "dark_aqua", new Color(0, 170, 170)),
	DARK_RED("§4", "dark_red", new Color(170, 0, 0)),
	DARK_PURPLE("§5", "dark_purple", new Color(170, 0, 170)),
	GOLD("§6", "gold", new Color(255, 170, 0)),
	GRAY("§7", "gray", new Color(170, 170, 170)),
	DARK_GRAY("§8", "dark_gray", new Color(85, 85, 85)),
	BLUE("§9", "blue", new Color(85, 85, 255)),
	GREEN("§a", "green", new Color(85, 255, 85)),
	AQUA("§b", "aqua", new Color(85, 255, 255)),
	RED("§c", "red", new Color(255, 85, 85)),
	LIGHT_PURPLE("§d", "light_purple", new Color(255, 85, 255)),
	YELLOW("§e", "yellow", new Color(255, 255, 85)),
	WHITE("§f", "white", new Color(255, 255, 255));
	private final String code;
	private final String jsonName;
	private final Color color;

	MinecraftColorCode(String code, String jsonName, Color color) {
		this.code = code;
		this.jsonName = jsonName;
		this.color = color;
	}
}
