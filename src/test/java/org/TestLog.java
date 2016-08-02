/*  
 * @(#) TestLog.java Create on 2016年4月15日 上午11:45:08   
 *   
 * Copyright 2016 by jeff.   
 */


package org;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @TestLog.java
 * @created at 2016年4月15日 上午11:45:08 by zhanghl
 *
 * @version $Revision$
 * @update: $Date$
 */
public class TestLog {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Test
	public void testInfo() throws Exception {
		logger.info("test info");
		logger.debug("test info");
		logger.warn("test info");
		logger.error("test info");
	}
}
