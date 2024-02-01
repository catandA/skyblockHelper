package com.catand.skyblockhelper.data;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class HOTMLevelInfo {
	private final int level;
	private final int xpRequired;
	private final int cumulativeXp;

	public static final List<HOTMLevelInfo> LEVELS = new ArrayList<>();

	static {
		LEVELS.add(new HOTMLevelInfo(1, 0, 0));
		LEVELS.add(new HOTMLevelInfo(2, 3000, 50));
		LEVELS.add(new HOTMLevelInfo(3, 9000, 175));
		LEVELS.add(new HOTMLevelInfo(4, 25000, 375));
		LEVELS.add(new HOTMLevelInfo(5, 60000, 675));
		LEVELS.add(new HOTMLevelInfo(6, 10000, 1925));
		LEVELS.add(new HOTMLevelInfo(7, 150000, 2925));
	}

	public HOTMLevelInfo(int level, int xpRequired, int cumulativeXp) {
		this.level = level;
		this.xpRequired = xpRequired;
		this.cumulativeXp = cumulativeXp;
	}

	public static HOTMLevelInfo getCurrentLevel(int totalXp) {
		for (int i = LEVELS.size() - 1; i >= 0; i--) {
			if (totalXp >= LEVELS.get(i).getCumulativeXp()) {
				return LEVELS.get(i);
			}
		}
		return null;
	}

	public static int getCurrentLevelXp(int totalXp) {
		HOTMLevelInfo currentLevel = getCurrentLevel(totalXp);
		return totalXp - currentLevel.getCumulativeXp();
	}

	public static int getRemainingXpToNextLevel(int totalXp) {
		HOTMLevelInfo currentLevel = getCurrentLevel(totalXp);
		if (currentLevel.getLevel() == LEVELS.size() - 1) {
			return 0;
		}
		HOTMLevelInfo nextLevel = LEVELS.get(currentLevel.getLevel() + 1);
		return nextLevel.getCumulativeXp() - totalXp;
	}

	public static double getPercentageToNextLevel(int totalXp) {
		HOTMLevelInfo currentLevel = getCurrentLevel(totalXp);
		if (currentLevel.getLevel() == LEVELS.size() - 1) {
			return 100.0;
		}
		int remainingXp = getRemainingXpToNextLevel(totalXp);
		int xpToNextLevel = currentLevel.getXpRequired();
		return ((double) (xpToNextLevel - remainingXp) / xpToNextLevel) * 100;
	}
}