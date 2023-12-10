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
			Font font = new Font("fonts/NotoSansSC-Bold.ttf", Font.BOLD, ImageUtil.getFontPixelSize(35));
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

			String[] keys = networthData.getJSONObject("types").keySet().toArray(new String[0]); // 这里应该是你的数据
			int dataPerRow = 3; // 每行的数据数量
			int rows = (int) Math.ceil((double) keys.length / dataPerRow); // 计算行数

			startX = 20;
			startY = 90;
			float rowHeight = (height - startY) / (rows + 1); // 计算每行的高度
			float columnWidth = (float) (width - 40) / dataPerRow; // 计算每列的宽度
			String data = null;


			// 绘制圆角矩形
			roundedRectangle.setRoundRect(startX + 20, startY, 880, rowHeight - 20, 20, 20);
			g2d.setColor(deeper);
			g2d.fill(roundedRectangle);

			// 在圆角矩形上绘制数据
			DecimalFormat decimalFormat = new DecimalFormat();
			decimalFormat.setGroupingUsed(true);
			decimalFormat.setGroupingSize(3);
			String networth = "总计身价:" + decimalFormat.format((long) networthData.getDoubleValue("networth"));
			g2d.setColor(white);
			g2d.drawString(networth,
					(startX + 20) + 440 - ((float) g2d.getFontMetrics().stringWidth(networth) / 2),
					startY + rowHeight / 2 - 10 - (float) g2d.getFontMetrics().getHeight() / 2 + g2d.getFontMetrics().getAscent());

			// 更新 startY 以便在新的一行开始绘制循环数据
			startY += rowHeight;


			// 遍历每行
			for (int i = 0; i < rows; i++) {
				// 遍历每列
				for (int j = 0; j < dataPerRow; j++) {
					// 计算当前数据的索引
					int dataIndex = i * dataPerRow + j;
					// 如果当前索引小于数据的总数，那么我们就绘制这个数据
					if (dataIndex < keys.length) {
						// 计算数据的位置
						float dataX = j * columnWidth + startX;
						float dataY = i * rowHeight + startY;

						// 绘制圆角矩形
						roundedRectangle.setRoundRect(dataX + 20, dataY, columnWidth - 40, rowHeight - 20, 20, 20);
						g2d.setColor(deeper);
						g2d.fill(roundedRectangle);

						// 在圆角矩形上绘制数据
						data = NumberFormatUtil.format((long) networthTypesData.getJSONObject(keys[dataIndex]).getDoubleValue("total"));
						switch (keys[dataIndex]) {
							case "armor" -> keys[dataIndex] = "装备";
							case "equipment" -> keys[dataIndex] = "饰品";
							case "wardrobe" -> keys[dataIndex] = "衣橱";
							case "inventory" -> keys[dataIndex] = "背包";
							case "enderchest" -> keys[dataIndex] = "末影箱";
							case "accessories" -> keys[dataIndex] = "护符";
							case "personal_vault" -> keys[dataIndex] = "保险箱";
							case "storage" -> keys[dataIndex] = "存储";
							case "fishing_bag" -> keys[dataIndex] = "钓鱼袋";
							case "potion_bag" -> keys[dataIndex] = "药水袋";
							case "candy_inventory" -> keys[dataIndex] = "糖果袋";
							case "sacks" -> keys[dataIndex] = "袋子";
							case "essence" -> keys[dataIndex] = "精粹";
							case "pets" -> keys[dataIndex] = "宠物";
						}
						data = keys[dataIndex] + ":" + data;
						dataX = dataX + 20 + columnWidth / 2 - 20 - (float) g2d.getFontMetrics().stringWidth(data) / 2;
						dataY = dataY + rowHeight / 2 - 10 - (float) g2d.getFontMetrics().getHeight() / 2 + g2d.getFontMetrics().getAscent();
						g2d.setColor(white);
						g2d.drawString(data, dataX, dataY);
					}
				}
			}


			g2d.dispose();
			sendMsg.img(ImageUtil.ImageToBase64(image));
			bot.sendGroupMsg(event.getGroupId(), sendMsg.build(), false);
		} catch (Exception e) {
			new ErrorProcessor(e, bot, event);
		}
		return MESSAGE_BLOCK;
	}
}