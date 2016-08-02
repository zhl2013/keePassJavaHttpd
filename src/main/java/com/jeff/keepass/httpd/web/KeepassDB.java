/*  
 * @(#) KeepassDB.java Create on 2016年4月15日 下午5:23:02   
 *   
 * Copyright 2016 by jeff.   
 */

package com.jeff.keepass.httpd.web;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.linguafranca.pwdb.Entry;
import org.linguafranca.pwdb.Entry.Matcher;
import org.linguafranca.pwdb.kdbx.KdbxCredentials;
import org.linguafranca.pwdb.kdbx.KdbxStreamFormat;
import org.linguafranca.pwdb.kdbx.dom.DomDatabaseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @KeepassDB.java
 * @created at 2016年4月15日 下午5:23:02 by zhanghl
 *
 * @version $Revision$
 * @update: $Date$
 */
@Repository
public class KeepassDB {
	// TODO pwd 需要通过提示框输入
	private final String pwd = "110120";
	private DomDatabaseWrapper db;
	@Autowired
	private KeepassConfig config;

	@PostConstruct
	public void init() {
		String keyfile = config.getKeyFile();
		String dbName = config.getDbFile();
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(dbName);
			InputStream keyinputStream = new FileInputStream(keyfile);
			KdbxCredentials.KeyFile credentials = new KdbxCredentials.KeyFile(pwd.getBytes(), keyinputStream);
			db = new DomDatabaseWrapper(new KdbxStreamFormat(), credentials, inputStream);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("初始化数据库失败", e);
		}
	}

	public List<Entry> find(final String word) {
		return db.findEntries(StringUtils.lowerCase(word));
	}

	public List<Entry> findByTitle(String title) {
		List<Entry> result = db.findEntries(new Matcher() {
			public boolean matches(Entry entry) {
				String t = StringUtils.defaultIfEmpty(title, "");
				return t.equals(entry.getTitle());
			}
		});
		return result;
	}

	public synchronized void dbSave() {
		try {
			OutputStream outputStream = new FileOutputStream(config.getDbFile());
			InputStream keyinputStream = new FileInputStream(config.getKeyFile());
			KdbxCredentials.KeyFile credentials = new KdbxCredentials.KeyFile(pwd.getBytes(), keyinputStream);
			db.save(credentials, outputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		init();
	}

	public void addEntry(Entry entry) {
		db.newEntry(entry);
		this.dbSave();
	}
}
