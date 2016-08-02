/*  
 * @(#) KeepassConfig.java Create on 2016年4月15日 下午5:23:32   
 *   
 * Copyright 2016 by jeff.   
 */

package com.jeff.keepass.httpd.web;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @KeepassConfig.java
 * @created at 2016年4月15日 下午5:23:32 by zhanghl
 *
 * @version $Revision$
 * @update: $Date$
 */
@Component
@ConfigurationProperties("keepass")
public class KeepassConfig {
	private String dbFile;
	private String keyFile;

	public String getDbFile() {
		return dbFile;
	}

	public void setDbFile(String dbFile) {
		this.dbFile = dbFile;
	}

	public String getKeyFile() {
		return keyFile;
	}

	public void setKeyFile(String keyFile) {
		this.keyFile = keyFile;
	}
}
