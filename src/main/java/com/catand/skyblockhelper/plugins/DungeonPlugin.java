package com.catand.skyblockhelper.plugins;

import com.alibaba.fastjson2.JSONObject;
import com.catand.skyblockhelper.ErrorProcessor;
import com.catand.skyblockhelper.data.Gamemode;
import com.catand.skyblockhelper.Player;
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
			sendMsg = MsgUtils.builder().text("参数错误，\n正确格式：/地牢 <玩家名>");
			bot.sendGroupMsg(event.getGroupId(), event.getUserId(), sendMsg.build(), false);
			return MESSAGE_BLOCK;
		}
		sendMsg = MsgUtils.builder();

		try {
			Player player = new Player(args[1]);
			JSONObject dungeonData = ProfileUtil.getDungeonData(player.getMainProfile());
			if (!dungeonData.getJSONObject("catacombs").getBooleanValue("visited")) {
				sendMsg.text(ProfileUtil.getDisplayNameData(player.getMainProfile()) + "不玩地牢,注定只能度过一个相对失败的人生");
				bot.sendGroupMsg(event.getGroupId(), event.getUserId(), sendMsg.build(), false);
				return MESSAGE_BLOCK;
			}
			double averageLevel = dungeonData.getJSONObject("classes").getDoubleValue("average_level_with_progress");
			JSONObject classesData = dungeonData.getJSONObject("classes").getJSONObject("classes");
			DecimalFormat decimalFormat = new DecimalFormat("#.##");


			sendMsg = sendMsg.text(ProfileUtil.getDisplayNameData(player.getMainProfile()) + "在" + "[" + ProfileUtil.getSkyblockLevel(player.getMainProfile()) + "]" + ProfileUtil.getProfileName(player.getMainProfile()) + Gamemode.getGamemode(player.getMainProfile()).getIcon() + "的地牢:\n" +
					"地牢等级:" + ProfileUtil.getDungeonData(player.getMainProfile()).getJSONObject("catacombs").getJSONObject("level").getIntValue("level") + "\t平均职业等级:" + decimalFormat.format(averageLevel) + "\n");

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
