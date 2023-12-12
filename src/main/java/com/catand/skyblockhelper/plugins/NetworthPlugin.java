package com.catand.skyblockhelper.plugins;

import com.alibaba.fastjson2.JSONObject;
import com.catand.skyblockhelper.ErrorProcessor;
import com.catand.skyblockhelper.Player;
import com.catand.skyblockhelper.utils.ImageUtil;
import com.catand.skyblockhelper.utils.NumberFormatUtil;
import com.catand.skyblockhelper.utils.ProfileUtil;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

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
			String profileName = ProfileUtil.getProfileName(player.getMainProfile());

			MsgUtils sendMsg = MsgUtils.builder();

			// 获取原始 BufferedImage 对象的宽度和高度
			int width = 960;
			int height = 500;

			Color grey = new Color(40, 40, 40);
			Color white = new Color(212, 212, 212);
			Color deeper = new Color(30, 30, 30, 200);

			// 创建随机背景底图，添加上边栏
			BufferedImage image = ImageUtil.getBackground(width, height);
			image = ImageUtil.addHeader(image, 70, grey);
			height = height + 70;

			// 获取 Graphics2D 对象，创建字体实例并设置像素大小
			Graphics2D g2d = image.createGraphics();
			Font font = new Font("fonts/NotoSansSC-Bold.ttf", Font.BOLD, ImageUtil.getFontPixelSize(40));
			g2d.setFont(font);

			// 创建四个圆角矩形
			g2d.setColor(white);
			RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(15, 15, 210, 40, 20, 20);
			g2d.fill(roundedRectangle);
			roundedRectangle.setRoundRect(255, 15, 210, 40, 20, 20);
			g2d.fill(roundedRectangle);
			roundedRectangle.setRoundRect(495, 15, 210, 40, 20, 20);
			g2d.fill(roundedRectangle);
			roundedRectangle.setRoundRect(735, 15, 210, 40, 20, 20);
			g2d.fill(roundedRectangle);

			// 计算字符串的起始位置，使其居中到圆角矩形
			g2d.setColor(Color.gray);
			float startY = 35 - (float) g2d.getFontMetrics().getHeight() / 2 + g2d.getFontMetrics().getAscent();
			float startX = 120 - (float) g2d.getFontMetrics().stringWidth(player.name) / 2;
			g2d.drawString(player.name, startX, startY);
			startX = 360 - (float) g2d.getFontMetrics().stringWidth(profileName) / 2;
			g2d.drawString(profileName, startX, startY);
			startX = 600 - (float) g2d.getFontMetrics().stringWidth("还没做你别急") / 2;
			g2d.drawString("还没做你别急", startX, startY);
			startX = 840 - (float) g2d.getFontMetrics().stringWidth("还没做你别急") / 2;
			g2d.drawString("还没做你别急", startX, startY);

			// 现在为边栏的长度
			startX = 30;
			startY = 70;
			// 边距大小
			float margin = 7.5f;

			// body的宽度和高度
			float bodyWidth = width - startX * 2;
			float bodyHeight = height - startY;

			Set<String> keys = new HashSet<>(networthTypesData.keySet());
			// 删除牛毛
			for (String key : keys) {
				// 检查值是否小于1000
				if (networthTypesData.getJSONObject(key).getDoubleValue("total") < 1000) {
					// 如果值小于1000，从networthTypesData中移除该键值对
					networthTypesData.remove(key);
				}
			}
			keys = networthTypesData.keySet();
			int dataPerRow = 4; // 每行的数据数量
			int rows = (int) Math.ceil((double) keys.size() / dataPerRow); // 计算行数
			float rowHeight = bodyHeight / (rows + 1); // 计算每行的高度
			float columnWidth = bodyWidth / dataPerRow; // 计算每列的宽度

			// 绘制圆角矩形
			g2d.setColor(deeper);
			roundedRectangle.setRoundRect(startX + margin, startY + margin, bodyWidth / 3f - 15, rowHeight - 2 * margin, 20, 20);
			g2d.fill(roundedRectangle);
			roundedRectangle.setRoundRect(startX + bodyWidth / 3f + margin, startY + margin, bodyWidth / 3f - 2 * margin, rowHeight - 15, 20, 20);
			g2d.fill(roundedRectangle);
			roundedRectangle.setRoundRect(startX + bodyWidth / 3f * 2 + margin, startY + margin, bodyWidth / 3f - 2 * margin, rowHeight - 15, 20, 20);
			g2d.fill(roundedRectangle);

			String data;

			// 绘制总计身价
			DecimalFormat decimalFormat = new DecimalFormat();
			decimalFormat.setGroupingUsed(true);
			decimalFormat.setGroupingSize(3);
			g2d.setColor(white);
			data = "总计身价:";
			g2d.drawString(data,
					startX + bodyWidth / 3 / 2 - g2d.getFontMetrics().stringWidth(data) / 2f,
					startY + rowHeight / 2 - g2d.getFontMetrics().getHeight() + g2d.getFontMetrics().getAscent());
			data = decimalFormat.format((long) networthData.getDoubleValue("networth"));
			g2d.drawString(data,
					startX + bodyWidth / 3 / 2 - g2d.getFontMetrics().stringWidth(data) / 2f,
					startY + rowHeight / 2 + g2d.getFontMetrics().getAscent());

			// 银行和钱包
			data = "钱包:" + NumberFormatUtil.format((long) networthData.getDoubleValue("purse"));
			g2d.drawString(data,
					startX + bodyWidth / 3f + bodyWidth / 3 / 2 - g2d.getFontMetrics().stringWidth(data) / 2f,
					startY + rowHeight / 2 - g2d.getFontMetrics().getHeight() / 2f + g2d.getFontMetrics().getAscent());
			data = "银行:" + NumberFormatUtil.format((long) networthData.getDoubleValue("bank"));
			g2d.drawString(data,
					startX + bodyWidth / 3f * 2 + bodyWidth / 3 / 2 - g2d.getFontMetrics().stringWidth(data) / 2f,
					startY + rowHeight / 2 - g2d.getFontMetrics().getHeight() / 2f + g2d.getFontMetrics().getAscent());

			// 遍历每行
			for (int i = 0; i < rows; i++) {
				// 遍历每列
				for (int j = 0; j < dataPerRow; j++) {
					// 计算当前数据的索引
					int dataIndex = i * dataPerRow + j;
					// 如果当前索引小于数据的总数，那么我们就绘制这个数据
					if (dataIndex < keys.size()) {
						// 计算数据的位置
						float dataX = j * columnWidth + startX;
						float dataY = (i + 1) * rowHeight + startY;

						// 绘制圆角矩形
						roundedRectangle.setRoundRect(dataX + margin, dataY + margin, columnWidth - 2 * margin, rowHeight - 2 * margin, 20, 20);
						g2d.setColor(deeper);
						g2d.fill(roundedRectangle);

						// 在圆角矩形上绘制数据
						String key = keys.toArray(new String[0])[dataIndex];
						data = NumberFormatUtil.format((long) networthTypesData.getJSONObject(key).getDoubleValue("total"));
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
						data = key + ":" + data;
						dataX = dataX + columnWidth / 2 - g2d.getFontMetrics().stringWidth(data) / 2f;
						dataY = dataY + rowHeight / 2 - g2d.getFontMetrics().getHeight() / 2f + g2d.getFontMetrics().getAscent();
						g2d.setColor(white);
						g2d.drawString(data, dataX, dataY);
					}
				}
			}

			// 绘图完成
			g2d.dispose();
			sendMsg.img(ImageUtil.ImageToBase64(image));
			bot.sendGroupMsg(event.getGroupId(), sendMsg.build(), false);
		} catch (Exception e) {
			new ErrorProcessor(e, bot, event);
		}
		return MESSAGE_BLOCK;
	}
}