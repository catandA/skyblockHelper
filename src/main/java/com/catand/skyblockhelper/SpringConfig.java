package com.catand.skyblockhelper;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SpringConfig {
	@Value("${hypixel-api-key}")
	private String hypixelApiKey;
}
