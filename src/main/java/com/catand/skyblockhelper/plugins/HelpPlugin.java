package com.catand.skyblockhelper.plugins;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.stereotype.Component;

@Component
public class HelpPlugin extends BotPlugin {
	@Override
	public int onGroupMessage(Bot bot, GroupMessageEvent event) {
		String messageRaw = event.getRawMessage();
		if (!messageRaw.contains("/")) {
			return MESSAGE_IGNORE;
		}
		messageRaw = messageRaw.split("/")[1];
		if (!messageRaw.startsWith("帮助")) {
			return MESSAGE_IGNORE;
		}
		bot.sendGroupMsg(event.getGroupId(), "/帮助 查看机器人指令列表" +
				"\n/身价 查询指定玩家身价信息" +
				"\n/技能 查询指定玩家技能信息" +
				"\n/信息 查看SkyblockHelper基本信息", false);
		return MESSAGE_BLOCK;
	}

}
