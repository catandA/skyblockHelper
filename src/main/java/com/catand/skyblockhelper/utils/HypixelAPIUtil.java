package com.catand.skyblockhelper.utils;

import com.catand.skyblockhelper.SpringConfig;
import lombok.Getter;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.http.HypixelHttpClient;
import net.hypixel.api.reactor.ReactorHttpClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.UUID;

public class HypixelAPIUtil {
	@Getter
	private static final HypixelAPI hypixelAPI = createHypixelAPI();

	private HypixelAPIUtil() {
	}

	private static HypixelAPI createHypixelAPI() {
		ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
		SpringConfig config = context.getBean(SpringConfig.class);
		HypixelHttpClient client = new ReactorHttpClient(UUID.fromString(config.getHypixelApiKey()));
		return new HypixelAPI(client);
	}
}
