package com.catand.skyblockhelper.data;

import lombok.Getter;

import static com.catand.skyblockhelper.utils.LevenshteinDistanceUtil.getLevenshteinDistance;
import static com.catand.skyblockhelper.utils.LevenshteinDistanceUtil.getMinLevenshteinDistance;

@Getter
public enum SkyblockProfile {
	APPLE("Apple", new String[]{"苹果"}, new String[]{"\uD83C\uDF4F","\uD83C\uDF4E"}),
	BANANA("Banana", new String[]{"香蕉"}, new String[]{"\uD83C\uDF4C"}),
	BLUEBERRY("Blueberry", new String[]{"蓝莓"}, new String[]{"\uD83E\uDED0"}),
	COCONUT("Coconut", new String[]{"椰子"}, new String[]{"\uD83E\uDD65"}),
	CUCUMBER("Cucumber", new String[]{"黄瓜"}, new String[]{"\uD83E\uDD52"}),
	GRAPES("Grapes", new String[]{"葡萄"}, new String[]{"\uD83C\uDF47"}),
	KIWI("Kiwi", new String[]{"猕猴桃", "奇异果"}, new String[]{"\uD83E\uDD5D"}),
	LEMON("Lemon", new String[]{"柠檬"}, new String[]{"\uD83C\uDF4B"}),
	LIME("Lime", new String[]{"酸橙", "青柠"}, new String[]{"\uD83C\uDF4B\u200D\uD83D\uDFE9"}),
	MANGO("Mango", new String[]{"芒果"}, new String[]{"\uD83E\uDD6D"}),
	ORANGE("Orange", new String[]{"橙子", "橘子"}, new String[]{"\uD83C\uDF4A"}),
	PAPAYA("Papaya", new String[]{"木瓜"}, new String[]{}),
	PEAR("Pear", new String[]{"梨", "梨子"}, new String[]{"\uD83C\uDF50"}),
	PEACH("Peach", new String[]{"桃", "桃子"}, new String[]{"\uD83C\uDF51"}),
	PINEAPPLE("Pineapple", new String[]{"菠萝"}, new String[]{"\uD83C\uDF4D"}),
	POMEGRANATE("Pomegranate", new String[]{"石榴"}, new String[]{}),
	RASPBERRY("Raspberry", new String[]{"树莓", "覆盆子"}, new String[]{}),
	STRAWBERRY("Strawberry", new String[]{"草莓"}, new String[]{"\uD83C\uDF53"}),
	TOMATO("Tomato", new String[]{"番茄", "西红柿"}, new String[]{"\uD83C\uDF45"}),
	WATERMELON("Watermelon", new String[]{"西瓜"}, new String[]{"\uD83C\uDF49"}),
	ZUCCHINI("Zucchini", new String[]{"西葫芦"}, new String[]{});
	private final String jsonName;
	private final String[] chineseName;
	private final String[] emoji;

	SkyblockProfile(String jsonName, String[] chineseName, String[] emoji) {
		this.jsonName = jsonName;
		this.chineseName = chineseName;
		this.emoji = emoji;
	}

	public static SkyblockProfile getProfile(String name) {
		SkyblockProfile closestProfile = null;
		int closestDistance = Integer.MAX_VALUE;

		for (SkyblockProfile profile : SkyblockProfile.values()) {
			for (String emoji : profile.getEmoji()) {
				if (name.equals(emoji)) {
					return profile;
				}
			}
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
