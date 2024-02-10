package com.catand.skyblockhelper.utils;

import com.alibaba.fastjson2.JSONArray;
import com.catand.skyblockhelper.SpringConfig;
import com.google.gson.Gson;
import lombok.Getter;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.http.HypixelHttpClient;
import net.hypixel.api.reactor.ReactorHttpClient;
import net.hypixel.api.reply.PlayerReply;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class HypixelAPIUtil {
	@Getter
	private static final HypixelAPI hypixelAPI = createHypixelAPI();

	private HypixelAPIUtil() {
	}

	private static HypixelAPI createHypixelAPI() {
		ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
		SpringConfig config = context.getBean(SpringConfig.class);
		HypixelHttpClient client = new ReactorHttpClient(UUID.fromString(config.getHypixelApiKey()));
		return new HypixelAPI(client);
	}
	public static CompletableFuture<JSONArray> fetchSkyBlockProfilesData(UUID uuid) {
		HypixelAPI hypixelAPI = HypixelAPIUtil.getHypixelAPI();
		return hypixelAPI.getSkyBlockProfiles(uuid).thenApplyAsync(
				resourceReply -> {
					Gson gson = new Gson();
					String jsonString = gson.toJson(resourceReply.getProfiles());
					return JSONArray.parseArray(jsonString);
				}
		);
	}

	public static CompletableFuture<PlayerReply.Player> fetchPlayersData(UUID uuid) {
		HypixelAPI hypixelAPI = HypixelAPIUtil.getHypixelAPI();
		return hypixelAPI.getPlayerByUuid(uuid).thenApplyAsync(
				PlayerReply::getPlayer
		);
	}
}
