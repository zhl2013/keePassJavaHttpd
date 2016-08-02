/*  
 * @(#) KeepassWeb.java Create on 2016年4月12日 下午4:42:28   
 *   
 * Copyright 2016 by jeff.   
 */

package com.jeff.keepass.httpd.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

/**
 * @KeepassWeb.java
 * @created at 2016年4月12日 下午4:42:28 by zhanghl
 *
 * @version $Revision$
 * @update: $Date$
 */
@RequestMapping
@Controller
public class KeepassWeb {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@RequestMapping("/hello")
	@ResponseBody
	public Object testHello(String name) {
		return "hello " + name + "\r\n";
	}

	@Autowired
	private KeepassService KeepassService;

	@RequestMapping("")
	@ResponseBody
	public Object httpd(HttpServletRequest requst) {
		String contentType = requst.getHeader("content-type");
		System.out.println(contentType);
		JSONObject json = null;
		try {
			InputStream inputIo = requst.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(inputIo));
			String sr = null;
			while ((sr = br.readLine()) != null) {
				json = (JSONObject) JSONObject.parse(sr);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.debug("reqeustData:{}", json);
		Map<String, Object> result = this.KeepassService.dealRequest(json);
		logger.debug("response:{}", result);
		return result;
	}

	@RequestMapping("/t3")
	@ResponseBody
	public Object httpd2(JSONObject json) {
		logger.debug("reqeustData:{}", json);
		Map<String, Object> result = this.KeepassService.dealRequest(json);
		logger.debug("response:{}", result);
		return result;
	}

	@RequestMapping("saveAESKey")
	@ResponseBody
	public Object saveAesKey(String key,String name) {
		Map<String, Object> result = new HashMap<>();
		result.put("result", true);
		JSONObject json = new JSONObject();
		json.put("key", key);
		json.put("name", name);
		this.KeepassService.saveAesKey(json, result);
		return result;
	}

	@RequestMapping("toView")
	public String toView(String key, ModelMap modelMap) {
		modelMap.put("key", key);
		return "keepassNotify";
	}
}
