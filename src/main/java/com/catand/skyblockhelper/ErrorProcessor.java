package com.catand.skyblockhelper;

import com.alibaba.fastjson2.JSONObject;
import com.catand.skyblockhelper.data.Gamemode;
import com.catand.skyblockhelper.exception.NoSuchProfileException;
import com.catand.skyblockhelper.utils.ProfileUtil;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@Slf4j
public class ErrorProcessor {

	public ErrorProcessor(Throwable exception, Bot bot, GroupMessageEvent event, Player player) {
		MsgUtils sendMsg = MsgUtils.builder();
		exception.printStackTrace();
		exception = getRootCause(exception);
		if (exception instanceof HttpServerErrorException.InternalServerError) {
			if (exception.getMessage().contains("No user with the name")) {
				sendMsg = sendMsg.text("玩家不存在");
			} else if (exception.getMessage().contains("undefined")) {
				sendMsg = sendMsg.text("你的数据目前被Hypixel打乱了\n暂时查不出来\n(￣ε(#￣)☆╰╮(￣▽￣///)\n经SkyCrypt确认，此问题由Hypixel更新skyblock数据格式引起\n在数据迁移未完成期间上号会导致此问题\n得等Hypixel修它的API");
			} else if (exception.getMessage().contains("404 Not Found")) {
				sendMsg = sendMsg.text("参数格式打错了\n正确格式：/指令 <玩家名> [档案名]");
			} else if (exception.getMessage().contains("Player has no SkyBlock profiles")) {
				sendMsg = sendMsg.text("wiped. R.I.P");
			} else if (exception.getMessage().contains("Failed resolving username")) {
				sendMsg = sendMsg.text("玩家不存在");
			} else if (exception.getMessage().contains("Invalid format for the UUID")) {
				sendMsg = sendMsg.text("非法ID格式");
			} else {
				sendMsg = sendMsg.text("未知错误,爆!");
			}
		} else if (exception instanceof ResourceAccessException) {
			sendMsg = sendMsg.text("网络错误,再来一次!");
		} else if (exception instanceof HttpServerErrorException.GatewayTimeout) {
			sendMsg = sendMsg.text("网络错误,再来一次!");
		} else if (exception instanceof NullPointerException) {
			sendMsg = sendMsg.text("经典空指针错误 :( 我先爆了");
		} else if (exception instanceof NoSuchProfileException noSuchProfileException) {
			sendMsg = MsgUtils.builder().text("俺没瞅见" + noSuchProfileException.getPlayer().getName() + "有个啥" + noSuchProfileException.getProfileName() + "啊\n俺只知道他有这些:\n");
			for (JSONObject profile2 : player.getProfileList()) {
				sendMsg.text("[" + ProfileUtil.getSkyblockLevel(profile2) + "]" + profile2.getString("cute_name") + Gamemode.getGamemode(profile2).getIcon() + "\n");
			}
		} else if (exception instanceof ArrayIndexOutOfBoundsException) {
			sendMsg = sendMsg.text("未知的数组越界,爆!");
		} else {
			sendMsg = sendMsg.text("未知错误,爆!");
		}
		log.error("Error: " + exception.getMessage());

		bot.sendGroupMsg(event.getGroupId(), event.getUserId(), sendMsg.build(), false);
	}

	public ErrorProcessor(Throwable exception, Bot bot, GroupMessageEvent event) {
		this(exception, bot, event, null);
	}

	private Throwable getRootCause(Throwable throwable) {
		Throwable cause;
		while ((cause = throwable.getCause()) != null) {
			throwable = cause;
		}
		return throwable;
	}
}
