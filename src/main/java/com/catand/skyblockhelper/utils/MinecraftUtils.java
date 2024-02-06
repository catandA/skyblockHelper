package com.catand.skyblockhelper.utils;

import javafx.scene.image.Image;

import java.util.concurrent.CompletableFuture;

public class MinecraftUtils {
	public static Image getFXImageSkin(String uuid) {
		String url = "https://crafatar.com/renders/body/" + uuid + "?overlay";
		return new Image(url);
	}

	public static CompletableFuture<Image> getFXImageSkinAsync(String uuid) {
		return CompletableFuture.supplyAsync(() -> {
			String url = "https://crafatar.com/renders/body/" + uuid + "?overlay";
			return new Image(url);
		});
	}
}
