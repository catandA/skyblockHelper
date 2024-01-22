package com.catand.skyblockhelper.utils;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class HttpUtils {
	public static String httpGetString(String url) {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.exchange(url, HttpMethod.GET, null, String.class).getBody();
	}

	public static byte[] httpGetBytes(String url) {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, null, byte[].class);
		return response.getBody();
	}

	public static String httpPostString(String url, String name) {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.postForEntity(url, name, String.class).getBody();
	}
}
