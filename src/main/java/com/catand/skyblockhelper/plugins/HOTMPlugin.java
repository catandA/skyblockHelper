package com.catand.skyblockhelper.plugins;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.catand.skyblockhelper.ErrorProcessor;
import com.catand.skyblockhelper.data.*;
import com.catand.skyblockhelper.exception.NoProfilesException;
import com.catand.skyblockhelper.exception.NoSuchProfileException;
import com.catand.skyblockhelper.utils.*;
import com.google.gson.Gson;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import me.kbrewster.mojangapi.MojangAPI;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.reply.PlayerReply;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Component
public class HOTMPlugin extends BotPlugin {
	private static final String IMAGE_PATH_PREFIX = "/assets/hotm/";
	private static final String IMAGE_PATH_SUFFIX = "_bar_hotm.png";

	@Override
	public int onGroupMessage(Bot bot, GroupMessageEvent event) {
		//格式化消息获取参数
		String messageRaw = event.getRawMessage();
		if (!messageRaw.contains("/")) {
			return MESSAGE_IGNORE;
		}
		messageRaw = messageRaw.split("/")[1];
		if (!messageRaw.startsWith("山心")) {
			return MESSAGE_IGNORE;
		}
		String[] args = messageRaw.split(" ");
		if (args.length < 2) {
			bot.sendGroupMsg(event.getGroupId(), event.getUserId(), MsgUtils.builder().text("参数错误，\n正确格式：/山心 <玩家名> [档案名]").build(), false);
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
			final Scene[] scene = new Scene[1];
			final JSONArray[] profiles = new JSONArray[1];
			final JSONObject[] profile = new JSONObject[1];
			UUID uuid = MojangAPI.getUUID(playerName[0]);

			CompletableFuture<JSONArray> fetchSkyBlockProfilesDataFuture = fetchSkyBlockProfilesData(uuid);
			CompletableFuture<Void> creatSceneFuture = CompletableFuture.supplyAsync(() -> {
				final CompletableFuture<Void> innerFuture = new CompletableFuture<>();
				Platform.runLater(() -> {
					try {
						scene[0] = createScene();
						innerFuture.complete(null);
					} catch (Exception e) {
						innerFuture.completeExceptionally(e);
					}
				});
				return innerFuture;
			}).thenCompose(Function.identity());
			CompletableFuture<PlayerReply.Player> fetchPlayersDataFuture = fetchPlayersData(uuid);
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

			CompletableFuture.allOf(fetchSkyBlockProfilesDataFutureThenAccept, creatSceneFuture, fetchPlayersDataFutureThenAccept, fetchSkinFuture).join();

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
			double miningTotalExp = member[0].getDoubleValue("experience_skill_mining");
			JSONObject miningData = member[0].getJSONObject("mining_core");

			// 设置基本信息
			Text playerNameText = (Text) scene[0].lookup("#IGN");
			playerNameText.setText(playerName[0]);
			Text profileNameText = (Text) scene[0].lookup("#ProfileName");
			profileNameText.setText(profileName[0]);
			Text profileIconText = (Text) scene[0].lookup("#ProfileIcon");
			profileIconText.setText(SkyblockProfile.getProfile(profileName[0]).getEmoji()[0]);
			Text skyblockLevelText = (Text) scene[0].lookup("#ProfileLevel");
			skyblockLevelText.setText(String.valueOf(skyblockLevel));
			Color skyblockLevelColor = JavaFXUtils.AWTColorToJavaFXColor(SkyblockLevelColorCode.getLevelColor(skyblockLevel).getColor());
			skyblockLevelText.setFill(skyblockLevelColor);
			Text gamemodeText = (Text) scene[0].lookup("#Gamemode");
			gamemodeText.setText(Gamemode.getGamemode(profile[0]).getChineseName());
			Text gamemodeIconText = (Text) scene[0].lookup("#GamemodeIcon");
			gamemodeIconText.setText(Gamemode.getGamemode(profile[0]).getIcon());
			Text rankText = (Text) scene[0].lookup("#rankName");
			rankText.setText(HypixelRankUtils.getName(rank[0]));
			Color rankColor = JavaFXUtils.AWTColorToJavaFXColor(HypixelRankUtils.getRankColor(rank[0]).getColor());
			rankText.setFill(rankColor);
			Text rankPlusText = (Text) scene[0].lookup("#rankPlus");
			StringBuilder plusBuilder = new StringBuilder();
			int plusNumber = HypixelRankUtils.getPlusNumber(rank[0]);
			plusBuilder.append("+".repeat(Math.max(0, plusNumber)));
			if (plusNumber != 0) {
				rankPlusText.setText(plusBuilder.toString());
				Color rankPlusColor = JavaFXUtils.AWTColorToJavaFXColor(rankPlusColorCode[0].getColor());
				rankPlusText.setFill(rankPlusColor);
			}
			Text bracketText1 = (Text) scene[0].lookup("#rankBracket1");
			Text bracketText2 = (Text) scene[0].lookup("#rankBracket2");
			Color bracketColor = JavaFXUtils.AWTColorToJavaFXColor(HypixelRankUtils.getBracketColor(rank[0]).getColor());
			bracketText1.setFill(bracketColor);
			bracketText2.setFill(bracketColor);

			// 设置皮肤
			ImageView skinImageView = (ImageView) scene[0].lookup("#skinImage");
			skinImageView.setImage(fetchSkinFuture.get());

			// 设置矿工等级
			Text miningLevelText = (Text) scene[0].lookup("#miningLevel");
			Text miningLevelProgressText = (Text) scene[0].lookup("#miningLevelProgress");
			ProgressBar miningLevelProgressBar = (ProgressBar) scene[0].lookup("#miningLevelProgressBar");
			int miningLevel = SkillsLevelInfo.getCurrentLevel((int) miningTotalExp).getLevel();
			if (miningLevel == 60) {
				miningLevelText.setText("60");
				miningLevelProgressText.setText(NumberFormatUtil.format((int) miningTotalExp, 1));
				miningLevelProgressBar.setProgress(1.0);
			} else {
				miningLevelText.setText(miningLevel + "/60");
				int currentLevelExp = SkillsLevelInfo.getCurrentLevelXp((int) miningTotalExp);
				int thisMiningLevelExp = SkillsLevelInfo.LEVELS.get(miningLevel + 1).getXpRequired();
				miningLevelProgressText.setText(NumberFormatUtil.format(currentLevelExp, 1) + "/" + NumberFormatUtil.format(thisMiningLevelExp, 1));
				miningLevelProgressBar.setProgress((double) currentLevelExp / (double) thisMiningLevelExp);
			}

			// 设置山心等级
			double hotmTotalExp = miningData.getDoubleValue("experience");
			Text hotmLevelText = (Text) scene[0].lookup("#HOTMLevel");
			Text hotmLevelProgressText = (Text) scene[0].lookup("#HOTMLevelProgress");
			ProgressBar hotmLevelProgressBar = (ProgressBar) scene[0].lookup("#HOTMLevelProgressBar");
			int hotmLevel = HOTMLevelInfo.getCurrentLevel((int) hotmTotalExp).getLevel();
			if (hotmLevel == 7) {
				hotmLevelText.setText("7");
				hotmLevelProgressText.setText(NumberFormatUtil.format((int) hotmTotalExp, 1));
				hotmLevelProgressBar.setProgress(1.0);
			} else {
				hotmLevelText.setText(hotmLevel + "/7");
				int currentLevelExp = HOTMLevelInfo.getCurrentLevelXp((int) hotmTotalExp);
				int thisHOTMLevelExp = HOTMLevelInfo.LEVELS.get(hotmLevel).getXpRequired();
				hotmLevelProgressText.setText(NumberFormatUtil.format(currentLevelExp, 1) + "/" + NumberFormatUtil.format(thisHOTMLevelExp, 1));
				hotmLevelProgressBar.setProgress((double) currentLevelExp / (double) thisHOTMLevelExp);
			}

			Color baseColor = Color.web("#008000");
			for (int i = 1; i <= 7; i++) {
				Rectangle rectangle = (Rectangle) scene[0].lookup("#HOTMLevelProgressBlock" + i);
				if (rectangle != null) {
					double opacity = i <= hotmLevel ? 1.0 : 0;
					Color color = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), opacity);
					rectangle.setFill(color);
				}
			}

