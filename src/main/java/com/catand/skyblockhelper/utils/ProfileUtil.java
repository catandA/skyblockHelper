package com.catand.skyblockhelper.utils;

import com.alibaba.fastjson2.JSONObject;

public class ProfileUtil {
	public static JSONObject getNetworthData(JSONObject profile) {
		return profile.getJSONObject("data").getJSONObject("networth");
	}
	public static JSONObject getDungeonData(JSONObject profile) {
		return profile.getJSONObject("data").getJSONObject("dungeons");
	}

	public static JSONObject get_skills_Data(JSONObject profile) {
		return profile.getJSONObject("data").getJSONObject("skills");
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
