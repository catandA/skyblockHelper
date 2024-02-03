package com.catand.skyblockhelper.exception;

import com.alibaba.fastjson2.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class NoProfilesException extends RuntimeException{
	JSONArray profiles;
	String playerName;
	UUID uuid;
}
