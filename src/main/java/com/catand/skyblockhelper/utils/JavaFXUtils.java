package com.catand.skyblockhelper.utils;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class JavaFXUtils {
	public static Color AWTColorToJavaFXColor(java.awt.Color awtColor) {
		return Color.rgb(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue(), awtColor.getAlpha() / 255.0);
	}

	public static Scene createScene(String fxmlPath, int width, int height) {
		try {
			Parent root = FXMLLoader.load(JavaFXUtils.class.getResource(fxmlPath));
			return new Scene(root, width, height);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 创建一个带随机背景的Scene,这个Scene内要有一个id为Background的HBox
	 */
	public static Scene createSceneWithBackground(String fxmlPath, int width, int height, int backgroundWidth, int backgroundHeight) {
		Scene scene = createScene(fxmlPath, width, height);

		// 设置背景
		Image background = ImageUtil.getImageBackground(backgroundWidth, backgroundHeight);
		BackgroundImage backgroundImage = new BackgroundImage(background, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
		HBox hBox = (HBox) scene.lookup("#Background");
		hBox.setBackground(new Background(backgroundImage));
		return scene;
	}

	public static CompletableFuture<Scene> createSceneAsync(String fxmlPath, int width, int height) {
		return supplyAsync(() -> createScene(fxmlPath, width, height));
	}

	public static CompletableFuture<Scene> createSceneWithBackgroundAsync(String fxmlPath, int width, int height, int backgroundWidth, int backgroundHeight) {
		return supplyAsync(() -> createSceneWithBackground(fxmlPath, width, height, backgroundWidth, backgroundHeight));
	}

	public static CompletableFuture<Scene> runJavaFXAsync(Supplier<Scene> supplier) {
		return CompletableFuture.supplyAsync(() -> {
			final CompletableFuture<Scene> innerFuture = new CompletableFuture<>();
			Platform.runLater(() -> {
				try {
					Scene scene = supplier.get();
					innerFuture.complete(scene);
				} catch (Exception e) {
					innerFuture.completeExceptionally(e);
				}
			});
			return innerFuture;
		}).thenCompose(Function.identity());
	}
}
