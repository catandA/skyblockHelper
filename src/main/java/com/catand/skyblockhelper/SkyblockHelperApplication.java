package com.catand.skyblockhelper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SkyblockHelperApplication {
	public static String VERSION = SkyblockHelperApplication.class.getPackage().getImplementationVersion();

	public static void main(String[] args) {
		SpringApplication.run(SkyblockHelperApplication.class, args);
	}

}
