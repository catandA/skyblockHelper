package com.catand.skyblockhelper.exception;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.catand.skyblockhelper.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class NoSuchProfileException extends RuntimeException{
	JSONArray profiles;
	String profileName;
	String playerName;
	UUID uuid;
}
