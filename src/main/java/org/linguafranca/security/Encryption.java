/*
 * Copyright 2015 Jo Rabin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.linguafranca.security;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.io.CipherInputStream;
import org.bouncycastle.crypto.io.CipherOutputStream;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encryption and decryption utilities..
 *
 * @author jo
 */
public class Encryption {
	private static Logger logger = LoggerFactory.getLogger(Encryption.class);

	/**
	 * Gets a digest for a UTF-8 encoded string
	 *
	 * @param string
	 *            the string
	 * @return a digest as a byte array
	 */
	@SuppressWarnings("unused")
	public static byte[] getDigest(String string) {
		return getDigest(string, "UTF-8");
	}

	/**
	 * Gets a digest for a string
	 *
	 * @param string
	 *            the string
	 * @param encoding
	 *            the encoding of the String
	 * @return a digest as a byte array
	 */
	public static byte[] getDigest(String string, String encoding) {
		if (string == null || string.length() == 0)
			throw new IllegalArgumentException("String cannot be null or empty");

		if (encoding == null || encoding.length() == 0)
			throw new IllegalArgumentException("Encoding cannot be null or empty");

		MessageDigest md = getMessageDigestInstance();

		try {
			byte[] bytes = string.getBytes(encoding);
			md.update(bytes, 0, bytes.length);
			return md.digest();
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(encoding + " is not supported");
		}
	}

	/**
	 * Gets a SHA-256 message digest instance
	 *
	 * @return A MessageDigest
	 */
	public static MessageDigest getMessageDigestInstance() {
		try {
			return MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("SHA-256 is not supported");
		}
	}

	/**
	 * Create a final key from the parameters passed
	 */
	public static byte[] getFinalKeyDigest(byte[] key, byte[] masterSeed, byte[] transformSeed, long transformRounds) {

		AESEngine engine = new AESEngine();
		engine.init(true, new KeyParameter(transformSeed));

		// copy input key
		byte[] transformedKey = new byte[key.length];
		System.arraycopy(key, 0, transformedKey, 0, transformedKey.length);

		// transform rounds times
		for (long rounds = 0; rounds < transformRounds; rounds++) {
			engine.processBlock(transformedKey, 0, transformedKey, 0);
			engine.processBlock(transformedKey, 16, transformedKey, 16);
		}

		MessageDigest md = getMessageDigestInstance();
		byte[] transformedKeyDigest = md.digest(transformedKey);

		md.update(masterSeed);
		return md.digest(transformedKeyDigest);
	}

	/**
	 * Create a decrypted input stream from an encrypted one
	 */
	public static InputStream getDecryptedInputStream(InputStream encryptedInputStream, byte[] keyData, byte[] ivData) {
		final ParametersWithIV keyAndIV = new ParametersWithIV(new KeyParameter(keyData), ivData);
		PaddedBufferedBlockCipher pbbc = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
		pbbc.init(false, keyAndIV);
		return new CipherInputStream(encryptedInputStream, pbbc);
	}

	/**
	 * Create an encrypted output stream from an unencrypted output stream
	 */
	public static OutputStream getEncryptedOutputStream(OutputStream decryptedOutputStream, byte[] keyData,
			byte[] ivData) {
		final ParametersWithIV keyAndIV = new ParametersWithIV(new KeyParameter(keyData), ivData);
		PaddedBufferedBlockCipher pbbc = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
		pbbc.init(true, keyAndIV);
		return new CipherOutputStream(decryptedOutputStream, pbbc);
	}

	/**
	 * 加密
	 * 
	 * @Title: getEncryptedBase64
	 * @data:2016年4月24日下午2:45:39
	 * @author:zhanghl
	 *
	 * @param textplan
	 * @param keyStr
	 * @param ivStr
	 * @return
	 */
	public static String getEncryptedBase64(String textplan, String keyStr, String ivStr) {
		String result = "";
		byte[] iv = Base64.getDecoder().decode(ivStr);
		byte[] keybytes = Base64.getDecoder().decode(keyStr);
		String content = textplan;
		logger.debug("Original content:{}", textplan);
		try {
			BufferedBlockCipher engine = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
			engine.init(true, new ParametersWithIV(new KeyParameter(keybytes), iv));
			byte[] enc = new byte[engine.getOutputSize(content.getBytes().length)];
			int size1 = engine.processBytes(content.getBytes(), 0, content.getBytes().length, enc, 0);
			int size2 = engine.doFinal(enc, size1);
			byte[] encryptedContent = new byte[size1 + size2];
			System.arraycopy(enc, 0, encryptedContent, 0, encryptedContent.length);
			result = new String(Base64.getEncoder().encode(encryptedContent));
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug("result:{}", result);
		return result;
	}

	/**
	 * 解密
	 * 
	 * @Title: getDeclassify
	 * @data:2016年4月24日下午2:45:50
	 * @author:zhanghl
	 *
	 * @param textplan
	 * @param keyStr
	 * @param ivStr
	 * @return
	 */
	public static String getDeclassify(String textplan, String keyStr, String ivStr) {
		String result = "";
		byte[] iv = Base64.getDecoder().decode(ivStr);
		byte[] keybytes = Base64.getDecoder().decode(keyStr);
		String content = textplan;
		logger.debug("Original content:{}", content);
		try {
			BufferedBlockCipher engine = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
			byte[] encryptedContent = Base64.getDecoder().decode(content);
			engine.init(false, new ParametersWithIV(new KeyParameter(keybytes), iv));
			byte[] dec = new byte[engine.getOutputSize(encryptedContent.length)];
			int size1 = engine.processBytes(encryptedContent, 0, encryptedContent.length, dec, 0);
			int size2 = engine.doFinal(dec, size1);
			byte[] decryptedContent = new byte[size1 + size2];
			System.arraycopy(dec, 0, decryptedContent, 0, decryptedContent.length);
			result = new String(decryptedContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug("result:{}", result);
		return result;
	}

	private static String generateIv(int size) {
		if (size <= 0) {
			size = 16;
		}
		byte[] ivArr = new byte[size];
		for (int i = 0; i < ivArr.length; i++) {
			ivArr[i] = (byte) org.apache.commons.lang.math.RandomUtils.nextInt();
		}
		return Base64.getEncoder().encodeToString(ivArr);
	}

	public static String generateIv(boolean boo) {
		return generateIv(boo ? 16 : 32);
	}

	public static String generateIv() {
		return generateIv(true);
	}
}
