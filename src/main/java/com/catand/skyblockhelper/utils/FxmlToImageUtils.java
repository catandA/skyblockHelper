package com.catand.skyblockhelper.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FxmlToImageUtils {
	public static void fromFxml(String fxmlPath) {
		try {
			// 加载FXML文件
			Parent root = FXMLLoader.load(FxmlToImageUtils.class.getResource(fxmlPath));

			// 创建Scene
			Scene scene = new Scene(root, 800, 600);

			// 创建一个新的Stage
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.show();

			// 获取WritableImage
			WritableImage writableImage = scene.snapshot(null);

			// 将WritableImage转换为BufferedImage
			BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

			// 将BufferedImage保存为文件
			ImageIO.write(bufferedImage, "png", new File("output.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}