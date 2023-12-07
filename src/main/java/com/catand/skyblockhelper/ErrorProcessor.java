package com.catand.skyblockhelper;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@Slf4j
public class ErrorProcessor {
	MsgUtils sendMsg;
	Bot bot;
	GroupMessageEvent event;
	Exception exception;

	public ErrorProcessor(Exception exception, Bot bot, GroupMessageEvent event) {
		this.bot = bot;
		this.event = event;
		this.exception = exception;
		if (exception instanceof HttpServerErrorException.InternalServerError) {
			if (exception.getMessage().contains("No user with the name")) {
				sendMsg = MsgUtils.builder().text("玩家不存在");
			} else if (exception.getMessage().contains("undefined")) {
				sendMsg = MsgUtils.builder().text("你的数据目前被Hypixel打乱了\n暂时查不出来\n(￣ε(#￣)☆╰╮(￣▽￣///)\n经SkyCrypt确认，此问题由Hypixel更新skyblock数据格式引起\n在数据迁移未完成期间上号会导致此问题\n得等Hypixel修它的API");
			} else if (exception.getMessage().contains("404 Not Found")) {
				sendMsg = MsgUtils.builder().text("参数格式打错了\n正确格式：/networth <玩家名>");
			}else {
				sendMsg = MsgUtils.builder().text("未知错误,爆!");
			}
		}else if (exception instanceof ResourceAccessException) {
			sendMsg = MsgUtils.builder().text("网络错误,再来一次!");
		}else {
			sendMsg = MsgUtils.builder().text("未知错误,爆!");
		}
		log.error("Error: " + exception.getMessage());

		bot.sendGroupMsg(event.getGroupId(), sendMsg.build(), false);
	}
}