			// 设置任务里程碑
			JSONArray questsCompleted = member[0].getJSONArray("tutorial");
			final int[] maxTier = {0};
			questsCompleted.forEach(quest -> {
				String questName = quest.toString();
				if (questName.startsWith("commission_milestone_reward_mining_xp_tier_")) {
					int tier = Integer.parseInt(questName.substring(43));
					if (tier > maxTier[0]) {
						maxTier[0] = tier;
					}
				}
			});
			Text commissionMilestoneText = (Text) scene[0].lookup("#commissionMilestone");
			if (maxTier[0] == 6) {
				commissionMilestoneText.setText("6");
			} else {
				commissionMilestoneText.setText(maxTier[0] + "/6");
			}
			int connissionNumber = fetchPlayersDataFuture.get().getRaw().getAsJsonObject("achievements").get("skyblock_hard_working_miner").getAsInt();
			Text commissionNumberText = (Text) scene[0].lookup("#commissionNumber");
			commissionNumberText.setText(String.valueOf(connissionNumber));

			// 设置粉末
			int mithrilPowder = miningData.getIntValue("powder_mithril_total") + miningData.getIntValue("powder_spent_mithril");
			int gemstonePowder = miningData.getIntValue("powder_gemstone_total") + miningData.getIntValue("powder_spent_gemstone");
			Text mithrilPowderText = (Text) scene[0].lookup("#mythrilPowder");
			mithrilPowderText.setText(NumberFormatUtil.format(mithrilPowder, 1));
			Text gemstonePowderText = (Text) scene[0].lookup("#gemstonePowder");
			gemstonePowderText.setText(NumberFormatUtil.format(gemstonePowder, 1));

