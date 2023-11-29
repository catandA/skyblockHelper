package com.catand.skyblockhelper;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.catand.skyblockhelper.utils.SkyCryptAPIGetUtil;

import java.util.ArrayList;

public class Player {
    public String name;
    public ArrayList<JSONObject> profileList;

    public Player(String name) {
        this.name = name;
    }

    public void refreshProfileList() {
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
}
