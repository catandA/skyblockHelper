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

@Component
public class HOTMPlugin extends BotPlugin {

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
		MsgUtils sendMsg = MsgUtils.builder();
		String[] args = messageRaw.split(" ");
		if (args.length < 2) {
			sendMsg.text("参数错误，\n正确格式：/山心 <玩家名> [档案名]");
			bot.sendGroupMsg(event.getGroupId(), event.getUserId(), sendMsg.build(), false);
			return MESSAGE_BLOCK;
		}
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
					sendMsg.text("俺没瞅见" + playerName + "有个啥" + profileName + "啊\n俺只知道他有这些:\n");
					for (JSONObject profile2 : player.getProfileList()) {
						sendMsg.text("[" + ProfileUtil.getSkyblockLevel(profile2) + "]" + profile2.getString("cute_name") + Gamemode.getGamemode(profile2).getIcon() + "\n");
					}
					bot.sendGroupMsg(event.getGroupId(), event.getUserId(), sendMsg.build(), false);
					return MESSAGE_BLOCK;
				}
				profile = profile1;
			}

			JSONObject miningData = ProfileUtil.getMiningData(profile);
			JSONObject miningCoreData = miningData.getJSONObject("core");
			JSONObject skillsData = ProfileUtil.getSkillsData(profile);
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

			sendMsg.text(ProfileUtil.getDisplayNameData(profile) + "在" + "[" + ProfileUtil.getSkyblockLevel(profile) + "]" + ProfileUtil.getProfileName(profile) + Gamemode.getGamemode(profile).getIcon() + "的山心:\n" +
					"山心等级:" + hotmLevel + "\n" +
					"任务里程碑:" + commissionsLevel + "\n" +
					"秘银粉\uD83D\uDFE2:" + NumberFormatUtil.format(mithrilPowder) + "\n" +
					"宝石粉\uD83D\uDFE3:" + NumberFormatUtil.format(gemstonePowder)
			);
			if (isMaxedMining) {
				sendMsg.text("\nwtfff 满配矿批");
			}

			bot.sendGroupMsg(event.getGroupId(), event.getUserId(), sendMsg.build(), false);
		} catch (Exception e) {
			new ErrorProcessor(e, bot, event);
		}
		return MESSAGE_BLOCK;
	}
}
