package com.catand.skyblockhelper;

import com.alibaba.fastjson2.JSONObject;
import com.catand.skyblockhelper.data.SkyblockProfile;
import com.catand.skyblockhelper.exception.NoSuchProfileException;
import com.catand.skyblockhelper.plugins.TrophyFishPlugin;
import com.catand.skyblockhelper.utils.ImageUtil;
import com.catand.skyblockhelper.utils.MinecraftUtils;
import com.catand.skyblockhelper.utils.ProfileUtil;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import lombok.Getter;
import lombok.Setter;
import me.kbrewster.mojangapi.MojangAPI;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Getter
public class SkycryptMessageHandler {
	private final MsgUtils sendMsg = MsgUtils.builder();
	private Player player;
	private JSONObject profile;
	private String profileName;
	// 输入的玩家名, 仅用于请求数据, 实际显示玩家名从profile中获取
	private final String playerName;
	@Setter
	private Scene scene;
	private Image skinImage;
	CompletableFuture<Void> future1;
	CompletableFuture<Void> future2;
	CompletableFuture<Void> future3;

	public SkycryptMessageHandler(String playerName) {
		this.playerName = playerName;
	}

	public SkycryptMessageHandler(String playerName, String profileName) {
		this.profileName = profileName;
		this.playerName = playerName;
	}

	public void handleGroupMessage() {
		future1 = CompletableFuture.runAsync(this::fetchPlayerData);
		future2 = CompletableFuture.runAsync(this::fetchSkinData);
		future3 = CompletableFuture.supplyAsync(() -> {
			final CompletableFuture<Void> innerFuture = new CompletableFuture<>();
			Platform.runLater(() -> {
				try {
					createScene();
					innerFuture.complete(null);
				} catch (Exception e) {
					innerFuture.completeExceptionally(e);
				}
			});
			return innerFuture;
		}).thenCompose(Function.identity());

		future1.exceptionally(this::handleException);
		future2.exceptionally(this::handleException);
		future3.exceptionally(this::handleException);

		future1.thenRun(() -> {
			if (profile == null) {
				throw new NoSuchProfileException(player, profileName);
			}
		});
	}

	private Void handleException(Throwable ex) {
		throw new RuntimeException(ex);
	}

	private void fetchPlayerData() {
		player = new Player(playerName);
		profile = player.getMainProfile();
		player.setName(ProfileUtil.getDisplayNameData(profile));

		if (profileName != null) {
			// 如果指定了存档名，那么就获取指定存档的数据
			profileName = SkyblockProfile.getProfile(profileName).getJsonName();
			profile = player.getProfile(profileName);
		} else {
			profileName = profile.getString("cute_name");
		}
	}

	private void fetchSkinData() {
		try {
			String uuid = String.valueOf(MojangAPI.getUUID(playerName));
			skinImage = ImageUtil.convertToFXImage(MinecraftUtils.getBufferedImageSkin(uuid));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void createScene() {
		try {
			Parent root = FXMLLoader.load(TrophyFishPlugin.class.getResource("/scene/TrophyFish.fxml"));
			scene = new Scene(root, 960, 570);

			// 设置背景
			Image background = ImageUtil.getImageBackground(960, 500);
			BackgroundImage backgroundImage = new BackgroundImage(background, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
			HBox hBox = (HBox) scene.lookup("#Background");
			hBox.setBackground(new Background(backgroundImage));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void sendAsImage(Bot bot, GroupMessageEvent event) {
		final WritableImage[] writableImage = new WritableImage[1];
		Platform.runLater(() -> {
			try {
				writableImage[0] = scene.snapshot(null);
				sendMsg.img(ImageUtil.ImageToBase64(SwingFXUtils.fromFXImage(writableImage[0], null)));
				bot.sendGroupMsg(event.getGroupId(), event.getUserId(), sendMsg.build(), false);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}
}