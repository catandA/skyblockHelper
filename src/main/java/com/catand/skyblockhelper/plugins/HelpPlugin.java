package com.catand.skyblockhelper.plugins;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import org.springframework.stereotype.Component;

@Component
public class HelpPlugin extends BotPlugin {
	@Override
	public int onAnyMessage(Bot bot, AnyMessageEvent event) {
		if (event.getRawMessage().contains("帮助")) {
			bot.sendMsg(event, "skyblock机器人\n还在开发", false);
		}
		return MESSAGE_IGNORE;
	}

}
