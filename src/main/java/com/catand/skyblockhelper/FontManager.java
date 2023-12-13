package com.catand.skyblockhelper;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FontManager {
    private static FontManager instance;
    private Map<String, Font> fonts;

    private FontManager() {
        fonts = new HashMap<>();
        try {
            Files.walk(Paths.get("src/main/resources/fonts"))
                    .filter(Files::isRegularFile)
                    .forEach(this::loadFont);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FontManager getInstance() {
        if (instance == null) {
            instance = new FontManager();
        }
        return instance;
    }

    private void loadFont(Path file) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, file.toFile());
            fonts.put(file.getFileName().toString(), font);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    public Font getFont(String name, float size) {
        if (fonts.containsKey(name)) {
            return fonts.get(name).deriveFont(size);
        } else {
            return null;
        }
    }
}