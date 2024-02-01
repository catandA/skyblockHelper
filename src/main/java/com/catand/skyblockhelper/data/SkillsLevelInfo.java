package com.catand.skyblockhelper.data;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SkillsLevelInfo {
	private final int level;
	private final int xpRequired;
	private final int cumulativeXp;

	public static final List<SkillsLevelInfo> LEVELS = new ArrayList<>();

	static {
		LEVELS.add(new SkillsLevelInfo(0, 0, 0));
		LEVELS.add(new SkillsLevelInfo(1, 50, 50));
		LEVELS.add(new SkillsLevelInfo(2, 125, 175));
		LEVELS.add(new SkillsLevelInfo(3, 200, 375));
		LEVELS.add(new SkillsLevelInfo(4, 300, 675));
		LEVELS.add(new SkillsLevelInfo(5, 500, 1175));
		LEVELS.add(new SkillsLevelInfo(6, 750, 1925));
		LEVELS.add(new SkillsLevelInfo(7, 1000, 2925));
		LEVELS.add(new SkillsLevelInfo(8, 1500, 4425));
		LEVELS.add(new SkillsLevelInfo(9, 2000, 6425));
		LEVELS.add(new SkillsLevelInfo(10, 3500, 9925));
		LEVELS.add(new SkillsLevelInfo(11, 5000, 14925));
		LEVELS.add(new SkillsLevelInfo(12, 7500, 22425));
		LEVELS.add(new SkillsLevelInfo(13, 10000, 32425));
		LEVELS.add(new SkillsLevelInfo(14, 15000, 47425));
		LEVELS.add(new SkillsLevelInfo(15, 20000, 67425));
		LEVELS.add(new SkillsLevelInfo(16, 30000, 97425));
		LEVELS.add(new SkillsLevelInfo(17, 50000, 147425));
		LEVELS.add(new SkillsLevelInfo(18, 75000, 222425));
		LEVELS.add(new SkillsLevelInfo(19, 100000, 322425));
		LEVELS.add(new SkillsLevelInfo(20, 200000, 522425));
		LEVELS.add(new SkillsLevelInfo(21, 300000, 822425));
		LEVELS.add(new SkillsLevelInfo(22, 400000, 1222425));
		LEVELS.add(new SkillsLevelInfo(23, 500000, 1722425));
		LEVELS.add(new SkillsLevelInfo(24, 600000, 2322425));
		LEVELS.add(new SkillsLevelInfo(25, 700000, 3022425));
		LEVELS.add(new SkillsLevelInfo(26, 800000, 3822425));
		LEVELS.add(new SkillsLevelInfo(27, 900000, 4722425));
		LEVELS.add(new SkillsLevelInfo(28, 1000000, 5722425));
		LEVELS.add(new SkillsLevelInfo(29, 1100000, 6822425));
		LEVELS.add(new SkillsLevelInfo(30, 1200000, 8022425));
		LEVELS.add(new SkillsLevelInfo(31, 1300000, 9322425));
		LEVELS.add(new SkillsLevelInfo(32, 1400000, 10722425));
		LEVELS.add(new SkillsLevelInfo(33, 1500000, 12222425));
		LEVELS.add(new SkillsLevelInfo(34, 1600000, 13822425));
		LEVELS.add(new SkillsLevelInfo(35, 1700000, 15522425));
		LEVELS.add(new SkillsLevelInfo(36, 1800000, 17322425));
		LEVELS.add(new SkillsLevelInfo(37, 1900000, 19222425));
		LEVELS.add(new SkillsLevelInfo(38, 2000000, 21222425));
		LEVELS.add(new SkillsLevelInfo(39, 2100000, 23322425));
		LEVELS.add(new SkillsLevelInfo(40, 2200000, 25522425));
		LEVELS.add(new SkillsLevelInfo(41, 2300000, 27822425));
		LEVELS.add(new SkillsLevelInfo(42, 2400000, 30222425));
		LEVELS.add(new SkillsLevelInfo(43, 2500000, 32722425));
		LEVELS.add(new SkillsLevelInfo(44, 2600000, 35322425));
		LEVELS.add(new SkillsLevelInfo(45, 2750000, 38072425));
		LEVELS.add(new SkillsLevelInfo(46, 2900000, 40972425));
		LEVELS.add(new SkillsLevelInfo(47, 3100000, 44072425));
		LEVELS.add(new SkillsLevelInfo(48, 3400000, 47472425));
		LEVELS.add(new SkillsLevelInfo(49, 3700000, 51172425));
		LEVELS.add(new SkillsLevelInfo(50, 4000000, 55172425));
		LEVELS.add(new SkillsLevelInfo(51, 4300000, 59472425));
		LEVELS.add(new SkillsLevelInfo(52, 4600000, 64072425));
		LEVELS.add(new SkillsLevelInfo(53, 4900000, 68972425));
		LEVELS.add(new SkillsLevelInfo(54, 5200000, 74172425));
		LEVELS.add(new SkillsLevelInfo(55, 5500000, 79672425));
		LEVELS.add(new SkillsLevelInfo(56, 5800000, 85472425));
		LEVELS.add(new SkillsLevelInfo(57, 6100000, 91572425));
		LEVELS.add(new SkillsLevelInfo(58, 6400000, 97972425));
		LEVELS.add(new SkillsLevelInfo(59, 6700000, 104672425));
		LEVELS.add(new SkillsLevelInfo(60, 7000000, 111672425));
	}

	public SkillsLevelInfo(int level, int xpRequired, int cumulativeXp) {
		this.level = level;
		this.xpRequired = xpRequired;
		this.cumulativeXp = cumulativeXp;
	}

	public static SkillsLevelInfo getCurrentLevel(int totalXp) {
		for (int i = LEVELS.size() - 1; i >= 0; i--) {
			if (totalXp >= LEVELS.get(i).getCumulativeXp()) {
				return LEVELS.get(i);
			}
		}
		return null;
	}

	public static int getCurrentLevelXp(int totalXp) {
		SkillsLevelInfo currentLevel = getCurrentLevel(totalXp);
		return totalXp - currentLevel.getCumulativeXp();
	}

	public static int getRemainingXpToNextLevel(int totalXp) {
		SkillsLevelInfo currentLevel = getCurrentLevel(totalXp);
		if (currentLevel.getLevel() == LEVELS.size() - 1) {
			return 0;
		}
		SkillsLevelInfo nextLevel = LEVELS.get(currentLevel.getLevel() + 1);
		return nextLevel.getCumulativeXp() - totalXp;
	}

	public static double getPercentageToNextLevel(int totalXp) {
		SkillsLevelInfo currentLevel = getCurrentLevel(totalXp);
		if (currentLevel.getLevel() == LEVELS.size() - 1) {
			return 100.0;
		}
		int remainingXp = getRemainingXpToNextLevel(totalXp);
		int xpToNextLevel = currentLevel.getXpRequired();
		return ((double) (xpToNextLevel - remainingXp) / xpToNextLevel) * 100;
	}
}