package com.catand.skyblockhelper;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;

@Getter
	public enum Gamemode {
		NORMAL("normal", "", "默认", ""),
		IRONMAN("ironman", "ironman", "铁人", "♻"),
		STRANDED("island", "stranded", "搁浅", "☀"),
		BINGO("bingo", "bingo", "宾果", "Ⓑ");
		private final String name;
		private final String jsonName;
		private final String chineseName;
		private final String icon;

		Gamemode(String name, String jsonName, String chineseName, String icon) {
			this.name = name;
			this.jsonName = jsonName;
			this.chineseName = chineseName;
			this.icon = icon;
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