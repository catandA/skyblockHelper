package com.catand.skyblockhelper.plugins;

import com.alibaba.fastjson2.JSONObject;
import com.catand.skyblockhelper.ErrorProcessor;
import com.catand.skyblockhelper.data.Gamemode;
import com.catand.skyblockhelper.Player;
import com.catand.skyblockhelper.data.SkyblockProfile;
import com.catand.skyblockhelper.utils.ProfileUtil;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class TrophyFishPlugin extends BotPlugin {
	MsgUtils sendMsg;

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
		sendMsg = MsgUtils.builder();
		JSONObject profile;
		String playerName;

		try {
			Player player = new Player(args[1]);
			profile = player.getMainProfile();
			playerName = ProfileUtil.getDisplayNameData(profile);
			if (args.length > 2) {
				// 如果指定了存档名，那么就获取指定存档的数据
				String profileName = SkyblockProfile.getProfile(args[2]).getJsonName();
				JSONObject profile1 = player.getProfile(profileName);
				// 未找到指定存档
				if (profile1 == null) {
					sendMsg = MsgUtils.builder().text("俺没瞅见" + playerName + "有个啥" + profileName + "啊\n俺只知道他有这些:\n");
					for (JSONObject profile2 : player.getProfileList()) {
						sendMsg.text("[" + ProfileUtil.getSkyblockLevel(profile2) + "]" + profile2.getString("cute_name") + Gamemode.getGamemode(profile2).getIcon() + "\n");
					}
					bot.sendGroupMsg(event.getGroupId(), event.getUserId(), sendMsg.build(), false);
					return MESSAGE_BLOCK;
				}
				profile = profile1;
			}

			JSONObject trophyFishData = ProfileUtil.getCrimsonIsleData(profile).getJSONObject("trophy_fish");
			int totalCaught = trophyFishData.getIntValue("total_caught");
			ArrayList<JSONObject> trophyFishList = (ArrayList<JSONObject>) trophyFishData.getJSONArray("fish").toJavaList(JSONObject.class);
			trophyFishList.removeIf(fish -> !fish.containsKey("highest_tier"));

			sendMsg = sendMsg.text(ProfileUtil.getDisplayNameData(profile) + "在" + "[" + ProfileUtil.getSkyblockLevel(profile) + "]" + ProfileUtil.getProfileName(profile) + Gamemode.getGamemode(profile).getIcon() + "的奖杯鱼:\n" +
					"总计:" + totalCaught + "\n");

			final int[] counter = {0};
			trophyFishList.forEach(trophyFIsh -> {
				String tier = trophyFIsh.getString("highest_tier");
				switch (tier) {
					case "bronze" -> tier = "铜\uD83E\uDD49";
					case "silver" -> tier = "银\uD83E\uDD48";
					case "gold" -> tier = "金\uD83E\uDD47";
					case "diamond" -> tier = "钻石\uD83D\uDC8E";
				}
				sendMsg.text(trophyFIsh.getString("display_name") + ":\t" + tier);
				counter[0]++;
				if (counter[0] % 2 == 0) {
					sendMsg.text("\n");
				} else {
					sendMsg.text("\t");
				}
			});

			bot.sendGroupMsg(event.getGroupId(), event.getUserId(), sendMsg.build(), false);
		} catch (Exception e) {
			new ErrorProcessor(e, bot, event);
		}
		return MESSAGE_BLOCK;
	}
}
