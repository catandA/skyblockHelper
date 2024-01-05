package com.catand.skyblockhelper.plugins;

import com.alibaba.fastjson2.JSONObject;
import com.catand.skyblockhelper.ErrorProcessor;
import com.catand.skyblockhelper.data.Gamemode;
import com.catand.skyblockhelper.Player;
import com.catand.skyblockhelper.data.SkyblockProfile;
import com.catand.skyblockhelper.utils.ProfileUtil;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;

@Component
public class DungeonPlugin extends BotPlugin {
	MsgUtils sendMsg;

	@Override
	public int onGroupMessage(Bot bot, GroupMessageEvent event) {
		//格式化消息获取参数
		String messageRaw = event.getRawMessage();
		if (!messageRaw.contains("/")) {
			return MESSAGE_IGNORE;
		}
		messageRaw = messageRaw.split("/")[1];
		if (!messageRaw.startsWith("地牢")) {
			return MESSAGE_IGNORE;
		}
		String[] args = messageRaw.split(" ");
		if (args.length < 2) {
			sendMsg = MsgUtils.builder().text("参数错误，\n正确格式：/地牢 <玩家名> [档案名]");
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

			JSONObject dungeonData = ProfileUtil.getDungeonData(profile);
			if (!dungeonData.getJSONObject("catacombs").getBooleanValue("visited")) {
				sendMsg.text(ProfileUtil.getDisplayNameData(profile) + "不玩地牢,注定只能度过一个相对失败的人生");
				bot.sendGroupMsg(event.getGroupId(), event.getUserId(), sendMsg.build(), false);
				return MESSAGE_BLOCK;
			}
			double averageLevel = dungeonData.getJSONObject("classes").getDoubleValue("average_level_with_progress");
			JSONObject classesData = dungeonData.getJSONObject("classes").getJSONObject("classes");
			DecimalFormat decimalFormat = new DecimalFormat("#.##");


			sendMsg = sendMsg.text(ProfileUtil.getDisplayNameData(profile) + "在" + "[" + ProfileUtil.getSkyblockLevel(profile) + "]" + ProfileUtil.getProfileName(profile) + Gamemode.getGamemode(profile).getIcon() + "的地牢:\n" +
					"地牢等级:" + ProfileUtil.getDungeonData(profile).getJSONObject("catacombs").getJSONObject("level").getIntValue("level") + "\t平均职业等级:" + decimalFormat.format(averageLevel) + "\n");

			String[] keys = classesData.keySet().toArray(new String[0]);
			for (int i = 0; i < keys.length; i++) {
				String key = keys[i];
				String value = classesData.getJSONObject(key).getJSONObject("level").getIntValue("level") + "";
				switch (key) {
					case "healer" -> key = "奶妈";
					case "mage" -> key = "法爷";
					case "berserk" -> key = "战士";
					case "archer" -> key = "射手";
					case "tank" -> key = "坦克";
				}
				sendMsg.text(key + ":\t" + value);
				if ((i + 1) % 3 == 0) {
					sendMsg.text("\n");
				} else {
					sendMsg.text("\t");
				}
			}
			bot.sendGroupMsg(event.getGroupId(), event.getUserId(), sendMsg.build(), false);
		} catch (Exception e) {
			new ErrorProcessor(e, bot, event);
		}
		return MESSAGE_BLOCK;
	}
}
