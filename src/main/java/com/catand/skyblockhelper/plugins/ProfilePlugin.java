package com.catand.skyblockhelper.plugins;

import com.alibaba.fastjson2.JSONObject;
import com.catand.skyblockhelper.ErrorProcessor;
import com.catand.skyblockhelper.data.Gamemode;
import com.catand.skyblockhelper.Player;
import com.catand.skyblockhelper.utils.ProfileUtil;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.stereotype.Component;

@Component
public class ProfilePlugin extends BotPlugin {
	MsgUtils sendMsg;

	@Override
	public int onGroupMessage(Bot bot, GroupMessageEvent event) {
		//格式化消息获取参数
		String messageRaw = event.getRawMessage();
		if (!messageRaw.contains("/")) {
			return MESSAGE_IGNORE;
		}
		messageRaw = messageRaw.split("/")[1];
		if (!messageRaw.startsWith("档案")) {
			return MESSAGE_IGNORE;
		}
		String[] args = messageRaw.split(" ");
		if (args.length < 2) {
			sendMsg = MsgUtils.builder().text("参数错误，\n正确格式：/档案 <玩家名>");
			bot.sendGroupMsg(event.getGroupId(), event.getUserId(), sendMsg.build(), false);
			return MESSAGE_BLOCK;
		}

		//获取networth
		try {
			sendMsg = MsgUtils.builder();
			Player player = new Player(args[1]);

			sendMsg.text(ProfileUtil.getDisplayNameData(player.getMainProfile()) + "的档案:\n当前档案:" + "[" + ProfileUtil.getSkyblockLevel(player.getMainProfile()) + "]" + player.getMainProfile().getString("cute_name") + Gamemode.getGamemode(player.getMainProfile()).getIcon() + "\n其他档案:\n");
			int counter = 0;
			for (JSONObject profile : player.getProfileList()) {
				if (counter == 0) {
					counter++;
					continue;
				}
				sendMsg.text("[" + ProfileUtil.getSkyblockLevel(profile) + "]" + profile.getString("cute_name") + Gamemode.getGamemode(profile).getIcon() + "\n");
			}

			bot.sendGroupMsg(event.getGroupId(), event.getUserId(), sendMsg.build(), false);
		} catch (Exception e) {
			new ErrorProcessor(e, bot, event);
		}
		return MESSAGE_BLOCK;
	}
}