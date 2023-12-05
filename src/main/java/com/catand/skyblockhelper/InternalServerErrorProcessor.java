package com.catand.skyblockhelper;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpServerErrorException;

@Slf4j
public class InternalServerErrorProcessor {
	MsgUtils sendMsg;
	Bot bot;
	GroupMessageEvent event;
	HttpServerErrorException.InternalServerError exception;

	public InternalServerErrorProcessor(HttpServerErrorException.InternalServerError exception,Bot bot, GroupMessageEvent event) {
		this.bot = bot;
		this.event = event;
		this.exception = exception;
		log.error("Internal Server Error: " + exception.getMessage());
		if (exception.getMessage().contains("No user with the name")) {
			sendMsg = MsgUtils.builder().text("玩家不存在");
		}
		if (exception.getMessage().contains("undefined")) {
			sendMsg = MsgUtils.builder().text("你的数据目前被Hypixel打乱了\n暂时查不出来\n(￣ε(#￣)☆╰╮(￣▽￣///)\n经SkyCrypt确认，此问题由Hypixel更新skyblock数据格式引起\n在数据迁移未完成期间上号会导致此问题\n得等Hypixel修它的API");
		}
		if (exception.getMessage().contains("404 Not Found")) {
			sendMsg = MsgUtils.builder().text("参数格式打错了\n正确格式：/networth <玩家名>");
		}
		bot.sendGroupMsg(event.getGroupId(), sendMsg.build(), false);
	}
}
