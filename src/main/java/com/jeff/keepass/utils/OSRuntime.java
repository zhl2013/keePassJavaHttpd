/*  
 * @(#) OSRuntime.java Create on 2016年4月24日 下午5:07:04   
 *   
 * Copyright 2016 by jeff.   
 */

package com.jeff.keepass.utils;

import java.io.IOException;

/**
 * @OSRuntime.java
 * @created at 2016年4月24日 下午5:07:04 by zhanghl
 *
 * @version $Revision$
 * @update: $Date$
 */
public class OSRuntime {

	public static final String OS_NAME = System.getProperty("os.name").toLowerCase();

	/**
	 * 执行命令
	 * 
	 * @param url
	 *            链接地址
	 * @return 是否执行成功
	 */
	public static boolean openByBrower(String url) {
		Runtime run = Runtime.getRuntime();
		if (null == url || "".equals(url)) {
			return false;
		}
		try {
			// 根据系统打开网址
			if (OS_NAME.indexOf("win") > -1) {
				run.exec("rundll32.exe url.dll,FileProtocolHandler " + url);
			} else if (OS_NAME.indexOf("mac") > -1) {
				run.exec("open " + url);
			} else if (OS_NAME.indexOf("nux") > -1 || OS_NAME.indexOf("nix") > -1) {
				String[] cmd = new String[2];
				cmd[0] = "firefox";
				cmd[1] = url;
				try {
					run.exec(cmd);
				} catch (IOException e) {
					cmd[0] = "xdg-open";
					run.exec(cmd);
				}
			} else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}