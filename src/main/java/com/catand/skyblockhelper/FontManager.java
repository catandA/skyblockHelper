package com.catand.skyblockhelper;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FontManager {
	public enum FontType {
		NOTO_SANS_SC_BOLD("/fonts/NotoSansSC-Bold.ttf");

		private final String fileName;

		FontType(String fileName) {
			this.fileName = fileName;
		}

		public String getFileName() {
			return fileName;
		}
	}

	private static FontManager instance;
	private Map<String, Font> fonts;

	private FontManager() {
		fonts = new HashMap<>();
		loadFonts();
	}

	public static FontManager getInstance() {
		if (instance == null) {
			instance = new FontManager();
		}
		return instance;
	}

	private void loadFonts() {
		try {
			for (FontType fontType : FontType.values()) {
				InputStream is = getClass().getResourceAsStream(fontType.getFileName());
				if (is != null) {
					Font font = Font.createFont(Font.TRUETYPE_FONT, is);
					fonts.put(fontType.name(), font);
				}
			}
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
	}

	public Font getFont(FontType fontType, float size) {
		String name = fontType.toString();
		if (fonts.containsKey(name)) {
			return fonts.get(name).deriveFont(size);
		} else {
			return null;
		}
	}
}