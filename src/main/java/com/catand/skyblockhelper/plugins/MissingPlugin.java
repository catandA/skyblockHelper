package com.catand.skyblockhelper.plugins;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.catand.skyblockhelper.ErrorProcessor;
import com.catand.skyblockhelper.data.Gamemode;
import com.catand.skyblockhelper.Player;
import com.catand.skyblockhelper.utils.NumberFormatUtil;
import com.catand.skyblockhelper.utils.ProfileUtil;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MissingPlugin extends BotPlugin {
	MsgUtils sendMsg;

	@Override
	public int onGroupMessage(Bot bot, GroupMessageEvent event) {
		//格式化消息获取参数
		String messageRaw = event.getRawMessage();
		if (!messageRaw.contains("/")) {
			return MESSAGE_IGNORE;
		}
		messageRaw = messageRaw.split("/")[1];
		if (!messageRaw.startsWith("护符补全")) {
			return MESSAGE_IGNORE;
		}
		String[] args = messageRaw.split(" ");
		if (args.length < 2) {
			sendMsg = MsgUtils.builder().text("参数错误，\n正确格式：/护符补全 <玩家名>");
			bot.sendGroupMsg(event.getGroupId(), event.getUserId(), sendMsg.build(), false);
			return MESSAGE_BLOCK;
		}

		//获取networth
		try {
			Player player = new Player(args[1]);
			JSONObject accessoriesData = ProfileUtil.getAccessoriesData(player.getMainProfile());
			JSONArray accessoriesMissing = accessoriesData.getJSONArray("missing");
			accessoriesMissing.addAll(accessoriesData.getJSONArray("upgrades"));
			ArrayList<JSONObject> accessoriesMissingList = (ArrayList<JSONObject>) accessoriesMissing.toJavaList(JSONObject.class);

			// 按照mp per排序
			accessoriesMissingList.sort((accessories1, accessories2) -> {
				int accessories1Mp = getMagicpowerFromRarity(accessories1.getString("tier"));
				int accessories2Mp = getMagicpowerFromRarity(accessories2.getString("tier"));
				long accessories1price = accessories1.getJSONObject("extra").getLongValue("price");
				long accessories2price = accessories2.getJSONObject("extra").getLongValue("price");
				return (int) (accessories1price / accessories1Mp - accessories2price / accessories2Mp);
			});

			// 把price为0的放到数组最后
			ArrayList<JSONObject> zeropriceAccessories = new ArrayList<>();
			for (int i = 0; i < accessoriesMissingList.size(); i++) {
				JSONObject accessory = accessoriesMissingList.get(i);
				int price = accessory.getJSONObject("extra").getIntValue("price");
				if (price == 0) {
					zeropriceAccessories.add(accessory);
					accessoriesMissingList.remove(i);
					i--; // 将i减1，以防止跳过下一个元素
				}
			}
			accessoriesMissingList.addAll(zeropriceAccessories);

			sendMsg = MsgUtils.builder();
			sendMsg.text(ProfileUtil.getDisplayNameData(player.getMainProfile()) + "在" + "[" + ProfileUtil.getSkyblockLevel(player.getMainProfile()) + "]" + ProfileUtil.getProfileName(player.getMainProfile()) + Gamemode.getGamemode(player.getMainProfile()).getIcon() + "的护符补全:\n" + "总mp:" + accessoriesData.getJSONObject("magical_power").getIntValue("total") + "\n");

			accessoriesMissingList.stream().limit(7).forEach(accessory -> {
				long price = accessory.getJSONObject("extra").getLongValue("price");
				sendMsg.text(accessory.getString("name") + ": " +
						NumberFormatUtil.format(price) +
						"(" + NumberFormatUtil.format(price / getMagicpowerFromRarity(accessory.getString("tier"))) + "每mp)\n");
			});

			bot.sendGroupMsg(event.getGroupId(), event.getUserId(), sendMsg.build(), false);
		} catch (Exception e) {
			new ErrorProcessor(e, bot, event);
		}
		return MESSAGE_BLOCK;
	}

	public static int getMagicpowerFromRarity(String rarity) {
		return switch (rarity) {
			case "common", "special" -> 3;
			case "uncommon", "very_special" -> 5;
			case "rare" -> 8;
			case "epic" -> 12;
			case "legendary" -> 16;
			case "mythic" -> 22;
			default -> 0;
		};
	}
}