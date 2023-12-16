package com.catand.skyblockhelper.utils;

import com.alibaba.fastjson2.JSONObject;
import com.catand.skyblockhelper.Gamemode;

public class ProfileUtil {
	public static JSONObject getNetworthData(JSONObject profile) {
		return profile.getJSONObject("data").getJSONObject("networth");
	}

	public static JSONObject getDungeonData(JSONObject profile) {
		return profile.getJSONObject("data").getJSONObject("dungeons");
	}

	public static String getDisplayNameData(JSONObject profile) {
		return profile.getJSONObject("data").getString("display_name");
	}

	public static JSONObject getSkillsData(JSONObject profile) {
		return profile.getJSONObject("data").getJSONObject("skills");
	}

	public static JSONObject getAccessoriesData(JSONObject profile) {
		return profile.getJSONObject("data").getJSONObject("accessories");
	}

	public static JSONObject getCrimsonIsleData(JSONObject profile) {
		return profile.getJSONObject("data").getJSONObject("crimson_isle");
	}

	public static JSONObject getSlayerData(JSONObject profile) {
		return profile.getJSONObject("data").getJSONObject("slayer");
	}

	public static JSONObject getMiningData(JSONObject profile) {
		return profile.getJSONObject("data").getJSONObject("mining");
	}

	public static JSONObject getProfileData(JSONObject profile) {
		return profile.getJSONObject("data").getJSONObject("profile");
	}

	public static JSONObject getProfilesData(JSONObject profile) {
		JSONObject profiles = profile.getJSONObject("data").getJSONObject("profiles");
		JSONObject result = new JSONObject();
		for (String key : profiles.keySet()) {
			result.put(key, profiles.getJSONObject(key));
		}
		return result;
	}

	public static String getProfileName(JSONObject profile) {
		return profile.getString("cute_name");
	}

	public static Gamemode getGamemode(JSONObject profile) {
		return Gamemode.getGamemode(profile);
	}
}
