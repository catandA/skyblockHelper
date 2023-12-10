package com.catand.skyblockhelper.plugins;

import com.alibaba.fastjson2.JSONObject;
import com.catand.skyblockhelper.ErrorProcessor;
import com.catand.skyblockhelper.Player;
import com.catand.skyblockhelper.utils.NumberFormatUtil;
import com.catand.skyblockhelper.utils.ProfileUtil;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.stereotype.Component;

@Component
public class NetworthPlugin extends BotPlugin {
	MsgUtils sendMsg;

	@Override
	public int onGroupMessage(Bot bot, GroupMessageEvent event) {
		//格式化消息获取参数
		String messageRaw = event.getRawMessage();
		if (!messageRaw.contains("/")) {
			return MESSAGE_IGNORE;
		}
		messageRaw = messageRaw.split("/")[1];
		if (!messageRaw.startsWith("身价")) {
			return MESSAGE_IGNORE;
		}
		String[] args = messageRaw.split(" ");
		if (args.length < 2) {
			sendMsg = MsgUtils.builder().text("参数错误，\n正确格式：/身价 <玩家名>");
			bot.sendGroupMsg(event.getGroupId(), sendMsg.build(), false);
			return MESSAGE_BLOCK;
		}

		//获取networth
		try {
			Player player = new Player(args[1]);
			JSONObject networthData = ProfileUtil.getNetworthData(player.getMainProfile());
			JSONObject networthTypesData = networthData.getJSONObject("types");

			sendMsg = MsgUtils.builder().text(player.name + "在" + ProfileUtil.getProfileName(player.getMainProfile()) + "上的身价:\n" +
					"总计:" + NumberFormatUtil.format(networthData.getDoubleValue("networth")) + "\t");
			String[] keys = networthData.getJSONObject("types").keySet().toArray(new String[0]);

			for (int i = 0; i < keys.length; i++) {
				String key = keys[i];
				String value = NumberFormatUtil.format((long) networthTypesData.getJSONObject(key).getDoubleValue("total"));
				switch (key) {
					case "armor" -> key = "装备";
					case "equipment" -> key = "饰品";
					case "wardrobe" -> key = "衣橱";
					case "inventory" -> key = "背包";
					case "enderchest" -> key = "末影箱";
					case "accessories" -> key = "护符";
					case "personal_vault" -> key = "保险箱";
					case "storage" -> key = "存储";
					case "fishing_bag" -> key = "钓鱼袋";
					case "potion_bag" -> key = "药水袋";
					case "candy_inventory" -> key = "糖果袋";
					case "sacks" -> key = "袋子";
					case "essence" -> key = "精粹";
					case "pets" -> key = "宠物";
				}
				sendMsg.text(key + ":\t" + value);
				if ((i + 1) % 3 == 2) {
					sendMsg.text("\n");
				} else {
					sendMsg.text("\t");
				}
			}
			bot.sendGroupMsg(event.getGroupId(), sendMsg.build(), false);
		} catch (Exception e) {
			new ErrorProcessor(e, bot, event);
		}
		return MESSAGE_BLOCK;
	}
}
