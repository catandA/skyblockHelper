package com.catand.skyblockhelper.plugins;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.catand.skyblockhelper.ErrorProcessor;
import com.catand.skyblockhelper.Player;
import com.catand.skyblockhelper.utils.ProfileUtil;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class BingoPlugin extends BotPlugin {
	MsgUtils sendMsg;

	@Override
	public int onGroupMessage(Bot bot, GroupMessageEvent event) {
		//格式化消息获取参数
		String messageRaw = event.getRawMessage();
		if (!messageRaw.contains("/")) {
			return MESSAGE_IGNORE;
		}
		messageRaw = messageRaw.split("/")[1];
		if (!messageRaw.startsWith("宾果")) {
			return MESSAGE_IGNORE;
		}
		String[] args = messageRaw.split(" ");
		if (args.length < 2) {
			sendMsg = MsgUtils.builder().text("参数错误，\n正确格式：/宾果 <玩家名>");
			bot.sendGroupMsg(event.getGroupId(), sendMsg.build(), false);
			return MESSAGE_BLOCK;
		}
		sendMsg = MsgUtils.builder();

		try {
			Player player = new Player(args[1]);
			JSONObject bingoData = ProfileUtil.getBingoData(player.getMainProfile());
			if (bingoData == null) {
				sendMsg.text(ProfileUtil.getDisplayNameData(player.getMainProfile()) + "不玩宾果,\nskb界又少了一双弹簧鞋");
				bot.sendGroupMsg(event.getGroupId(), sendMsg.build(), false);
				return MESSAGE_BLOCK;
			}
			JSONArray bingoCardItems = ProfileUtil.getBingoCardItems(player.getMainProfile());
			ArrayList<JSONObject> bingoCardItemsList = (ArrayList<JSONObject>) bingoCardItems.toJavaList(JSONObject.class);
			sendMsg.text(ProfileUtil.getDisplayNameData(player.getMainProfile()) + "的宾果:\n" +
					"参加次数:" + bingoData.getIntValue("total") + "|获得点数:" + bingoData.getIntValue("points") + "|完成目标:" + bingoData.getIntValue("completed_goals") + "\n");

			sendMsg.text("本期宾果卡:\n");
			for (int i = 0; i < bingoCardItemsList.size(); i++) {
				JSONObject bingoGoal = bingoCardItemsList.get(i);
				if (bingoGoal.containsKey("id")) {
					int id = bingoGoal.getIntValue("id");
					if (id == 133 || id == 351 || id == 339 || id == 42) {
						continue;
					}
				}
				bingoCardItemsList.remove(i);
				i--; // 将i减1，以防止跳过下一个元素
			}
			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 5; j++) {
					int id = bingoCardItemsList.get(i * 5 + j).getIntValue("id");
					switch (id) {
						case 133:
							sendMsg.text("\uD83D\uDFE9");
							break;
						case 351:
							sendMsg.text("\uD83D\uDFE2");
							break;
						case 339:
							sendMsg.text("\uD83D\uDD34");
							break;
						case 42:
							sendMsg.text("\uD83D\uDFE5");
							break;
						default:
							sendMsg.text("❓");
							break;
					}
				}
				sendMsg.text("\n");
			}
			bot.sendGroupMsg(event.getGroupId(), sendMsg.build(), false);
		} catch (Exception e) {
			new ErrorProcessor(e, bot, event);
		}
		return MESSAGE_BLOCK;
	}
}
