package com.catand.skyblockhelper.plugins;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.GuildMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogPlugin extends BotPlugin {
    @Override
    public int onPrivateMessage(Bot bot, PrivateMessageEvent event) {
        log.info("收到私聊消息 QQ：{} 内容：{}", event.getUserId(), event.getRawMessage());
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(Bot bot, GroupMessageEvent event) {
        log.info("收到群消息 群号：{} QQ：{} 内容：{}", event.getGroupId(), event.getUserId(), event.getRawMessage());
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGuildMessage(Bot bot, GuildMessageEvent event) {
        log.info("收到频道消息 GuildID：{} QQ：{} 内容：{}", event.getGuildId(), event.getUserId(), event.getRawMessage());
        return MESSAGE_IGNORE;
    }
}