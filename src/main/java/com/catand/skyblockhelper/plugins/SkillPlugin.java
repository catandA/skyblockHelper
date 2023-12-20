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

import java.text.DecimalFormat;

@Component
public class SkillPlugin extends BotPlugin {
	MsgUtils sendMsg;

	@Override
	public int onGroupMessage(Bot bot, GroupMessageEvent event) {
		//格式化消息获取参数
		String messageRaw = event.getRawMessage();
		if (!messageRaw.contains("/")) {
			return MESSAGE_IGNORE;
		}
		messageRaw = messageRaw.split("/")[1];
		if (!messageRaw.startsWith("技能")) {
			return MESSAGE_IGNORE;
		}
		String[] args = messageRaw.split(" ");
		if (args.length < 2) {
			sendMsg = MsgUtils.builder().text("参数错误，\n正确格式：/技能 <玩家名>");
			bot.sendGroupMsg(event.getGroupId(), event.getUserId(), sendMsg.build(), false);
			return MESSAGE_BLOCK;
		}

		try {
			Player player = new Player(args[1]);
			JSONObject skillsData = ProfileUtil.getSkillsData(player.getMainProfile());
			JSONObject skillsListData = skillsData.getJSONObject("skills");
			DecimalFormat decimalFormat = new DecimalFormat("#.##");
			sendMsg = MsgUtils.builder().text(ProfileUtil.getDisplayNameData(player.getMainProfile()) + "在" + "[" + ProfileUtil.getSkyblockLevel(player.getMainProfile()) + "]" + ProfileUtil.getProfileName(player.getMainProfile()) + "上的技能:\n" +
					"总经验:" + NumberFormatUtil.format(skillsData.getDoubleValue("totalSkillXp")) + "\t平均等级:" + decimalFormat.format(skillsData.getDouble("averageSkillLevel")) + "\n");

			String[] keys = skillsListData.keySet().toArray(new String[0]);
			for (int i = 0; i < keys.length; i++) {
				String key = keys[i];
				String value = skillsListData.getJSONObject(key).getIntValue("level") + "";
				switch (key) {
					case "combat" -> key = "战斗";
					case "farming" -> key = "农业";
					case "mining" -> key = "挖矿";
					case "foraging" -> key = "砍树";
					case "fishing" -> key = "钓鱼";
					case "enchanting" -> key = "附魔";
					case "taming" -> key = "驯养";
					case "alchemy" -> key = "炼金";
					case "carpentry" -> key = "木工";
					case "runecrafting" -> key = "符文";
					case "social" -> key = "社交";
				}
				sendMsg.text(key + ":\t" + value);
				if ((i + 1) % 3 == 0) {
					sendMsg.text("\n");
				} else {
					sendMsg.text("\t");
				}
			}
			if (ProfileUtil.getDungeonData(player.getMainProfile()).getJSONObject("catacombs").getBooleanValue("visited")) {
				sendMsg.text("地牢" + ":\t" + ProfileUtil.getDungeonData(player.getMainProfile()).getJSONObject("catacombs").getJSONObject("level").getIntValue("level"));
			} else {
				if (keys.length % 3 != 0) {
					sendMsg.text("\n");
				}
				sendMsg.text("地牢?什么地牢?这位进都没进去过");
			}
			bot.sendGroupMsg(event.getGroupId(), event.getUserId(), sendMsg.build(), false);
		} catch (Exception e) {
			new ErrorProcessor(e, bot, event);
		}
		return MESSAGE_BLOCK;
	}
}
