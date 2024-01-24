package com.catand.skyblockhelper.exception;

import com.catand.skyblockhelper.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NoSuchProfileException extends RuntimeException{
	Player player;
	String profileName;
}
