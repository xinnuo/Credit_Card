package com.ruanmeng.allinpay;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.util.Log;

public class PaaCreator {

	public static JSONObject randomPaa() {
		String amount = "13900";

		@SuppressLint("SimpleDateFormat")
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		String timeStr = dateFormat.format(new Date());
		String orderStr = timeStr + "0000";

		JSONObject paaParams = new JSONObject();
		try {
			paaParams.put("inputCharset", "1");
			paaParams.put("receiveUrl", "http://www");
			paaParams.put("version", "v1.0");
			paaParams.put("signType", "0");
			paaParams.put("merchantId", "008310107630044");
			paaParams.put("orderNo", orderStr);
			paaParams.put("orderAmount", amount);
			paaParams.put("orderCurrency", "0");
			paaParams.put("orderDatetime", timeStr);
			paaParams.put("productName", "会员升级");
			paaParams.put("payType", "0");
			paaParams.put("cardNo", "6216913000115973");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String[] paaParamsArray = {
				"1","inputCharset",
				"http://www","receiveUrl",
				"v1.0","version",
				"0","signType",
				"008310107630044","merchantId",
				orderStr,"orderNo",
				amount,"orderAmount",
				"0","orderCurrency",
				timeStr,"orderDatetime",
				"会员升级", "productName",
				"0","payType",
				"1234567890","key",
		};

		String paaStr = "";
		for (int i = 0; i < paaParamsArray.length; i++) {
			paaStr += paaParamsArray[i+1] + "=" + paaParamsArray[i] + "&";
			i++;
		}
		Log.d("PaaCreator", "PaaCreator " + paaStr.substring(0, paaStr.length() -1));
		String md5Str = md5(paaStr.substring(0, paaStr.length() -1));
		Log.d("PaaCreator", "PaaCreator md5Str " + md5Str);
		try {
			paaParams.put("signMsg", md5Str);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return paaParams;
	}

	/**
	 * MD5签名
	 *
	 * @param orgin
	 * @return String
	 */
	private static String md5(String orgin) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(orgin.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}

		return hexString(hash);
	}

	/**
	 * Byte数组转十六进制字符串
	 *
	 * @param bytes
	 * @return String
	 */
	private static String hexString(byte[] bytes) {
		StringBuilder buffer = new StringBuilder();
		for (byte aByte : bytes) buffer.append(hexString(aByte));
		return buffer.toString();
	}

	/**
	 * Byte字节转十六进制字符串
	 *
	 * @param byte0
	 * @return String
	 */
	private static String hexString(byte byte0) {
		char ac[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char ac1[] = new char[2];
		ac1[0] = ac[byte0 >>> 4 & 0xf];
		ac1[1] = ac[byte0 & 0xf];
		return new String(ac1);
	}
}
