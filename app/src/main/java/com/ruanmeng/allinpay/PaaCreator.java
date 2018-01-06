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
			paaParams.put("signType", "1");
			paaParams.put("merchantId", "");
			paaParams.put("orderNo", orderStr);
			paaParams.put("orderAmount", amount);
			paaParams.put("orderCurrency", "0");
			paaParams.put("orderDatetime", timeStr);
			paaParams.put("productName", "二维码");
			paaParams.put("payType", "27");
			paaParams.put("cardNo", "");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String[] paaParamsArray = {
				"1","inputCharset",
				"http://www","receiveUrl",
				"v1.0","version",
				"1","signType",
				"","merchantId",
				orderStr,"orderNo",
				amount,"orderAmount",
				"0","orderCurrency",
				timeStr,"orderDatetime",
				"二维码", "productName",
				"27","payType",
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
//				paaParams.put("ship_to_country", "US");
//				paaParams.put("ship_to_state", "AL");
//				paaParams.put("ship_to_city", "city");
//				paaParams.put("ship_to_street1", "street_1");
//				paaParams.put("ship_to_street2", "street_2"); 
//				paaParams.put("ship_to_phonenumber", "13812345678");
//				paaParams.put("ship_to_postalcode", "20004");
//				paaParams.put("ship_to_firstname", "Smith");
//				paaParams.put("ship_to_lastname", "Black");
//				paaParams.put("registration_name", "abc");
//				paaParams.put("registration_email", "abc@gmail.com");
//				paaParams.put("registration_phone", "999-13800000000");
//				paaParams.put("buyerid_period", "200");
//				paaParams.put("fnpay_mode", "1");
//				paaParams.put("bill_firstname", "handon");
//				paaParams.put("bill_lastname", "hao");
//				paaParams.put("expireddate", "1919");
//				paaParams.put("cvv2", "888");
//				paaParams.put("bill_email", "abc@gmail.com");
//				paaParams.put("bill_country", "US"); 
//				paaParams.put("bill_address", "billaddress");
//				paaParams.put("bill_city", "billcity");
//				paaParams.put("bill_state", "IL");
//				paaParams.put("bill_zip", "12345");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return paaParams;
	}

	public static String md5(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(
					string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}

		return hexString(hash);
	}

	public static String hexString(byte[] bytes)
	{
		StringBuilder buffer = new StringBuilder();
		for (byte aByte : bytes) buffer.append(hexString(aByte));
		return buffer.toString();
	}

	public static String hexString(byte byte0)
	{
		char ac[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char ac1[] = new char[2];
		ac1[0] = ac[byte0 >>> 4 & 0xf];
		ac1[1] = ac[byte0 & 0xf];
		return new String(ac1);
	}
}
