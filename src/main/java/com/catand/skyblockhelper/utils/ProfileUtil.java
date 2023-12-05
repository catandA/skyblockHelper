package com.catand.skyblockhelper.utils;

import com.alibaba.fastjson2.JSONObject;

public class ProfileUtil {
	public static JSONObject getNetworthData(JSONObject profile) {
		return profile.getJSONObject("data").getJSONObject("networth");
	}
	public static JSONObject getDungeonData(JSONObject profile) {
		return profile.getJSONObject("data").getJSONObject("dungeons");
	}

	public static JSONObject get_levels_Data(JSONObject profile) {
		return profile.getJSONObject("data").getJSONObject("levels");
	}

	public static double get_average_level_Data(JSONObject profile) {
		return profile.getJSONObject("data").getDoubleValue("average_level");
	}

	public static long get_average_level_rank_Data(JSONObject profile) {
		return profile.getJSONObject("data").getLongValue("average_level_rank");
	}

	public static double get_total_skill_xp_Data(JSONObject profile) {
		return profile.getJSONObject("data").getDoubleValue("total_skill_xp");
	}

	public static String getProfileName(JSONObject profile) {
		return profile.getString("cute_name");
	}

	public static gamemode getGamemode(JSONObject profile) {
		return gamemode.NORMAL;
	}

	public enum gamemode {
		NORMAL,
		IRONMAN
	}
}
