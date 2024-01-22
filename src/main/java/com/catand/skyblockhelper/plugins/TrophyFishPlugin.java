package com.catand.skyblockhelper.plugins;

import com.alibaba.fastjson2.JSONObject;
import com.catand.skyblockhelper.ErrorProcessor;
import com.catand.skyblockhelper.Player;
import com.catand.skyblockhelper.data.Gamemode;
import com.catand.skyblockhelper.data.SkyblockLevelColorCode;
import com.catand.skyblockhelper.data.SkyblockProfile;
import com.catand.skyblockhelper.utils.ImageUtil;
import com.catand.skyblockhelper.utils.JavaFXUtils;
import com.catand.skyblockhelper.utils.MinecraftUtils;
import com.catand.skyblockhelper.utils.ProfileUtil;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import me.kbrewster.exceptions.APIException;
import me.kbrewster.mojangapi.MojangAPI;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@Component
public class TrophyFishPlugin extends BotPlugin {
	MsgUtils sendMsg = MsgUtils.builder();
	Player player;
	JSONObject profile;
	String profileName;
	String playerName;
	Parent root;
	Scene scene;
	Image skinImage;

	@Override
	public int onGroupMessage(Bot bot, GroupMessageEvent event) {
		//格式化消息获取参数
		String messageRaw = event.getRawMessage();
		if (!messageRaw.contains("/")) {
			return MESSAGE_IGNORE;
		}
		messageRaw = messageRaw.split("/")[1];
		if (!messageRaw.startsWith("奖杯鱼")) {
			return MESSAGE_IGNORE;
		}
		String[] args = messageRaw.split(" ");
		if (args.length < 2) {
			sendMsg = MsgUtils.builder().text("参数错误，\n正确格式：/奖杯鱼 <玩家名> [档案名]");
			bot.sendGroupMsg(event.getGroupId(), event.getUserId(), sendMsg.build(), false);
			return MESSAGE_BLOCK;
		}

		try {
			if (args.length > 2) {
				profileName = args[2];
			}
			playerName = args[1];
			CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
				fetchTrophyFishData();
				return null;
			});
			CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
				try {
					fetchSkinData();
				} catch (APIException | IOException e) {
					throw new RuntimeException(e);
				}
				return null;
			});
			CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
				try {
					Platform.runLater(() -> {
						try {
							createScene();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					});
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				return null;
			});
			future1.handle((result1, exception1) -> {
				if (exception1 != null) {
					throw new RuntimeException(exception1);
				} else {
					if (profile == null) {
						sendMsg = MsgUtils.builder().text("俺没瞅见" + playerName + "有个啥" + profileName + "啊\n俺只知道他有这些:\n");
						for (JSONObject profile2 : player.getProfileList()) {
							sendMsg.text("[" + ProfileUtil.getSkyblockLevel(profile2) + "]" + profile2.getString("cute_name") + Gamemode.getGamemode(profile2).getIcon() + "\n");
						}
						bot.sendGroupMsg(event.getGroupId(), event.getUserId(), sendMsg.build(), false);
						return MESSAGE_BLOCK;
					} else {
						return MESSAGE_IGNORE;
					}
				}
			});
			future2.handle((result2, exception2) -> {
				if (exception2 != null) {
					throw new RuntimeException(exception2);
				}
				return null;
			});
			future3.handle((result3, exception3) -> {
				if (exception3 != null) {
					throw new RuntimeException(exception3);
				}
				return null;
			});
			CompletableFuture.allOf(future1, future2, future3).join();
			if (profile == null) {
				return MESSAGE_BLOCK;
			}
			// 设置基本信息
			Text playerNameText = (Text) scene.lookup("#IGN");
			playerNameText.setText(playerName);
			Text profileNameText = (Text) scene.lookup("#ProfileName");
			profileNameText.setText(profileName);
			Text profileIconText = (Text) scene.lookup("#ProfileIcon");
			profileIconText.setText(SkyblockProfile.getProfile(profileName).getEmoji()[0]);
			Text skyblockLevelText = (Text) scene.lookup("#ProfileLevel");
			int skyblockLevel = ProfileUtil.getSkyblockLevel(profile);
			skyblockLevelText.setText(String.valueOf(skyblockLevel));
			Color skyblockLevelColor = JavaFXUtils.AWTColorToJavaFXColor(SkyblockLevelColorCode.getLevelColor(skyblockLevel).getColor());
			skyblockLevelText.setFill(skyblockLevelColor);
			Text gamemodeText = (Text) scene.lookup("#Gamemode");
			gamemodeText.setText(Gamemode.getGamemode(profile).getChineseName());
			Text gamemodeIconText = (Text) scene.lookup("#GamemodeIcon");
			gamemodeIconText.setText(Gamemode.getGamemode(profile).getIcon());

			ImageView skinImageView = (ImageView) scene.lookup("#SkinImage");
			skinImageView.setImage(skinImage);

			// 构造基本奖杯鱼数据
			JSONObject trophyFishData = ProfileUtil.getCrimsonIsleData(profile).getJSONObject("trophy_fish");
			int totalCaught = trophyFishData.getIntValue("total_caught");
			Text totalCaughtText = (Text) scene.lookup("#TotalFish");
			totalCaughtText.setText(String.valueOf(totalCaught));

			ArrayList<JSONObject> trophyFishList = (ArrayList<JSONObject>) trophyFishData.getJSONArray("fish").toJavaList(JSONObject.class);
			trophyFishList.removeIf(fish -> !fish.containsKey("highest_tier"));

			// 处理奖杯鱼List
			trophyFishList.forEach(trophyFish -> {
				// 把json名字转换
				String name = trophyFish.getString("display_name");
				name = switch (name) {
					case "Blobfish" -> "blobfish";
					case "Flyfish" -> "flyfish";
					case "Golden Fish" -> "golden_fish";
					case "Gusher" -> "gusher";
					case "Karate Fish" -> "karate_fish";
					case "Lavahorse" -> "lavahorse";
					case "Mana Ray" -> "mana_ray";
					case "Moldfin" -> "moldfin";
					case "Obfuscated 1" -> "obfuscated_1_fish";
					case "Obfuscated 2" -> "obfuscated_2_fish";
					case "Obfuscated 3" -> "obfuscated_3_fish";
					case "Skeleton Fish" -> "skeleton_fish";
					case "Slugfish" -> "slugfish";
					case "Soul Fish" -> "soul_fish";
					case "Steaming-Hot Flounder" -> "steaming_hot_flounder";
					case "Sulphur Skitter" -> "sulphur_skitter";
					case "Vanille" -> "vanille";
					case "Volcanic Stonefish" -> "volcanic_stonefish";
					default -> "Unknown";
				};

				// 设置边框颜色
				String tier = trophyFish.getString("highest_tier");
				HBox hbox = (HBox) scene.lookup("#" + name);
				String css = "-fx-background-color: rgba(30, 30, 30, 0.784); -fx-background-radius: 20; -fx-border-radius: 15; -fx-border-width: 5; -fx-border-color: ";
				switch (tier) {
					case "bronze" -> hbox.setStyle(css + "#ffc800" + ";");
					case "silver" -> hbox.setStyle(css + "#c0c0c0" + ";");
					case "gold" -> hbox.setStyle(css + "#ffff00" + ";");
					case "diamond" -> hbox.setStyle(css + "#00ffff" + ";");
				}

				// 设置鱼的图像
				ImageView imageView = (ImageView) scene.lookup("#" + name + "Image");
				Image scaledImage = ImageUtil.scaleImage(getFishImage(name, tier), 48, 48);
				imageView.setImage(scaledImage);

				// 设置各等级捕获次数
				Text text = (Text) scene.lookup("#" + name + "_bronze");
				text.setText(String.valueOf(trophyFish.getIntValue("bronze")));
				text = (Text) scene.lookup("#" + name + "_silver");
				text.setText(String.valueOf(trophyFish.getIntValue("silver")));
				text = (Text) scene.lookup("#" + name + "_gold");
				text.setText(String.valueOf(trophyFish.getIntValue("gold")));
				text = (Text) scene.lookup("#" + name + "_diamond");
				text.setText(String.valueOf(trophyFish.getIntValue("diamond")));
			});

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
		} catch (Exception e) {
			new ErrorProcessor(e, bot, event);
		}
		return MESSAGE_BLOCK;
	}

	private void fetchTrophyFishData() {
		player = new Player(playerName);
		profile = player.getMainProfile();
		this.playerName = ProfileUtil.getDisplayNameData(profile);

		if (profileName != null) {
			// 如果指定了存档名，那么就获取指定存档的数据
			profileName = SkyblockProfile.getProfile(profileName).getJsonName();
			profile = player.getProfile(profileName);
		} else {
			profileName = profile.getString("cute_name");
		}
	}

	private void fetchSkinData() throws APIException, IOException {
		String uuid = String.valueOf(MojangAPI.getUUID(playerName));
		skinImage = ImageUtil.convertToFXImage(MinecraftUtils.getBufferedImageSkin(uuid));
	}

	private void createScene() throws IOException {
		root = FXMLLoader.load(TrophyFishPlugin.class.getResource("/scene/TrophyFish.fxml"));
		scene = new Scene(root, 960, 570);

		// 设置背景
		Image background = ImageUtil.getImageBackground(960, 500);
		BackgroundImage backgroundImage = new BackgroundImage(background, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
		HBox hBox = (HBox) scene.lookup("#Background");
		hBox.setBackground(new Background(backgroundImage));

		// 设置鱼的图像
		setBasicFishImage("blobfish");
		setBasicFishImage("flyfish");
		setBasicFishImage("golden_fish");
		setBasicFishImage("gusher");
		setBasicFishImage("karate_fish");
		setBasicFishImage("lavahorse");
		setBasicFishImage("mana_ray");
		setBasicFishImage("moldfin");
		setBasicFishImage("obfuscated_1_fish");
		setBasicFishImage("obfuscated_2_fish");
		setBasicFishImage("obfuscated_3_fish");
		setBasicFishImage("skeleton_fish");
		setBasicFishImage("slugfish");
		setBasicFishImage("soul_fish");
		setBasicFishImage("steaming_hot_flounder");
		setBasicFishImage("sulphur_skitter");
		setBasicFishImage("vanille");
		setBasicFishImage("volcanic_stonefish");
	}

	private Image getFishImage(String name, String tier) {
		return new Image("assets/trophy_fish/" + name + "/" + name + "_" + tier + ".png");
	}

	private void setBasicFishImage(String name) {
		ImageView imageView = (ImageView) scene.lookup("#" + name + "Image");
		Image scaledImage = ImageUtil.scaleImage(getFishImage(name, "bronze"), 48, 48);
		imageView.setImage(scaledImage);
	}
}
