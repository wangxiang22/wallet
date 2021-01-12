package com.xinlian.admin.biz.myshiro.utils;
import org.springframework.util.DigestUtils;

import java.util.Random;

public class EncryptionUtil {

    public static String getMD5(String credentials, String saltSource) {
        String base = credentials +"/" + saltSource;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }

    public static String getSalt(){
        return getRandomString(5);
    }

    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < length; ++i) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static void main(String[] args){
        System.out.println(getMD5("123456", "kirow"));
        /*System.out.println(getMD5("123456", "shddd"));
        System.out.println(getMD5("123456", "shddfd"));*/
    }
}
