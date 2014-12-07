package com.example.market.ljw.utils;

/**
 * Created by GYH on 2014/10/20.
 */
public class StringUtils {

    /**
     * 判断给定字符串是否空白串。
     * 空白串是指由空格、制表符、回车符、换行符组成的字符串
     * 若输入字符串为null或空字符串，返回true
     *
     * @param input
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

//    /**
//     * BASE64解密
//     * @param key
//     * @return
//     * @throws Exception
//     */
//    public static byte[] decryptBASE64(String key) throws Exception {
//        return (new BASE64Decoder()).decodeBuffer(key);
//    }
//
//    /**
//     * BASE64加密
//     * @param key
//     * @return
//     * @throws Exception
//     */
//    public static String encryptBASE64(byte[] key) throws Exception {
//        return (new BASE64Encoder()).encodeBuffer(key);
//    }
//
//    public static void main(String[] args) throws Exception
//    {
//        String data = BASE64.encryptBASE64("http://aub.iteye.com/".getBytes());
//        System.out.println("加密前："+data);
//
//        byte[] byteArray = BASE64.decryptBASE64(data);
//        System.out.println("解密后："+new String(byteArray));
//    }

}
