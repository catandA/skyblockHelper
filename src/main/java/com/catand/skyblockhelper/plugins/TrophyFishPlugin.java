package com.catand.skyblockhelper.plugins;

import com.alibaba.fastjson2.JSONObject;
import com.catand.skyblockhelper.ErrorProcessor;
import com.catand.skyblockhelper.data.Gamemode;
import com.catand.skyblockhelper.data.SkyblockLevelColorCode;
import com.catand.skyblockhelper.data.SkyblockProfile;
import com.catand.skyblockhelper.utils.ImageUtil;
import com.catand.skyblockhelper.utils.JavaFXUtils;
import com.catand.skyblockhelper.utils.ProfileUtil;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
		String[] args = messageRaw.split(" ");
		if (args.length < 2) {
			bot.sendGroupMsg(event.getGroupId(), event.getUserId(), MsgUtils.builder().text("参数错误，\n正确格式：/奖杯鱼 <玩家名> [档案名]").build(), false);
			return MESSAGE_BLOCK;
		}
		SkycryptMessageHandler handler;
		if (args.length > 2) {
			handler = new SkycryptMessageHandler(args[1], args[2]);
		} else {
			handler = new SkycryptMessageHandler(args[1]);
		}
		try {
			handler.handleGroupMessage();
			final Scene[] scene = new Scene[1];
			CompletableFuture<Void> future3 = handler.getFuture3();
			CompletableFuture<Void> future3ThenRun = future3.thenRunAsync(() -> {
				scene[0] = handler.getScene();
				// 设置鱼的图像
				setBasicFishImage(scene[0], "blobfish");
				setBasicFishImage(scene[0], "flyfish");
				setBasicFishImage(scene[0], "golden_fish");
				setBasicFishImage(scene[0], "gusher");
				setBasicFishImage(scene[0], "karate_fish");
				setBasicFishImage(scene[0], "lavahorse");
				setBasicFishImage(scene[0], "mana_ray");
				setBasicFishImage(scene[0], "moldfin");
				setBasicFishImage(scene[0], "obfuscated_1_fish");
				setBasicFishImage(scene[0], "obfuscated_2_fish");
				setBasicFishImage(scene[0], "obfuscated_3_fish");
				setBasicFishImage(scene[0], "skeleton_fish");
				setBasicFishImage(scene[0], "slugfish");
				setBasicFishImage(scene[0], "soul_fish");
				setBasicFishImage(scene[0], "steaming_hot_flounder");
				setBasicFishImage(scene[0], "sulphur_skitter");
				setBasicFishImage(scene[0], "vanille");
				setBasicFishImage(scene[0], "volcanic_stonefish");
			});
			CompletableFuture.allOf(handler.getFuture1(), handler.getFuture2(), future3ThenRun).join();


			// 设置基本信息
			String profileName = handler.getProfileName();
			JSONObject profile = handler.getProfile();
			Image skinImage = handler.getSkinImage();

			Text playerNameText = (Text) scene[0].lookup("#IGN");
			playerNameText.setText(handler.getPlayer().getName());
			Text profileNameText = (Text) scene[0].lookup("#ProfileName");
			profileNameText.setText(profileName);
			Text profileIconText = (Text) scene[0].lookup("#ProfileIcon");
			profileIconText.setText(SkyblockProfile.getProfile(profileName).getEmoji()[0]);
			Text skyblockLevelText = (Text) scene[0].lookup("#ProfileLevel");
			int skyblockLevel = ProfileUtil.getSkyblockLevel(profile);
			skyblockLevelText.setText(String.valueOf(skyblockLevel));
			Color skyblockLevelColor = JavaFXUtils.AWTColorToJavaFXColor(SkyblockLevelColorCode.getLevelColor(skyblockLevel).getColor());
			skyblockLevelText.setFill(skyblockLevelColor);
			Text gamemodeText = (Text) scene[0].lookup("#Gamemode");
			gamemodeText.setText(Gamemode.getGamemode(profile).getChineseName());
			Text gamemodeIconText = (Text) scene[0].lookup("#GamemodeIcon");
			gamemodeIconText.setText(Gamemode.getGamemode(profile).getIcon());

			ImageView skinImageView = (ImageView) scene[0].lookup("#SkinImage");
			skinImageView.setImage(skinImage);

			// 构造基本奖杯鱼数据
			JSONObject trophyFishData = ProfileUtil.getCrimsonIsleData(profile).getJSONObject("trophy_fish");
			int totalCaught = trophyFishData.getIntValue("total_caught");
			Text totalCaughtText = (Text) scene[0].lookup("#TotalFish");
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
				HBox hbox = (HBox) scene[0].lookup("#" + name);
				String css = "-fx-background-color: rgba(30, 30, 30, 0.784); -fx-background-radius: 20; -fx-border-radius: 15; -fx-border-width: 5; -fx-border-color: ";
				switch (tier) {
					case "bronze" -> hbox.setStyle(css + "#964a0c" + ";");
					case "silver" -> hbox.setStyle(css + "#757f89" + ";");
					case "gold" -> hbox.setStyle(css + "#e49b23" + ";");
					case "diamond" -> hbox.setStyle(css + "#1cffff" + ";");
				}

				// 设置鱼的图像
				ImageView imageView = (ImageView) scene[0].lookup("#" + name + "Image");
				Image scaledImage = ImageUtil.scaleImage(getFishImage(name, tier), 48, 48);
				imageView.setImage(scaledImage);

				// 设置各等级捕获次数
				Text text = (Text) scene[0].lookup("#" + name + "_bronze");
				text.setText(String.valueOf(trophyFish.getIntValue("bronze")));
				text = (Text) scene[0].lookup("#" + name + "_silver");
				text.setText(String.valueOf(trophyFish.getIntValue("silver")));
				text = (Text) scene[0].lookup("#" + name + "_gold");
				text.setText(String.valueOf(trophyFish.getIntValue("gold")));
				text = (Text) scene[0].lookup("#" + name + "_diamond");
				text.setText(String.valueOf(trophyFish.getIntValue("diamond")));
			});
			handler.setScene(scene[0]);
			handler.sendAsImage(bot, event);
		} catch (Exception e) {
			new ErrorProcessor(e, bot, event);
		}

		return MESSAGE_BLOCK;
	}

	public static Image getFishImage(String name, String tier) {
		return new Image("assets/trophy_fish/" + name + "/" + name + "_" + tier + ".png");
	}

	public static void setBasicFishImage(Scene scene, String name) {
		ImageView imageView = (ImageView) scene.lookup("#" + name + "Image");
		Image scaledImage = ImageUtil.scaleImage(getFishImage(name, "bronze"), 48, 48);
		imageView.setImage(scaledImage);
	}
}
