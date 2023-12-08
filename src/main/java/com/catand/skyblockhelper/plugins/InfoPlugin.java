package com.catand.skyblockhelper.plugins;

import com.catand.skyblockhelper.SkyblockHelperApplication;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.stereotype.Component;

@Component
public class InfoPlugin extends BotPlugin {
	@Override
	public int onGroupMessage(Bot bot, GroupMessageEvent event) {
		String messageRaw = event.getRawMessage();
		if (!messageRaw.contains("/")) {
			return MESSAGE_IGNORE;
		}
		messageRaw = messageRaw.split("/")[1];
		if (!messageRaw.startsWith("信息")) {
			return MESSAGE_IGNORE;
		}
		bot.sendGroupMsg(event.getGroupId(), "skyblockHelper v" + SkyblockHelperApplication.VERSION, false);
		return MESSAGE_BLOCK;
	}
}
