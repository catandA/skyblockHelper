package com.catand.skyblockhelper.plugins;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.catand.skyblockhelper.ErrorProcessor;
import com.catand.skyblockhelper.data.Gamemode;
import com.catand.skyblockhelper.data.MinecraftColorCode;
import com.catand.skyblockhelper.data.SkyblockLevelColorCode;
import com.catand.skyblockhelper.data.SkyblockProfile;
import com.catand.skyblockhelper.exception.NoPlayerException;
import com.catand.skyblockhelper.exception.NoProfilesException;
import com.catand.skyblockhelper.exception.NoSuchProfileException;
import com.catand.skyblockhelper.utils.*;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import me.kbrewster.mojangapi.MojangAPI;
import net.hypixel.api.reply.PlayerReply;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
public class TrophyFishPlugin extends BotPlugin {

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
		messageRaw = messageRaw.replaceAll("\\s+", " ");
		String[] args = messageRaw.split(" ");
		if (args.length < 2) {
			bot.sendGroupMsg(event.getGroupId(), event.getUserId(), MsgUtils.builder().text("参数错误，\n正确格式：/奖杯鱼 <玩家名> [档案名]").build(), false);
			return MESSAGE_BLOCK;
		}
		final String[] playerName = {args[1]};
		final String[] profileName = {null};
		final String[] rank = {null};
		final MinecraftColorCode[] rankPlusColorCode = {null};
		if (args.length > 2) {
			profileName[0] = SkyblockProfile.getProfile(args[2]).getJsonName();
		}
		MsgUtils sendMsg = MsgUtils.builder();
		try {
			final Scene scene;
			final JSONArray[] profiles = new JSONArray[1];
			final JSONObject[] profile = new JSONObject[1];
			UUID uuid;
			try {
				uuid = MojangAPI.getUUID(playerName[0]);
			} catch (NullPointerException e) {
				throw new NoPlayerException(playerName[0]);
			}
			CompletableFuture<JSONArray> fetchSkyBlockProfilesDataFuture = HypixelAPIUtil.fetchSkyBlockProfilesData(uuid);
			CompletableFuture<Scene> creatSceneFuture = JavaFXUtils.createSceneWithBackgroundAsync("/scene/TrophyFish.fxml", 960, 570, 960, 500);
			CompletableFuture<PlayerReply.Player> fetchPlayersDataFuture = HypixelAPIUtil.fetchPlayersData(uuid);
			CompletableFuture<Image> fetchSkinFuture = MinecraftUtils.getFXImageSkinAsync(uuid.toString());

			fetchSkyBlockProfilesDataFuture.exceptionally(throwable -> {
				throw new RuntimeException(throwable);
			});
			creatSceneFuture.exceptionally(throwable -> {
				throw new RuntimeException(throwable);
			});
			fetchPlayersDataFuture.exceptionally(throwable -> {
				throw new RuntimeException(throwable);
			});
			fetchSkinFuture.exceptionally(throwable -> {
				throw new RuntimeException(throwable);
			});

			CompletableFuture<Void> fetchSkyBlockProfilesDataFutureThenAccept = fetchSkyBlockProfilesDataFuture.thenAcceptAsync(result -> {
				profiles[0] = result;
				if (profiles[0].isEmpty()) {
					// 没有存档 Wiped R.I.P :(
					throw new NoProfilesException(profiles[0], playerName[0], uuid);
				}
				if (profileName[0] == null) {
					// 找到主档
					profiles[0].forEach(value -> {
						JSONObject jsonObject = (JSONObject) value;
						if (jsonObject.getBoolean("selected")) {
							profile[0] = jsonObject;
							profileName[0] = jsonObject.getString("cute_name");
						}
					});
					if (profile[0] == null) {
						// 没有主档 Wiped R.I.P :(
						throw new NoProfilesException(profiles[0], playerName[0], uuid);
					}
				} else {
					// 找到指定存档
					profiles[0].forEach((value) -> {
						JSONObject jsonObject = (JSONObject) value;
						if (jsonObject.getString("cute_name").equals(profileName[0])) {
							profile[0] = jsonObject;
						}
					});
				}
			});

			CompletableFuture<Void> fetchPlayersDataFutureThenAccept = fetchPlayersDataFuture.thenAcceptAsync(result -> {
				playerName[0] = result.getName();
				rank[0] = result.getHighestRank();
				rankPlusColorCode[0] = MinecraftColorCode.getColorCode(result.getSelectedPlusColor().toLowerCase());
			});

			CompletableFuture<Scene> creatSceneFutureThenRun = creatSceneFuture.thenApplyAsync(scene1 -> {
				// 设置鱼的图像
				setBasicFishImage(scene1, "blobfish");
				setBasicFishImage(scene1, "flyfish");
				setBasicFishImage(scene1, "golden_fish");
				setBasicFishImage(scene1, "gusher");
				setBasicFishImage(scene1, "karate_fish");
				setBasicFishImage(scene1, "lava_horse");
				setBasicFishImage(scene1, "mana_ray");
				setBasicFishImage(scene1, "moldfin");
				setBasicFishImage(scene1, "obfuscated_fish_1");
				setBasicFishImage(scene1, "obfuscated_fish_2");
				setBasicFishImage(scene1, "obfuscated_fish_3");
				setBasicFishImage(scene1, "skeleton_fish");
				setBasicFishImage(scene1, "slugfish");
				setBasicFishImage(scene1, "soul_fish");
				setBasicFishImage(scene1, "steaming_hot_flounder");
				setBasicFishImage(scene1, "sulphur_skitter");
				setBasicFishImage(scene1, "vanille");
				setBasicFishImage(scene1, "volcanic_stonefish");
				return scene1;
			});
			CompletableFuture.allOf(fetchSkyBlockProfilesDataFutureThenAccept, creatSceneFutureThenRun, fetchPlayersDataFutureThenAccept, fetchSkinFuture).join();

			scene = creatSceneFutureThenRun.get();
			final JSONObject[] member = new JSONObject[1];
			if (profile[0] == null) {
				throw new NoSuchProfileException(profiles[0], profileName[0], playerName[0], uuid);
			} else {
				JSONObject members = profile[0].getJSONObject("members");
				// 循环成员
				members.forEach((key, value) -> {
					// 找到当前玩家
					if (key.equals(uuid.toString().replace("-", "").toLowerCase())) {
						member[0] = (JSONObject) value;
					}
				});
			}

			int skyblockLevel = Math.floorDiv(member[0].getJSONObject("leveling").getIntValue("experience"), 100);
			JSONObject trophyFishData = member[0].getJSONObject("trophy_fish");

			// 设置基本信息
			Text playerNameText = (Text) scene.lookup("#IGN");
			playerNameText.setText(playerName[0]);
			Text profileNameText = (Text) scene.lookup("#ProfileName");
			profileNameText.setText(profileName[0]);
			Text profileIconText = (Text) scene.lookup("#ProfileIcon");
			profileIconText.setText(SkyblockProfile.getProfile(profileName[0]).getEmoji()[0]);
			Text skyblockLevelText = (Text) scene.lookup("#ProfileLevel");
			skyblockLevelText.setText(String.valueOf(skyblockLevel));
			Color skyblockLevelColor = JavaFXUtils.AWTColorToJavaFXColor(SkyblockLevelColorCode.getLevelColor(skyblockLevel).getColor());
			skyblockLevelText.setFill(skyblockLevelColor);
			Text gamemodeText = (Text) scene.lookup("#Gamemode");
			gamemodeText.setText(Gamemode.getGamemode(profile[0]).getChineseName());
			Text gamemodeIconText = (Text) scene.lookup("#GamemodeIcon");
			gamemodeIconText.setText(Gamemode.getGamemode(profile[0]).getIcon());
			Text rankText = (Text) scene.lookup("#rankName");
			rankText.setText(HypixelRankUtils.getName(rank[0]));
			Color rankColor = JavaFXUtils.AWTColorToJavaFXColor(HypixelRankUtils.getRankColor(rank[0]).getColor());
			rankText.setFill(rankColor);
			Text rankPlusText = (Text) scene.lookup("#rankPlus");
			StringBuilder plusBuilder = new StringBuilder();
			int plusNumber = HypixelRankUtils.getPlusNumber(rank[0]);
			plusBuilder.append("+".repeat(Math.max(0, plusNumber)));
			if (plusNumber != 0) {
				rankPlusText.setText(plusBuilder.toString());
				Color rankPlusColor = JavaFXUtils.AWTColorToJavaFXColor(rankPlusColorCode[0].getColor());
				rankPlusText.setFill(rankPlusColor);
			}
			Text bracketText1 = (Text) scene.lookup("#rankBracket1");
			Text bracketText2 = (Text) scene.lookup("#rankBracket2");
			Color bracketColor = JavaFXUtils.AWTColorToJavaFXColor(HypixelRankUtils.getBracketColor(rank[0]).getColor());
			bracketText1.setFill(bracketColor);
			bracketText2.setFill(bracketColor);

			// 设置皮肤
			ImageView skinImageView = (ImageView) scene.lookup("#skinImage");
			skinImageView.setImage(fetchSkinFuture.get());


			// 构造基本奖杯鱼数据
			String[] fishNames = {
					"blobfish", "flyfish", "golden_fish", "gusher", "karate_fish", "lava_horse", "mana_ray", "moldfin",
					"obfuscated_fish_1", "obfuscated_fish_2", "obfuscated_fish_3", "skeleton_fish", "slugfish", "soul_fish",
					"steaming_hot_flounder", "sulphur_skitter", "vanille", "volcanic_stonefish"
			};
			ArrayList<TrophyFish> trophyFishList = new ArrayList<>();
			for (String fishName : fishNames) {
				TrophyFish trophyFish = new TrophyFish();
				trophyFish.name = fishName;
				trophyFish.total = trophyFishData.containsKey(fishName) ? trophyFishData.getIntValue(fishName) : 0;
				if (trophyFish.total == 0) {
					continue;
				}
				trophyFish.bronze = trophyFishData.containsKey(fishName + "_bronze") ? trophyFishData.getIntValue(fishName + "_bronze") : 0;
				trophyFish.silver = trophyFishData.containsKey(fishName + "_silver") ? trophyFishData.getIntValue(fishName + "_silver") : 0;
				trophyFish.gold = trophyFishData.containsKey(fishName + "_gold") ? trophyFishData.getIntValue(fishName + "_gold") : 0;
				trophyFish.diamond = trophyFishData.containsKey(fishName + "_diamond") ? trophyFishData.getIntValue(fishName + "_diamond") : 0;
				trophyFishList.add(trophyFish);
			}


			// 处理奖杯鱼List
			trophyFishList.forEach(trophyFish -> {
				// 设置边框颜色
				String name = trophyFish.name;
				String tier = trophyFish.getHighestTier();
				HBox hbox = (HBox) scene.lookup("#" + name);
				String css = "-fx-background-color: rgba(30, 30, 30, 0.784); -fx-background-radius: 20; -fx-border-radius: 15; -fx-border-width: 5; -fx-border-color: ";
				switch (tier) {
					case "bronze" -> hbox.setStyle(css + "#964a0c" + ";");
					case "silver" -> hbox.setStyle(css + "#757f89" + ";");
					case "gold" -> hbox.setStyle(css + "#e49b23" + ";");
					case "diamond" -> hbox.setStyle(css + "#1cffff" + ";");
				}

				// 设置鱼的图像
				ImageView imageView = (ImageView) scene.lookup("#" + name + "Image");
				Image scaledImage = ImageUtil.scaleImage(getFishImage(name, tier), 48, 48);
				imageView.setImage(scaledImage);

				// 设置各等级捕获次数
				Text text = (Text) scene.lookup("#" + name + "_bronze");
				text.setText(String.valueOf(trophyFish.bronze));
				text = (Text) scene.lookup("#" + name + "_silver");
				text.setText(String.valueOf(trophyFish.silver));
				text = (Text) scene.lookup("#" + name + "_gold");
				text.setText(String.valueOf(trophyFish.gold));
				text = (Text) scene.lookup("#" + name + "_diamond");
				text.setText(String.valueOf(trophyFish.diamond));
			});

			// 发送消息
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

	public static Image getFishImage(String name, String tier) {
		URL url = TrophyFishPlugin.class.getResource("/assets/trophy_fish/" + name + "/" + name + "_" + tier + ".png");
		return new Image(url.toString());
	}

	public static void setBasicFishImage(Scene scene, String name) {
		ImageView imageView = (ImageView) scene.lookup("#" + name + "Image");
		Image scaledImage = ImageUtil.scaleImage(getFishImage(name, "bronze"), 48, 48);
		imageView.setImage(scaledImage);
	}

	private static class TrophyFish {
		private String name;
		private int total;
		private int bronze;
		private int silver;
		private int gold;
		private int diamond;

		public String getHighestTier() {
			if (diamond > 0) {
				return "diamond";
			} else if (gold > 0) {
				return "gold";
			} else if (silver > 0) {
				return "silver";
			} else if (bronze > 0) {
				return "bronze";
			} else {
				return "none";
			}
		}
	}
}
