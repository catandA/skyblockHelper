package com.catand.skyblockhelper.utils;

public class SkyCryptAPIGetUtil {
	public static final String SKY_CRYPT_API = "https://sky.shiiyu.moe/api/v2/";

	public static String getDataByTags(String player, Tags name) {
		return HttpUtils.httpGetString(SKY_CRYPT_API + name + player + "/");
	}

	public static String getDataByTags(String player, Tags name, String profile) {
		return HttpUtils.httpGetString(SKY_CRYPT_API + name + player + "/" + profile + "/");
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
