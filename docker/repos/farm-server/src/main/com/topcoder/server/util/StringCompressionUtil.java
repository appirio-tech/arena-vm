package com.topcoder.server.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

/**
 * Utility for string compression.
 * 
 * @author james
 *
 */
public class StringCompressionUtil {

	/**
	 * Compresses a string using gzip compress and returns a base64 encoded
	 * representation of the string.
	 * 
	 * @param str
	 *            The string to compress
	 * @return The compressed base64 string
	 */
	public static String compress(String str) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		GZIPOutputStream zos = new GZIPOutputStream(bos);
		zos.write(str.getBytes("UTF-8"));
		zos.close();
		return new String(Base64.encodeBase64(bos.toByteArray()), "UTF-8");
	}

	/**
	 * Decompresses a gzip compressed base64 encoded string
	 * 
	 * @param compressed
	 *            The string to decompress
	 * @return The decompressed string
	 */
	public static String decompress(String compressed) throws IOException {
		byte[] compBytes = Base64.decodeBase64(compressed.getBytes("UTF-8"));
		GZIPInputStream zis = new GZIPInputStream(new ByteArrayInputStream(compBytes));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		IOUtils.copy(zis, bos);
		zis.close();
		return new String(bos.toString("UTF-8"));
	}
}
