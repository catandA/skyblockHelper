package com.catand.skyblockhelper;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SkyblockHelperApplication extends Application {
	public static String VERSION = SkyblockHelperApplication.class.getPackage().getImplementationVersion();

	public static void main(String[] args) {
		SpringApplication.run(SkyblockHelperApplication.class, args);
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		Platform.runLater(() -> {
			// Your JavaFX code here
		});
	}
}
