/*  
 * @(#) BrowseUtil.java Create on 2016年4月24日 下午3:50:52   
 *   
 * Copyright 2016 by jeff.   
 */

package com.jeff.keepass.utils;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

/**
 * @BrowseUtil.java
 * @created at 2016年4月24日 下午3:50:52 by zhanghl
 *
 * @version $Revision$
 * @update: $Date$
 */
public class BrowseUtil {
	public static void openUri(String url) {
		try {
			URI uri = new URI(url);
			Desktop desktop = null;
			if (Desktop.isDesktopSupported()) {
				desktop = Desktop.getDesktop();
			}
			if (desktop != null)
				desktop.browse(uri);
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}
	}
	public static void main(String[] args) {
//		openUri("http://localhost:19455/toView?key=MlzY1voenr61Lx6M9WNp4BsDIA/t+d7aDW9K9TQi9mE=");
		try {
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler http://www.jb51.net");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
