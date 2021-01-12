package com.xinlian.rabbitMq;

import java.util.Locale;
import java.util.UUID;

public final class UUIDUtil {

	private UUIDUtil() {
	}

	public static String get32UpperCaseUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase(Locale.ENGLISH);
	}
}
