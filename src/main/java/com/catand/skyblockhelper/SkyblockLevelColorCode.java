package com.catand.skyblockhelper;

import lombok.Getter;

import java.awt.*;

@Getter
public enum SkyblockLevelColorCode {
	GRAY("§7", "gray", new Color(170, 170, 170), 0),
	WHITE("§f", "white", new Color(255, 255, 255), 40),
	YELLOW("§e", "yellow", new Color(255, 255, 85), 80),
	GREEN("§a", "green", new Color(85, 255, 85), 120),
	DARK_GREEN("§2", "dark_green", new Color(0, 170, 0), 160),
	AQUA("§b", "aqua", new Color(85, 255, 255), 200),
	CYAN("§3", "dark_aqua", new Color(0, 170, 170), 240),
	BLUE("§9", "blue", new Color(85, 85, 255), 280),
	PINK("§d", "light_purple", new Color(255, 85, 255), 320),
	PURPLE("§5", "dark_purple", new Color(170, 0, 170), 360),
	GOLD("§6", "gold", new Color(255, 170, 0), 400),
	RED("§c", "red", new Color(255, 85, 85), 440),
	DARK_RED("§4", "dark_red", new Color(170, 0, 0), 480);
	private final String code;
	private final String jsonName;
	private final Color color;
	private final int level;

	SkyblockLevelColorCode(String code, String jsonName, Color color, int level) {
		this.code = code;
		this.jsonName = jsonName;
		this.color = color;
		this.level = level;
	}
	public static SkyblockLevelColorCode getLevelColor(int level) {
		SkyblockLevelColorCode result = SkyblockLevelColorCode.GRAY;
		for (SkyblockLevelColorCode colorCode : SkyblockLevelColorCode.values()) {
			if (colorCode.getLevel() < level && colorCode.getLevel() >= result.getLevel()) {
				result = colorCode;
			}
		}
		return result;
	}
}
