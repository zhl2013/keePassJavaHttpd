/*  
 * @(#) ServletInitializer.java Create on 2016年4月12日 下午4:37:10   
 *   
 * Copyright 2016 by jeff.   
 */

package com.jeff.keepass;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @ServletInitializer.java
 * @created at 2016年4月12日 下午4:37:10 by zhanghl
 *
 * @version $Revision$
 * @update: $Date$
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}
}