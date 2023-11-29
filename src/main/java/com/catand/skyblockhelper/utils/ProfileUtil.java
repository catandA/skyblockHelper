package com.catand.skyblockhelper.utils;

import com.alibaba.fastjson2.JSONObject;

public class ProfileUtil {
    public enum gamemode{
        NORMAL,
        IRONMAN
    }
    public static JSONObject getNetworthData(JSONObject profile) {
        return profile.getJSONObject("data").getJSONObject("networth");
    }
    public static String getProfileName(JSONObject profile) {
        return profile.getString("cute_name");
    }
    public static gamemode getGamemode(JSONObject profile) {
        return gamemode.NORMAL;
    }
}
