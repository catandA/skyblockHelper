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
public class HOTMPlugin extends BotPlugin {
	MsgUtils sendMsg;

	@Override
	public int onGroupMessage(Bot bot, GroupMessageEvent event) {
		//格式化消息获取参数
		String messageRaw = event.getRawMessage();
		if (!messageRaw.contains("/")) {
			return MESSAGE_IGNORE;
		}
		messageRaw = messageRaw.split("/")[1];
		if (!messageRaw.startsWith("山心")) {
			return MESSAGE_IGNORE;
		}
		String[] args = messageRaw.split(" ");
		if (args.length < 2) {
			sendMsg = MsgUtils.builder().text("参数错误，\n正确格式：/山心 <玩家名>");
			bot.sendGroupMsg(event.getGroupId(), sendMsg.build(), false);
			return MESSAGE_BLOCK;
		}

		try {
			Player player = new Player(args[1]);
			JSONObject miningData = ProfileUtil.getMiningData(player.getMainProfile());
			JSONObject miningCoreData = miningData.getJSONObject("core");
			JSONObject skillsData = ProfileUtil.getSkillsData(player.getMainProfile());
			JSONObject skillsListData = skillsData.getJSONObject("skills");
			int miningLevel = skillsListData.getJSONObject("mining").getIntValue("level");
			int hotmLevel = miningCoreData.getJSONObject("level").getIntValue("level");
			int commissionsLevel = miningData.getJSONObject("commissions").getIntValue("milestone");
			int mithrilPowder = miningCoreData.getJSONObject("powder").getJSONObject("mithril").getIntValue("total");
			int gemstonePowder = miningCoreData.getJSONObject("powder").getJSONObject("gemstone").getIntValue("total");
			boolean isMaxedMining = false;
			if (miningLevel == 60 && hotmLevel == 7 && commissionsLevel == 6 && mithrilPowder >= 12000000 && gemstonePowder >= 20000000) {
				isMaxedMining = true;
			}

			sendMsg = MsgUtils.builder().text(ProfileUtil.getDisplayNameData(player.getMainProfile()) + "在" + "[" + ProfileUtil.getSkyblockLevel(player.getMainProfile()) + "]" + ProfileUtil.getProfileName(player.getMainProfile()) + "的山心:\n" +
					"山心等级:" + hotmLevel + "\n" +
					"任务里程碑:" + commissionsLevel + "\n" +
					"秘银粉\uD83D\uDFE2:" + NumberFormatUtil.format(mithrilPowder) + "\n" +
					"宝石粉\uD83D\uDFE3:" + NumberFormatUtil.format(gemstonePowder)
			);
			if (isMaxedMining) {
				sendMsg.text("\nwtfff 满配矿批");
			}

			bot.sendGroupMsg(event.getGroupId(), sendMsg.build(), false);
		} catch (Exception e) {
			new ErrorProcessor(e, bot, event);
		}
		return MESSAGE_BLOCK;
	}
}
