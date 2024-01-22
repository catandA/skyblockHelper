package com.catand.skyblockhelper.utils;

import javafx.scene.paint.Color;

public class JavaFXUtils {
	public static Color AWTColorToJavaFXColor(java.awt.Color awtColor) {
		return Color.rgb(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue(), awtColor.getAlpha() / 255.0);
	}
}
