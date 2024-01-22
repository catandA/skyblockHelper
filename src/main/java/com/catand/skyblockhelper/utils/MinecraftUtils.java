package com.catand.skyblockhelper.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MinecraftUtils {
	public static BufferedImage getBufferedImageSkin(String uuid) throws IOException {
		String url = "https://crafatar.com/renders/body/" + uuid + "?overlay";
		InputStream input = new ByteArrayInputStream(HttpUtils.httpGetBytes(url));
		return ImageIO.read(input);
	}
}
