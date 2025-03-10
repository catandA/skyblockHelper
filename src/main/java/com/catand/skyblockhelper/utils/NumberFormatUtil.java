package com.catand.skyblockhelper.utils;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class NumberFormatUtil {
	private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

	static {
		suffixes.put(1_000L, "k");
		suffixes.put(1_000_000L, "M");
		suffixes.put(1_000_000_000L, "B");
		suffixes.put(1_000_000_000_000L, "T");
	}

	public static String format(double value) {
		return format((long) value);
	}

	public static String format(int value) {
		return format((long) value);
	}

	public static String format(long value) {
		//Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
		if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
		if (value < 0) return "-" + format(-value);
		if (value < 1000) return Long.toString(value); //deal with easy case

		Map.Entry<Long, String> e = suffixes.floorEntry(value);
		Long divideBy = e.getKey();
		String suffix = e.getValue();

		long truncated = value / (divideBy / 10); //the number part of the output times 10
		boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
		return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
	}

	public static String format(int value, int decimalPlaces) {
		if (value < 1000) return Integer.toString(value); //deal with easy case

		Map.Entry<Long, String> e = suffixes.floorEntry((long) value);
		Long divideBy = e.getKey();
		String suffix = e.getValue();

		double truncated = (double) value / ((double) divideBy / 10); //the number part of the output times 10
		String formatString = "%." + decimalPlaces + "f%s";
		return String.format(formatString, truncated / 10, suffix);
	}
}
