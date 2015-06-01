package com.wjch.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	private final static Pattern emailer = Pattern
			.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
	
	
	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}
	
	//mail checker
	public static boolean isEmail(String email) {
		if (email == null || email.trim().length() == 0)
			return false;
		return emailer.matcher(email).matches();
	}
	public static int toInt(String str, int defValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
		}
		return defValue;
	}
	
	public static String getUrlFromDoubanXml(String s){
		String img_pattren = "http://img(\\d).douban.com/spic/[a-z](\\d+).jpg";
		Pattern pattern = Pattern.compile(img_pattren);
		Matcher matcher = pattern.matcher(s);
		if (matcher.find()) {
			return matcher.group().replace("spic", "lpic");
		} else {
			return "";
		}
	}
}
