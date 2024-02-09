package com.catand.skyblockhelper.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NoPlayerException extends RuntimeException {
	String playerName;
}
