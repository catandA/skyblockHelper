package com.catand.skyblockhelper.plugins;

import com.alibaba.fastjson2.JSONObject;
import com.catand.skyblockhelper.Player;
import com.catand.skyblockhelper.utils.ProfileUtil;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

import java.text.DecimalFormat;

@Component
public class NetworthPlugin extends BotPlugin {
	MsgUtils sendMsg;

	@Override
	public int onGroupMessage(Bot bot, GroupMessageEvent event) {
		//格式化消息获取参数
		String messageRaw = event.getRawMessage();
		messageRaw = messageRaw.split("/")[1];
		if (!messageRaw.contains("networth")) {
			return MESSAGE_IGNORE;
		}
		String[] args = messageRaw.split(" ");
		if (args.length < 2) {
			sendMsg = MsgUtils.builder().text("参数错误，\n正确格式：/networth <玩家名>");
			bot.sendGroupMsg(event.getGroupId(), sendMsg.build(), false);
			return MESSAGE_BLOCK;
		}

		//获取networth
		try {
			Player player = new Player(args[1]);
			player.refreshProfileList();
			JSONObject networthData = ProfileUtil.getNetworthData(player.getMainProfile());
			DecimalFormat decimalFormat = new DecimalFormat(",###");
			sendMsg = MsgUtils.builder().text(player.name + ":\n"
					+ "Networth: " + decimalFormat.format((int) networthData.getDoubleValue("networth")));
			String[] keys = networthData.getJSONObject("types").keySet().toArray(new String[0]);
			for (String key : keys) {
				sendMsg.text("\n" + key + ": " + decimalFormat.format((int) networthData.getJSONObject("types").getJSONObject(key).getDoubleValue("total")));
			}
			bot.sendGroupMsg(event.getGroupId(), sendMsg.build(), false);
		} catch (HttpServerErrorException.InternalServerError e) {
			e.printStackTrace();
			if (e.getMessage().contains("No user with the name")) {
				sendMsg = MsgUtils.builder().text("玩家不存在");
				bot.sendGroupMsg(event.getGroupId(), sendMsg.build(), false);
				return MESSAGE_BLOCK;
			}
			if (e.getMessage().contains("undefined")) {
				sendMsg = MsgUtils.builder().text("你的数据目前被Hypixel打乱了\n暂时查不出来\n(￣ε(#￣)☆╰╮(￣▽￣///)\n经SkyCrypt确认，此问题由Hypixel更新skyblock数据格式引起\n在数据迁移未完成期间上号会导致此问题\n得等Hypixel修它的API");
				bot.sendGroupMsg(event.getGroupId(), sendMsg.build(), false);
				return MESSAGE_BLOCK;
			}
			if (e.getMessage().contains("404 Not Found")) {
				sendMsg = MsgUtils.builder().text("参数格式打错了\n正确格式：/networth <玩家名>");
				bot.sendGroupMsg(event.getGroupId(), sendMsg.build(), false);
				return MESSAGE_BLOCK;
			}
		}
		return MESSAGE_BLOCK;
	}
}
