package com.catand.skyblockhelper.data;

import lombok.Getter;

import static com.catand.skyblockhelper.utils.LevenshteinDistanceUtil.getLevenshteinDistance;
import static com.catand.skyblockhelper.utils.LevenshteinDistanceUtil.getMinLevenshteinDistance;

@Getter
public enum SkyblockProfile {
	APPLE("Apple", new String[]{"苹果"}),
	BANANA("Banana", new String[]{"香蕉"}),
	BLUEBERRY("Blueberry", new String[]{"蓝莓"}),
	COCONUT("Coconut", new String[]{"椰子"}),
	CUCUMBER("Cucumber", new String[]{"黄瓜"}),
	GRAPES("Grapes", new String[]{"葡萄"}),
	KIWI("Kiwi", new String[]{"猕猴桃"}),
	LEMON("Lemon", new String[]{"柠檬"}),
	LIME("Lime", new String[]{"酸橙", "青柠"}),
	MANGO("Mango", new String[]{"芒果"}),
	ORANGE("Orange", new String[]{"橙子"}),
	PAPAYA("Papaya", new String[]{"木瓜"}),
	PEAR("Pear", new String[]{"梨", "梨子"}),
	PEACH("Peach", new String[]{"桃","桃子"}),
	PINEAPPLE("Pineapple", new String[]{"菠萝"}),
	POMEGRANATE("Pomegranate", new String[]{"石榴"}),
	RASPBERRY("Raspberry", new String[]{"树莓", "覆盆子"}),
	STRAWBERRY("Strawberry", new String[]{"草莓"}),
	TOMATO("Tomato", new String[]{"番茄", "西红柿"}),
	WATERMELON("Watermelon", new String[]{"西瓜"}),
	ZUCCHINI("Zucchini", new String[]{"西葫芦"});
	private final String jsonName;
	private final String[] chineseName;

	SkyblockProfile(String jsonName, String[] chineseName) {
		this.jsonName = jsonName;
		this.chineseName = chineseName;
	}
	public static SkyblockProfile getProfile(String name) {
		SkyblockProfile closestProfile = null;
		int closestDistance = Integer.MAX_VALUE;

		for (SkyblockProfile profile : SkyblockProfile.values()) {
			int distance = Math.min(getLevenshteinDistance(name, profile.getJsonName()),
					getMinLevenshteinDistance(name, profile.getChineseName()));
			if (distance < closestDistance) {
				closestDistance = distance;
				closestProfile = profile;
			}
		}

		return closestProfile;
	}
}
