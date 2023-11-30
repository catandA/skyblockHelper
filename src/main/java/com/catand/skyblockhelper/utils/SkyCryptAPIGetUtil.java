package com.catand.skyblockhelper.utils;

import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public class SkyCryptAPIGetUtil {
	public static final String SKY_CRYPT_API = "https://sky.shiiyu.moe/api/v2/";

	public static String httpGet(String url) {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.exchange(url, HttpMethod.GET, null, String.class).getBody();
	}

	public static String httpPost(String url, String name) {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.postForEntity(url, name, String.class).getBody();
	}

	public static String getDataByTags(String player, Tags name) {
		return SkyCryptAPIGetUtil.httpGet(SKY_CRYPT_API + name + player + "/");
	}

	public static String getDataByTags(String player, Tags name, String profile) {
		return SkyCryptAPIGetUtil.httpGet(SKY_CRYPT_API + name + player + "/" + profile + "/");
	}

	public enum Tags {
		PROFILE,
		TALISMANS,
		SLAYERS,
		COINS,
		BAZAAR;

		@Override
		public String toString() {
			return switch (this) {
				case PROFILE -> "profile/";
				case TALISMANS -> "talismans/";
				case SLAYERS -> "slayers/";
				case COINS -> "coins/";
				case BAZAAR -> "bazaar/";
			};
		}
	}
}
