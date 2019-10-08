package com.cf.util.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2017/2/15.
 */
public class SecretUtil {
    /**
     * @param str
     * @Description: 32位小写MD5
     */
    public static String md5(String str) {
        String reStr = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(str.getBytes());
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : bytes) {
                int bt = b & 0xff;
                if (bt < 16) {
                    stringBuffer.append(0);
                }
                stringBuffer.append(Integer.toHexString(bt));
            }
            reStr = stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return reStr;
    }

    /**
     * @param decript 要加密的字符串
     * @return 加密的字符串
     * SHA1加密
     */
    public  static String SHA1(String decript) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String base64decode(String s){
    	BASE64Decoder decoder = new BASE64Decoder();
		try{
			String decodeStr = new String(decoder.decodeBuffer(s));
						
			return decodeStr;
		}catch(Exception e){
			return s;
		}
	}
    public static String base64encode(String s){
    BASE64Encoder encoder = new BASE64Encoder();
		try{
			String encodeStr = encoder.encode(s.getBytes());
			
			return encodeStr;
		}catch(Exception e){
			return s;
		}
	}
	public static void main(String[] args) {
		
		//81060505,81060436,81061067,81061037
	//	String code = SecretUtil.base64encode("{\"customer_number\":81014556 }");
		String code = SecretUtil.base64encode("{\"customer_number\":\"81088785\" }");
		//String code = SecretUtil.base64encode("{\""+DBConst.key_complexity_condition+"\":\"activatedDate like '2018-11-12%'\"}");
		//String code = SecretUtil.base64encode("{\""+DBConst.key_complexity_condition+"\":\" loginTime>=0\"}");
		//String code = SecretUtil.base64encode("{\"1\":\"15516177993\"}");
		System.out.println("https://mis.cf139.com/mc/tabledata?sign=cf8820170808&table=cf_customer&condition="+code);
		//System.out.println("http://mis.cf860.com/mc/tabledata?sign=cf8820170406&table=cf_tracker&condition="+code);
		//long start =System.currentTimeMillis()-600000;
		//long end =System.currentTimeMillis();
		//System.out.println("https://mis.cf139.com/mc/tradeByHand?sign=cf8820170808&start="+start+"&end="+end);
		//System.out.println("https://mis.cf139.com/mc/activebyacount?sign=cf8820170808&acount=81052239");
		code =SecretUtil.base64decode(code);//[[500,1523544365000,"PLA"],["profit","cretetime","customer:lelve"]]
		System.out.println(code);
	}
}
