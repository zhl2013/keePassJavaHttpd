/*  
 * @(#) KeepassService.java Create on 2016年4月14日 下午3:54:20   
 *   
 * Copyright 2016 by jeff.   
 */

package com.jeff.keepass.httpd.web;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.StringUtils;
import org.linguafranca.pwdb.Entry;
import org.linguafranca.pwdb.kdb.KdbEntry;
import org.linguafranca.security.Encryption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.jeff.keepass.utils.OSRuntime;

/**
 * @KeepassService.java
 * @created at 2016年4月14日 下午3:54:20 by zhanghl
 *
 * @version $Revision$
 * @update: $Date$
 */
@Service
public class KeepassService {
	private String Hash = "5b06db8825f5e7f0d890b31ed1c2447fa5c0d78d";
	@Autowired
	private KeepassDB keepassDB;
	Logger logg = LoggerFactory.getLogger(getClass());
	CountDownLatch latch = new CountDownLatch(2);

	public Map<String, Object> dealRequest(JSONObject json) {
		Map<String, Object> result = new HashMap<>();
		if (json == null) {
			return result;
		}
		result.put("Hash", Hash);
		result.put("Version", "1.8.3.0");

		String id = json.getString("Id");
		String key = "";
		if (StringUtils.isNoneBlank(id)) {
			key = getAESKeyById(id);
			if (StringUtils.isNoneBlank(key) && !verifier(json, key)) {
				logg.error("校验没通过，检查，或者非法请求");
				result.put("error", "verifier false");
				return result;
			}

		}

		String requestType = json.getString("RequestType");
		switch (requestType) {
		case "associate":
			associate(json, result, key);
			break;
		case "test-associate":
			testAssociate(json, result, key);
			break;
		case "get-logins":
			result.put("RequestType", "get-logins");
			getLogins(json, result, key);
		case "generate-password":
			generatePasswd(json,result,key);
		default:
			break;
		}
		return result;
	}

	private void generatePasswd(JSONObject json, Map<String, Object> result, String key) {
		// TODO Auto-generated method stub
		/**
		 * @desc
		 *
		 * @created 2016年4月24日 下午6:02:32 by zhanghl
		 * @version
		 */

	}

	private void getLogins(JSONObject json, Map<String, Object> result, String key) {
		String _url = json.getString("Url");
		String _SubmitUrl = json.getString("SubmitUrl");
		String ivStr = json.getString("Nonce");

		String url = Encryption.getDeclassify(_url, key, ivStr);
		String submitUrl = Encryption.getDeclassify(_SubmitUrl, key, ivStr);

		List<Entry> entryList = this.keepassDB.find(url);

		result.put("Count", entryList.size());
		List<Map<String,String>> Entries = new ArrayList<>();
		result.put("Entries", Entries);
		String nonce = Encryption.generateIv();

		for (Entry entry : entryList) {
			Entries.add(getEntryMap(entry,key,nonce));
		}

		String v2 = Encryption.getEncryptedBase64(nonce, key, nonce);
		result.put("Verifier", v2);
		result.put("Nonce", nonce);
		result.put("Success",true);
		result.put("Id", json.getString("Id"));
	}

	public Map<String,String> getEntryMap(Entry entry,String key,String iv){
		Map<String,String> result = new HashMap<>();
		result.put("Name",entry.getTitle());
		result.put("Login",entry.getUsername());
		result.put("Uuid",entry.getUuid().toString());
		result.put("Password",entry.getPassword());
		
		for (Map.Entry<String, String> entry2 : result.entrySet()) {
			String tkey = entry2.getKey();
			String value = entry2.getValue();
			value = Encryption.getEncryptedBase64(value, key, iv);
			result.put(tkey,value);
		}
		return result;
	}
	
	class UserEntryVo {
		private String Login;
		private String Name;
		private String Password;
		private String Uuid;

		public UserEntryVo() {
		}

		public UserEntryVo(Entry entry) {
			this.setName(entry.getUsername());
			this.setLogin(entry.getUrl());
			this.setUuid(entry.getUuid().toString());
			this.setPassword(entry.getPassword());
		}

		public String getLogin() {
			return Login;
		}

		public void setLogin(String login) {
			Login = login;
		}

		public String getName() {
			return Name;
		}

		public void setName(String name) {
			Name = name;
		}

		public String getPassword() {
			return Password;
		}

		public void setPassword(String password) {
			Password = password;
		}

		public String getUuid() {
			return Uuid;
		}

		public void setUuid(String uuid) {
			Uuid = uuid;
		}
	}

	public void associate(JSONObject json, Map<String, Object> result, String key) {
		String nonce = Encryption.generateIv();
		key = json.getString("Key");
		String verifier = json.getString("Verifier");
		String _n = json.getString("Nonce");
		String or = Encryption.getDeclassify(verifier, key, _n);

		String uri = "http://localhost:19455/toView?key=" + URLEncoder.encode(key);
		// BareBonesBrowserLaunch.openURL(uri);
		OSRuntime.openByBrower(uri);
		latch.countDown();
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		latch = new CountDownLatch(2);
		String Id = "";
		Map<String, String> aesKeyMap = getAESKeyMap();
		for (Map.Entry<String, String> entry : aesKeyMap.entrySet()) {
			if (key.equals(entry.getValue())) {
				Id = entry.getKey();
				break;
			}
		}

		result.put("Id", Id);
		result.put("Success", true);
		String v2 = Encryption.getEncryptedBase64(nonce, key, nonce);
		result.put("Verifier", v2);
		result.put("Nonce", nonce);
	}

	public void testAssociate(JSONObject json, Map<String, Object> result, String key) {
		// boolean boo = json.getBoolean("TriggerUnlock");
		String id = json.getString("Id");
		if (StringUtils.isNoneBlank(id)) {
			result.put("Success", true);
			String aesKey = getAESKeyById(id);
			result.put("Success", StringUtils.isNoneBlank(aesKey));
		} else {
			result.put("Success", false);
		}
	}

	private boolean verifier(JSONObject json, String key) {
		String nonce = json.getString("Nonce");
		String verifiter = json.getString("Verifier");
		String n2 = Encryption.getDeclassify(verifiter, key, nonce);
		return nonce.equals(n2);
	}

	private Entry getKeePassHttpSettings() {
		String title = "KeePassHttp Settings";
		List<Entry> list = keepassDB.findByTitle(title);
		if (list == null || list.size() == 0) {
			Entry entry = new KdbEntry();
			entry.setTitle(title);
			return getKeePassHttpSettings();
		}
		return list.get(0);
	}

	private Map<String, String> getAESKeyMap() {
		Map<String, String> result = new HashMap<>();
		Entry entry = getKeePassHttpSettings();
		if (entry != null) {
			String prefix = "AES Key: ";
			String key = "";
			List<String> pNames = entry.getPropertyNames();
			// AES Key: chrome
			for (String p : pNames) {
				if (StringUtils.startsWith(p, prefix)) {
					key = StringUtils.remove(p, prefix);
					result.put(StringUtils.trim(key), entry.getProperty(p));
				}
			}
		}
		return result;
	}

	private String getAESKeyById(String id) {
		if (StringUtils.isBlank(id)) {
			return "";
		}
		return getAESKeyMap().get(id);
	}

	public void saveAesKey(JSONObject json, Map<String, Object> map) {
		Entry entry = getKeePassHttpSettings();
		String name = "AES Key: " + json.getString("name");
		entry.setProperty(name, json.getString("key"));
		this.keepassDB.dbSave();
		latch.countDown();
	}

}
