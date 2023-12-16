package com.catand.skyblockhelper.plugins;

import com.alibaba.fastjson2.JSONObject;
import com.catand.skyblockhelper.ErrorProcessor;
import com.catand.skyblockhelper.Player;
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
			sendMsg = MsgUtils.builder().text("参数错误，\n正确格式：/奖杯鱼 <玩家名>");
			bot.sendGroupMsg(event.getGroupId(), sendMsg.build(), false);
			return MESSAGE_BLOCK;
		}
		sendMsg = MsgUtils.builder();

		try {
			Player player = new Player(args[1]);
			JSONObject trophyFishData = ProfileUtil.getCrimsonIsleData(player.getMainProfile()).getJSONObject("trophy_fish");
			int totalCaught = trophyFishData.getIntValue("total_caught");
			ArrayList<JSONObject> trophyFishList = (ArrayList<JSONObject>) trophyFishData.getJSONArray("fish").toJavaList(JSONObject.class);
			trophyFishList.removeIf(fish -> !fish.containsKey("highest_tier"));

			sendMsg = sendMsg.text(ProfileUtil.getDisplayNameData(player.getMainProfile()) + "在" + ProfileUtil.getProfileName(player.getMainProfile()) + "的奖杯鱼:\n" +
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

			bot.sendGroupMsg(event.getGroupId(), sendMsg.build(), false);
		} catch (Exception e) {
			new ErrorProcessor(e, bot, event);
		}
		return MESSAGE_BLOCK;
	}
}
