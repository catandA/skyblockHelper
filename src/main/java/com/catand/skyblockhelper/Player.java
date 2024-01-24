package com.catand.skyblockhelper;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.catand.skyblockhelper.utils.SkyCryptAPIGetUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class Player {
	@Getter
	@Setter
	private String name;
	@Getter
	private ArrayList<JSONObject> profileList;

	public Player(String name) {
		this.name = name;
		refreshProfileList();
	}

	private void refreshProfileList() {
		profileList = new ArrayList<>();
		//从API获取profileList
		String rawProfileData = SkyCryptAPIGetUtil.getDataByTags(name, SkyCryptAPIGetUtil.Tags.PROFILE);
		//序列化保存profileList
		JSONObject jsonObject = JSON.parseObject(rawProfileData);
		jsonObject.getJSONObject("profiles").forEach((k, v) -> profileList.add((JSONObject) v));
		//将主要profile放在第一位
		JSONObject mainProfile = new JSONObject();
		for (JSONObject profile : profileList) {
			if (profile.getBoolean("current")) {
				mainProfile = profile;
			}
		}
		profileList.remove(mainProfile);
		profileList.add(0, mainProfile);
	}

	public JSONObject getMainProfile() {
		return profileList.get(0);
	}

	public JSONObject getProfile(String jsonName) {
		for (JSONObject profile : profileList) {
			if (profile.getString("cute_name").equals(jsonName)) {
				return profile;
			}
		}
		return null;
	}
}