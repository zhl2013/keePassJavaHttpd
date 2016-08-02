/*  
 * @(#) TestBrown.java Create on 2016年4月24日 下午3:23:26   
 *   
 * Copyright 2016 by jeff.   
 */

package org;

import java.awt.Desktop;
/**
 * @TestBrown.java
 * @created at 2016年4月24日 下午3:23:26 by zhanghl
 *
 * @version $Revision$
 * @update: $Date$
 */
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class TestBrowse {
	public static void main(String[] a) {
		try {
			URI uri = new URI("http://www.baidu.com");
			Desktop desktop = null;
			if (Desktop.isDesktopSupported()) {
				desktop = Desktop.getDesktop();
			}
			if (desktop != null)
				desktop.browse(uri);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}