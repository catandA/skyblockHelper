package com.catand.skyblockhelper.plugins;

import com.alibaba.fastjson2.JSONObject;
import com.catand.skyblockhelper.ErrorProcessor;
import com.catand.skyblockhelper.data.Gamemode;
import com.catand.skyblockhelper.Player;
import com.catand.skyblockhelper.utils.NumberFormatUtil;
import com.catand.skyblockhelper.utils.ProfileUtil;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.stereotype.Component;

@Component
public class SlayerPlugin extends BotPlugin {
	MsgUtils sendMsg;

	@Override
	public int onGroupMessage(Bot bot, GroupMessageEvent event) {
		//格式化消息获取参数
		String messageRaw = event.getRawMessage();
		if (!messageRaw.contains("/")) {
			return MESSAGE_IGNORE;
		}
		messageRaw = messageRaw.split("/")[1];
		if (!messageRaw.startsWith("杀手")) {
			return MESSAGE_IGNORE;
		}
		String[] args = messageRaw.split(" ");
		if (args.length < 2) {
			sendMsg = MsgUtils.builder().text("参数错误，\n正确格式：/杀手 <玩家名>");
			bot.sendGroupMsg(event.getGroupId(), event.getUserId(), sendMsg.build(), false);
			return MESSAGE_BLOCK;
		}
		sendMsg = MsgUtils.builder();

		try {
			Player player = new Player(args[1]);
			JSONObject slayerData = ProfileUtil.getSlayerData(player.getMainProfile());
			long totalSlayerXp = slayerData.getLong("total_slayer_xp");
			JSONObject slayersData = slayerData.getJSONObject("slayers");


			sendMsg = sendMsg.text(ProfileUtil.getDisplayNameData(player.getMainProfile()) + "在" + "[" + ProfileUtil.getSkyblockLevel(player.getMainProfile()) + "]" + ProfileUtil.getProfileName(player.getMainProfile()) + Gamemode.getGamemode(player.getMainProfile()).getIcon() + "的杀手:\n" +
					"总经验:" + NumberFormatUtil.format(totalSlayerXp) + "\n");

			String[] keys = slayersData.keySet().toArray(new String[0]);
			for (int i = 0; i < keys.length; i++) {
				String key = keys[i];
				String value = slayersData.getJSONObject(key).getJSONObject("level").getIntValue("currentLevel") + "";
				switch (key) {
					case "zombie" -> key = "僵尸";
					case "spider" -> key = "蜘蛛";
					case "wolf" -> key = "狼";
					case "enderman" -> key = "末影人";
					case "blaze" -> key = "烈焰人";
					case "vampire" -> key = "血族";
				}
				sendMsg.text(key + ":\t" + value);
				if ((i + 1) % 3 == 0) {
					sendMsg.text("\n");
				} else {
					sendMsg.text("\t");
				}
			}
			bot.sendGroupMsg(event.getGroupId(), event.getUserId(), sendMsg.build(), false);
		} catch (Exception e) {
			new ErrorProcessor(e, bot, event);
		}
		return MESSAGE_BLOCK;
	}
}
