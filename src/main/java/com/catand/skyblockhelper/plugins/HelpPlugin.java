package com.catand.skyblockhelper.plugins;

import com.catand.skyblockhelper.SkyblockHelperApplication;
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
		bot.sendGroupMsg(event.getGroupId(), "skyblockHelper v" + SkyblockHelperApplication.VERSION +
				"\ndeveloped by catand\n\n" +
				"/帮助 /身价 /护符补全 /技能\n" +
				"/地牢 /奖杯鱼 /杀手\n" +
				"/山心 /档案 /宾果\n" +
				"Bot交流群:180901798", false);
		return MESSAGE_BLOCK;
	}

}
