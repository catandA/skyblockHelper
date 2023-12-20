package com.catand.skyblockhelper.plugins;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.stereotype.Component;

@Component
public class NoSuchCommandPlugin extends BotPlugin {
	MsgUtils sendMsg;

	@Override
	public int onGroupMessage(Bot bot, GroupMessageEvent event) {
		//格式化消息获取参数
		String messageRaw = event.getRawMessage();
		if (!messageRaw.contains("/")) {
			sendMsg = MsgUtils.builder().text("请在指令前添加/");
			bot.sendGroupMsg(event.getGroupId(), event.getUserId(), sendMsg.build(), false);
			return MESSAGE_BLOCK;
		}
		messageRaw = messageRaw.split("/")[1];
		String[] args = messageRaw.split(" ");
		sendMsg = MsgUtils.builder().text("不存在名为\"" + args[0] + "\"的指令，输入/help查看帮助");
		bot.sendGroupMsg(event.getGroupId(), event.getUserId(), sendMsg.build(), false);
		return MESSAGE_BLOCK;
	}
}
