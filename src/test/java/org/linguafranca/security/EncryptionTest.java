/*  
 * @(#) EncryptionTest.java Create on 2016年4月15日 上午10:12:31   
 *   
 * Copyright 2016 by jeff.   
 */

package org.linguafranca.security;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @EncryptionTest.java
 * @created at 2016年4月15日 上午10:12:31 by zhanghl
 *
 * @version $Revision$
 * @update: $Date$
 */
public class EncryptionTest {

	// {
	// "Nonce": "HZz/DDWUNQIqq5q+QRMGIQ==",
	// "Verifier": "nmHalrWirVAAsysP4FFcu7PnBUuPT/o5tCjiVfp4hcM=",
	// "Key": "WOCkBP6//dFyq1gIXn0X3UALNkQv1rt7JCN0KeM5v+E=",
	// "RequestType": "associate"
	// }
	// AES KEY: WOCkBP6//dFyq1gIXn0X3UALNkQv1rt7JCN0KeM5v+E=
	//
	// --响应 {
	// "Hash": "5b06db8825f5e7f0d890b31ed1c2447fa5c0d78d",
	// "Id": "test123",
	// "Nonce": "nek36Pqs8uMn5wcUYhEL5Q==",
	// "RequestType": "associate",
	// "Success": true,
	// "Verifier": "4R+CK2ebrLc0qYWpsqvTX9OeOImDvxRnleoffbQvi58=",
	// "Version": "1.8.3.0"
	// }
	@Test
	public void testEncryOrg() throws Exception {
		String nonce = "HZz/DDWUNQIqq5q+QRMGIQ==";
		String verifier = "nmHalrWirVAAsysP4FFcu7PnBUuPT/o5tCjiVfp4hcM=";
		String key = "WOCkBP6//dFyq1gIXn0X3UALNkQv1rt7JCN0KeM5v+E=";

		String cc = Encryption.getDeclassify(verifier, key, nonce);
		System.out.println(cc);

		String v2 = "4R+CK2ebrLc0qYWpsqvTX9OeOImDvxRnleoffbQvi58=";
		String n2 = "nek36Pqs8uMn5wcUYhEL5Q==";
		cc = Encryption.getDeclassify(v2, key, n2);
		System.out.println(cc);
	}

//	{
//		"Nonce": "8AtF1SyPfFFjx2tim+lhOQ==",
//		"Verifier": "EZkXqHQqu48PFwLBEobuHwHID99fTPjo2eYxbn31Des=",
//		"Key": "NcVuY/S+/MYW7iLL6PZrGZdZh64H0EBsKpcJIGfxEu4=",
//		"RequestType": "associate"
//	}
//
//	{
//		"Nonce": "ToKgvZ5nJV2YsVDHC9UtlQ==",
//		"SortSelection": "true",
//		"TriggerUnlock": "true",
//		"Id": "test2",
//		"Verifier": "8/mzQ0SJu4aj+2KS/XsRD8qFrkJPgv+L0ZG943GajJk=",
//		"SubmitUrl": "gaUJmejWBM8XmcMhh11CbUuchWLLNWw3KNjVG0Z2GYE=",
//		"Url": "gaUJmejWBM8XmcMhh11CbX1F1L7eWUzqeB5s7HEm2q0=",
//		"RequestType": "get-logins"
//	}
	@Test
	public void testHttpKeepassData() throws Exception {
		String key = "NcVuY/S+/MYW7iLL6PZrGZdZh64H0EBsKpcJIGfxEu4=";
		String nonec = "ToKgvZ5nJV2YsVDHC9UtlQ==";
		String text = "8/mzQ0SJu4aj+2KS/XsRD8qFrkJPgv+L0ZG943GajJk=";
		System.out.println(Encryption.getDeclassify(text, key, nonec));
		text = "gaUJmejWBM8XmcMhh11CbUuchWLLNWw3KNjVG0Z2GYE=";
		System.out.println(Encryption.getDeclassify(text, key, nonec));
	
		text = "gaUJmejWBM8XmcMhh11CbX1F1L7eWUzqeB5s7HEm2q0=";
		System.out.println(Encryption.getDeclassify(text, key, nonec));
	}
}