			// 设置水晶
			JSONObject crystalsJsonObject = miningData.getJSONObject("crystals");
			if (crystalsJsonObject != null && !crystalsJsonObject.isEmpty()) {
				final int[] crystalCount = {0};
				crystalsJsonObject.forEach((key, value) -> {
					JSONObject jsonObject = (JSONObject) value;
					switch (jsonObject.getString("state")) {
						case "FOUND":
							if (!key.equals("jasper_crystal") && !key.equals("ruby_crystal")) {
								crystalCount[0]++;
							}
							Text crystalText = (Text) scene[0].lookup("#" + key);
							crystalText.setText("✔");
							crystalText.setFill(Color.GREEN);
							break;
						case "PLACED":
							Text crystalText1 = (Text) scene[0].lookup("#" + key);
							crystalText1.setText("✔");
							crystalText1.setFill(Color.YELLOW);
							break;
						case "NOT_FOUND":
							Text crystalText2 = (Text) scene[0].lookup("#" + key);
							crystalText2.setText("✖");
							crystalText2.setFill(Color.RED);
							break;
					}
				});
				Text crystalCountText = (Text) scene[0].lookup("#crystal");
				crystalCountText.setText(String.valueOf(crystalCount[0]));
			} else {
				Text crystalHintText = (Text) scene[0].lookup("#crystalHint");
				crystalHintText.setText("没跑过水晶 :(");
				Text crystalCountText = (Text) scene[0].lookup("#crystal");
				crystalCountText.setText("");
			}

