package com.catand.skyblockhelper.plugins;

import com.alibaba.fastjson2.JSONObject;
import com.catand.skyblockhelper.ErrorProcessor;
import com.catand.skyblockhelper.Player;
import com.catand.skyblockhelper.data.Gamemode;
import com.catand.skyblockhelper.data.SkyblockProfile;
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
			sendMsg = MsgUtils.builder().text("参数错误，\n正确格式：/技能 <玩家名> [档案名]");
			bot.sendGroupMsg(event.getGroupId(), event.getUserId(), sendMsg.build(), false);
			return MESSAGE_BLOCK;
		}
		sendMsg = MsgUtils.builder();
		JSONObject profile;
		String playerName;

		try {
			Player player = new Player(args[1]);
			profile = player.getMainProfile();
			playerName = ProfileUtil.getDisplayNameData(profile);
			if (args.length > 2) {
				// 如果指定了存档名，那么就获取指定存档的数据
				String profileName = SkyblockProfile.getProfile(args[2]).getJsonName();
				JSONObject profile1 = player.getProfile(profileName);
				// 未找到指定存档
				if (profile1 == null) {
					sendMsg = MsgUtils.builder().text("俺没瞅见" + playerName + "有个啥" + profileName + "啊\n俺只知道他有这些:\n");
					for (JSONObject profile2 : player.getProfileList()) {
						sendMsg.text("[" + ProfileUtil.getSkyblockLevel(profile2) + "]" + profile2.getString("cute_name") + Gamemode.getGamemode(profile2).getIcon() + "\n");
					}
					bot.sendGroupMsg(event.getGroupId(), event.getUserId(), sendMsg.build(), false);
					return MESSAGE_BLOCK;
				}
				profile = profile1;
			}

			JSONObject skillsData = ProfileUtil.getSkillsData(profile);
			JSONObject skillsListData = skillsData.getJSONObject("skills");
			DecimalFormat decimalFormat = new DecimalFormat("#.##");
			sendMsg.text(ProfileUtil.getDisplayNameData(profile) + "在" + "[" + ProfileUtil.getSkyblockLevel(profile) + "]" + ProfileUtil.getProfileName(profile) + Gamemode.getGamemode(profile).getIcon() + "上的技能:\n" +
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
			if (ProfileUtil.getDungeonData(profile).getJSONObject("catacombs").getBooleanValue("visited")) {
				sendMsg.text("地牢" + ":\t" + ProfileUtil.getDungeonData(profile).getJSONObject("catacombs").getJSONObject("level").getIntValue("level"));
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
