package com.catand.skyblockhelper.plugins;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.catand.skyblockhelper.ErrorProcessor;
import com.catand.skyblockhelper.utils.HypixelAPIUtil;
import com.catand.skyblockhelper.utils.ImageUtil;
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
import javafx.scene.chart.PieChart;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.util.ResourceType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Component
public class MayorPlugin extends BotPlugin {

	@Override
	public int onGroupMessage(Bot bot, GroupMessageEvent event) {
		//格式化消息获取参数
		String messageRaw = event.getRawMessage();
		if (!messageRaw.contains("/")) {
			return MESSAGE_IGNORE;
		}
		messageRaw = messageRaw.split("/")[1];
		if (!messageRaw.startsWith("市长")) {
			return MESSAGE_IGNORE;
		}
		MsgUtils sendMsg = MsgUtils.builder();
		try {
			final Scene[] scene = new Scene[1];
			CompletableFuture<JSONObject> future1 = MayorPlugin.this.fetchMayorData();
			CompletableFuture<Void> future2 = CompletableFuture.supplyAsync(() -> {
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

			future1.exceptionally(throwable -> {
				throw new RuntimeException(throwable);
			});
			future2.exceptionally(throwable -> {
				throw new RuntimeException(throwable);
			});
			CompletableFuture.allOf(future1, future2).join();

			// 处理数据
			JSONObject jsonObject = future1.get();
			JSONObject mayorData = jsonObject.getJSONObject("mayor");
			JSONObject electionData = jsonObject.getJSONObject("current");
			if (electionData == null) {
				scene[0] = createNoElectionScene();
			}
			PieChart pieChart = (PieChart) scene[0].lookup("#pieChart");

			// 当前市长
			String mayorName = mayorData.getString("name");
			Text mayorNameText = (Text) scene[0].lookup("#mayorName0");
			mayorNameText.setText(mayorName);

			ImageView mayorImage = (ImageView) scene[0].lookup("#mayorImage0");
			URL mayorImageUrl = MayorPlugin.class.getResource("/assets/mayor/" + mayorName + ".png");
			mayorImage.setImage(new Image(mayorImageUrl.toString()));

			ImageView mayorIcon = (ImageView) scene[0].lookup("#mayorIcon0");
			URL mayorIconUrl = MayorPlugin.class.getResource("/assets/mayor/" + mayorName + "_Sprite.png");
			Image scaledImage = ImageUtil.scaleImage(new Image(mayorIconUrl.toString()), 40, 40);
			mayorIcon.setImage(scaledImage);

			JSONArray perks = mayorData.getJSONArray("perks");
			perks.stream().forEach(perk -> {
				JSONObject perkData = (JSONObject) perk;
				String perkName = getMayorPerkName(perkData.getString("name"));
				Text text = (Text) scene[0].lookup("#mayorPerk0" + (perks.indexOf(perk) + 1));
				text.setText(perkName);
			});
			for (int i = perks.size() + 1; i < 5; i++) {
				Text text = (Text) scene[0].lookup("#mayorPerk0" + (i));
				text.setText("");
			}

			// 当前选举
			if (electionData != null) {
				// 年份
				int year = electionData.getIntValue("year");
				Text yearText = (Text) scene[0].lookup("#electionYear");
				yearText.setText(String.valueOf(year));

				JSONArray candidates = electionData.getJSONArray("candidates");
				// 总票数
				int totalVotes = candidates.stream().mapToInt(candidate -> ((JSONObject) candidate).getIntValue("votes")).sum();
				candidates.stream().forEach(candidate -> {
					JSONObject candidateData = (JSONObject) candidate;
					int index = candidates.indexOf(candidate) + 1;

					// 名字
					String name = getMayorPerkName(candidateData.getString("name"));
					Text text1 = (Text) scene[0].lookup("#mayorName" + index);
					text1.setText(name);

					// 得票
					int votes = candidateData.getIntValue("votes");
					pieChart.getData().add(new PieChart.Data(name, votes));
					ProgressBar progressBar = (ProgressBar) scene[0].lookup("#mayorProgress" + index);
					progressBar.setProgress(votes / (double) totalVotes);
					Text percentText = (Text) scene[0].lookup("#mayorPercent" + index);
					percentText.setText(String.format("%.2f%%", votes / (double) totalVotes * 100));

					// 图片
					ImageView imageView = (ImageView) scene[0].lookup("#mayorImage" + index);
					URL imageUrl = MayorPlugin.class.getResource("/assets/mayor/" + name + ".png");
					imageView.setImage(new Image(imageUrl.toString()));

					// 头像
					ImageView iconView = (ImageView) scene[0].lookup("#mayorIcon" + index);
					URL iconUrl = MayorPlugin.class.getResource("/assets/mayor/" + name + "_Sprite.png");
					Image scaledImage1 = ImageUtil.scaleImage(new Image(iconUrl.toString()), 40, 40);
					iconView.setImage(scaledImage1);

					// 特权
					JSONArray perks1 = candidateData.getJSONArray("perks");
					perks1.stream().forEach(perk -> {
						JSONObject perkData = (JSONObject) perk;
						String perkName = getMayorPerkName(perkData.getString("name"));
						Text text = (Text) scene[0].lookup("#mayorPerk" + index + (perks1.indexOf(perk) + 1));
						text.setText(perkName);
					});
					for (int i = perks1.size() + 1; i < 5; i++) {
						Text text = (Text) scene[0].lookup("#mayorPerk" + index + (i));
						text.setText("");
					}
				});
			} else {
				int year = mayorData.getJSONObject("election").getIntValue("year");
				Text yearText = (Text) scene[0].lookup("#year");
				yearText.setText(String.valueOf(year));
			}

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

	private CompletableFuture<JSONObject> fetchMayorData() {
		HypixelAPI hypixelAPI = HypixelAPIUtil.getHypixelAPI();
		return hypixelAPI.getResource(ResourceType.SKYBLOCK_ELECTION).thenApply(
				resourceReply -> {
					Gson gson = new Gson();
					String jsonString = gson.toJson(resourceReply.getResponse());
					return JSONObject.parseObject(jsonString);
				}
		);
	}

	private Scene createScene() {
		try {
			Parent root = FXMLLoader.load(MayorPlugin.class.getResource("/scene/Mayor.fxml"));
			Scene scene = new Scene(root, 900, 770);

			// 设置背景
			Image background = ImageUtil.getImageBackground(900, 700);
			BackgroundImage backgroundImage = new BackgroundImage(background, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
			HBox hBox = (HBox) scene.lookup("#Background");
			hBox.setBackground(new Background(backgroundImage));
			return scene;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Scene createNoElectionScene() {
		try {
			Parent root = FXMLLoader.load(MayorPlugin.class.getResource("/scene/MayorNoElection.fxml"));
			Scene scene = new Scene(root, 500, 770);

			// 设置背景
			Image background = ImageUtil.getImageBackground(500, 700);
			BackgroundImage backgroundImage = new BackgroundImage(background, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
			HBox hBox = (HBox) scene.lookup("#Background");
			hBox.setBackground(new Background(backgroundImage));
			return scene;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String getMayorPerkName(String perkName) {
		return switch (perkName) {
			case "SLASHED Pricing" -> "猎手任务半价";
			case "Slayer XP Buff" -> "+25%猎手经验";
			case "Pathfinder" -> "+20%稀有掉落";
			case "Prospection" -> "挖矿小人加快";
			case "Mining XP Buff" -> "+60%挖矿经验";
			case "Mining Fiesta" -> "挖矿节";
			case "Lucky!" -> "+25宠物幸运";
			case "Mythological Ritual" -> "挖宝节";
			case "Pet XP Buff" -> "+35%宠物经验";
			case "Barrier Street" -> "+25%银行利息";
			case "Shopping Spree" -> "x10商店上限";
			case "Farming Simulator" -> "+25%锄头计数";
			case "Pelt-pocalypse" -> "+150%皮毛掉落";
			case "GOATed" -> "+10%农业竞赛";
			case "Sweet Tooth" -> "+20%糖果掉落";
			case "Benevolence" -> "+125%礼物掉落";
			case "Extra Event" -> "额外节日";
			case "Fishing XP Buff" -> "+50%钓鱼经验";
			case "Luck of the Sea 2.0" -> "+15海怪几率";
			case "Fishing Festival" -> "钓鱼节";
			case "Marauder" -> "-20%开箱价格";
			case "EZPZ" -> "地牢+10分";
			case "Benediction" -> "+25%祝福强度";
			case "TURBO MINIONS!!!" -> "小人产能翻倍";
			case "AH CLOSED!!!" -> "关闭AH";
			case "DOUBLE MOBS HP!!!" -> "怪物双倍生命";
			case "MOAR SKILLZ!!!" -> "+50%经验获取";
			case "Perkpocalypse" -> "市长大银趴";
			case "Statspocalypse" -> "+10%所有属性";
			case "Jerrypocalypse" -> "Jerry盒子";
			case "Bribe" -> "拿好你的脏钱\uD83D\uDE20";
			case "Darker Auctions" -> "超级黑市";
			default -> perkName;
		};
	}
}