			// 设置当前技能
			Text currentSkillText = (Text) scene[0].lookup("#currentPickaxeAbility");
			String currentSkill = miningData.getString("selected_pickaxe_ability");
			if (currentSkill == null) {
				currentSkill = "别问,问就是没有";
			} else {
				switch (currentSkill) {
					case "vein_seeker" -> currentSkill = "Vein Seeker";
					case "maniac_miner" -> currentSkill = "Maniac Miner";
					case "pickaxe_toss" -> currentSkill = "Pickobulus";
					case "mining_speed_boost" -> currentSkill = "Mining Speed Boost";
				}
			}
			currentSkillText.setText(currentSkill);

			// 重置时间
			Text resetTimeText = (Text) scene[0].lookup("#resetTime");
			long resetTime = miningData.getLongValue("last_reset");
			if (resetTime == 0) {
				resetTimeText.setText("没重置过");
			} else {
				Instant instant = Instant.ofEpochSecond(resetTime / 1000);
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.systemDefault());
				resetTimeText.setText(formatter.format(instant));
			}

			// 山心树
			JSONObject treeData = miningData.getJSONObject("nodes");

			ImageView POTMImageView = (ImageView) scene[0].lookup("#special_0");
			Text POTMText = (Text) scene[0].lookup("#special_0_1");
			URL POTMImageURL;
			if (treeData.containsKey("special_0")) {
				int POTM = treeData.getIntValue("special_0");
				if (POTM == 7) {
					POTMText.setText("7");
					POTMText.setFill(Color.GREEN);
				} else {
					POTMText.setText(POTM + "/7");
				}
				POTMImageURL = HOTMPlugin.class.getResource("/assets/hotm/peak_of_the_mountain_" + POTM + ".png");
				Image scaledImage = ImageUtil.scaleImage(new Image(POTMImageURL.toString()), 64, 64);
				POTMImageView.setImage(scaledImage);
			} else {
				POTMImageURL = HOTMPlugin.class.getResource("/assets/hotm/peak_of_the_mountain_0.png");
				Image scaledImage = ImageUtil.scaleImage(new Image(POTMImageURL.toString()), 64, 64);
				POTMImageView.setImage(scaledImage);
				POTMText.setText("✖");
				POTMText.setFill(Color.RED);
			}

			updateImageViewAndText(scene[0], treeData, "mining_speed", "vertical_start", 50);
			updateImageViewAndText(scene[0], treeData, "mining_fortune", "fork", 50);
			updateImageViewAndText(scene[0], treeData, "titanium_insanium", "horizontal", 50);
			updateImageViewAndText(scene[0], treeData, "mining_speed_boost", "45_degree", 1);
			updateImageViewAndText(scene[0], treeData, "forge_time", "horizontal", 20);
			updateImageViewAndText(scene[0], treeData, "pickaxe_toss", "135_degree", 1);
			updateImageViewAndText(scene[0], treeData, "daily_powder", "vertical", 100);
			updateImageViewAndText(scene[0], treeData, "random_event", "vertical", 45);
			updateImageViewAndText(scene[0], treeData, "fallen_star_bonus", "vertical", 30);
			updateImageViewAndText(scene[0], treeData, "mining_madness", "fork", 1);
			updateImageViewAndText(scene[0], treeData, "daily_effect", "horizontal_start", 1);
			updateImageViewAndText(scene[0], treeData, "mining_experience", "horizontal", 100);
			updateImageViewAndText(scene[0], treeData, "efficient_miner", "fork", 100);
			updateImageViewAndText(scene[0], treeData, "experience_orbs", "horizontal", 80);
			updateImageViewAndText(scene[0], treeData, "front_loaded", "fork", 1);
			updateImageViewAndText(scene[0], treeData, "precision_mining", "horizontal_end", 1);
			updateImageViewAndText(scene[0], treeData, "goblin_killer", "vertical", 1);
			updateImageViewAndText(scene[0], treeData, "star_powder", "vertical", 1);
			updateImageViewAndText(scene[0], treeData, "vein_seeker", "horizontal_start", 1);
			updateImageViewAndText(scene[0], treeData, "lonesome_miner", "fork", 45);
			updateImageViewAndText(scene[0], treeData, "professional", "horizontal", 140);
			updateImageViewAndText(scene[0], treeData, "mole", "fork", 190);
			updateImageViewAndText(scene[0], treeData, "fortunate", "horizontal", 20);
			updateImageViewAndText(scene[0], treeData, "great_explorer", "fork", 20);
			updateImageViewAndText(scene[0], treeData, "maniac_miner", "horizontal_end", 1);
			updateImageViewAndText(scene[0], treeData, "powder_buff", "vertical_end", 50);
			updateImageViewAndText(scene[0], treeData, "mining_speed_2", "vertical_end", 50);
			updateImageViewAndText(scene[0], treeData, "mining_fortune_2", "vertical_end", 50);


			// 发送消息
			final WritableImage[] writableImage = new WritableImage[1];
			Platform.runLater(() -> {
				try {
					writableImage[0] = scene[0].snapshot(null);
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

	private CompletableFuture<JSONArray> fetchSkyBlockProfilesData(UUID uuid) {
		HypixelAPI hypixelAPI = HypixelAPIUtil.getHypixelAPI();
		return hypixelAPI.getSkyBlockProfiles(uuid).thenApplyAsync(
				resourceReply -> {
					Gson gson = new Gson();
					String jsonString = gson.toJson(resourceReply.getProfiles());
					return JSONArray.parseArray(jsonString);
				}
		);
	}

	private CompletableFuture<PlayerReply.Player> fetchPlayersData(UUID uuid) {
		HypixelAPI hypixelAPI = HypixelAPIUtil.getHypixelAPI();
		return hypixelAPI.getPlayerByUuid(uuid).thenApplyAsync(
				PlayerReply::getPlayer
		);
	}

	private Scene createScene() {
		try {
			Parent root = FXMLLoader.load(TrophyFishPlugin.class.getResource("/scene/HOTM.fxml"));
			Scene scene = new Scene(root, 900, 630);

			// 设置背景
			Image background = ImageUtil.getImageBackground(900, 560);
			BackgroundImage backgroundImage = new BackgroundImage(background, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
			HBox hBox = (HBox) scene.lookup("#Background");
			hBox.setBackground(new Background(backgroundImage));
			return scene;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void updateImageViewAndText(Scene scene, JSONObject treeData, String dataKey, String imageMiddlePath, int maxValue) {
		ImageView imageView = (ImageView) scene.lookup("#" + dataKey);
		Text text = (Text) scene.lookup("#" + dataKey + "_1");
		URL imageURL;
		if (treeData.containsKey(dataKey)) {
			int dataValue = treeData.getIntValue(dataKey);
			if (dataValue == maxValue) {
				imageURL = HOTMPlugin.class.getResource(IMAGE_PATH_PREFIX + imageMiddlePath + "_complete" + IMAGE_PATH_SUFFIX);
				text.setText(maxValue == 1 ? "✔" : String.valueOf(maxValue));
				text.setFill(Color.GREEN);
			} else {
				String progressOrIncomplete = maxValue == 1 ? "incomplete" : "progress";
				imageURL = HOTMPlugin.class.getResource(IMAGE_PATH_PREFIX + imageMiddlePath + "_" + progressOrIncomplete + IMAGE_PATH_SUFFIX);
				text.setText(dataValue + "/" + maxValue);
			}
		} else {
			imageURL = HOTMPlugin.class.getResource(IMAGE_PATH_PREFIX + imageMiddlePath + "_incomplete" + IMAGE_PATH_SUFFIX);
			text.setText("✖");
			text.setFill(Color.RED);
		}
		Image scaledImage = ImageUtil.scaleImage(new Image(imageURL.toString()), 64, 64);
		imageView.setImage(scaledImage);
	}
}
