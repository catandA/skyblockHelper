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
import org.springframework.web.client.HttpServerErrorException;

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
			bot.sendGroupMsg(event.getGroupId(), sendMsg.build(), false);
			return MESSAGE_BLOCK;
		}

		try {
			Player player = new Player(args[1]);
			JSONObject levelsData = ProfileUtil.get_levels_Data(player.getMainProfile());
			sendMsg = MsgUtils.builder().text(player.name + "在" + ProfileUtil.getProfileName(player.getMainProfile()) + "上的技能(排名:" + ProfileUtil.get_average_level_rank_Data(player.getMainProfile()) + "):\n" +
					"总经验:" + NumberFormatUtil.format(ProfileUtil.get_total_skill_xp_Data(player.getMainProfile())) + "\t平均等级:" + NumberFormatUtil.format(ProfileUtil.get_average_level_Data(player.getMainProfile())) +
					"\n战斗:" + NumberFormatUtil.format(levelsData.getJSONObject("combat").getIntValue("level")) + "\t农业:" + NumberFormatUtil.format(levelsData.getJSONObject("farming").getIntValue("level")) +
					"\n挖矿:" + NumberFormatUtil.format(levelsData.getJSONObject("mining").getIntValue("level")) + "\t砍树:" + NumberFormatUtil.format(levelsData.getJSONObject("foraging").getIntValue("level")) +
					"\n钓鱼:" + NumberFormatUtil.format(levelsData.getJSONObject("fishing").getIntValue("level")) + "\t附魔:" + NumberFormatUtil.format(levelsData.getJSONObject("enchanting").getIntValue("level")) +
					"\n驯养:" + NumberFormatUtil.format(levelsData.getJSONObject("taming").getIntValue("level")) + "\t地牢:" + NumberFormatUtil.format(ProfileUtil.getDungeonData(player.getMainProfile()).getJSONObject("catacombs").getJSONObject("level").getIntValue("level")) +
					"\n炼金:" + NumberFormatUtil.format(levelsData.getJSONObject("alchemy").getIntValue("level")) + "\t木工:" + NumberFormatUtil.format(levelsData.getJSONObject("carpentry").getIntValue("level")) +
					"\n符文:" + NumberFormatUtil.format(levelsData.getJSONObject("runecrafting").getIntValue("level")) + "\t社交:" + NumberFormatUtil.format(levelsData.getJSONObject("social").getIntValue("level")));
			bot.sendGroupMsg(event.getGroupId(), sendMsg.build(), false);
		} catch (Exception e) {
			new ErrorProcessor(e, bot, event);
		}
		return MESSAGE_BLOCK;
	}
}
