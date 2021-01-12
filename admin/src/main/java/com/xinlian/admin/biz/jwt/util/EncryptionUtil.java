package com.xinlian.admin.biz.jwt.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

import java.util.Random;

public class EncryptionUtil {
    private static final char[] r = new char[] { 'q', 'w', 'e', '8', 's', '2', 'd', 'z', 'x', '9', 'c', '7', 'p', '5',
            'k', '3', 'm', 'j', 'u', 'f', 'r', '4', 'v', 'y', 't', 'n', '6', 'b', 'g', 'h' };
    //加盐参数
    private final static String HASH_ALGORITHM_NAME = "MD5";
    //循环次数
    private final static int HASH_ITERATIONS = 1024;

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

    public static String md5Two(String pwd, String salt){
        return DigestUtils.md5Hex(DigestUtils.md5Hex(pwd) + salt
            + DigestUtils.md5Hex(pwd));
    }

    public static String md5(String credentials, String saltSource) {
        ByteSource salt = new Md5Hash(saltSource);
        return new SimpleHash(HASH_ALGORITHM_NAME, credentials, salt, HASH_ITERATIONS).toString();
    }

    public static String getRandomCode(int length) {
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < length; ++i) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static String createReferralCode(String phone){
        char[] phoneChars = phone.toCharArray();
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        int end = phoneChars.length>3 ? 3 : phoneChars.length;
        for(int i=0; i<end; i++){
            sb.append(phoneChars[i]);
        }
        for(int i=0; i<6; i++){
            sb.append(r[random.nextInt(r.length)]);
        }
        int start = phoneChars.length-4;
        start = start<0 ? 0 : start;
        for(int i=start; i<phoneChars.length; i++){
            sb.append(phoneChars[i]);
        }
        return sb.toString();
    }

    public static String replace(CharSequence str, int startInclude, int endExclude, char replacedChar) {
        if(str==null || str.length()==0) {
            return str(str);
        }
        int strLength = str.length();
        if (startInclude > strLength) {
            return str(str);
        }
        if (endExclude > strLength) {
            endExclude = strLength;
        }
        if (startInclude > endExclude) {
            return str(str);
        }
        char[] chars = new char[strLength];
        for(int i = 0; i < strLength; ++i) {
            if (i >= startInclude && i < endExclude) {
                chars[i] = replacedChar;
            } else {
                chars[i] = str.charAt(i);
            }
        }
        return new String(chars);
    }

    public static String str(CharSequence cs) {
        return null == cs ? null : cs.toString();
    }

    public static void main(String[] args){
        //System.out.println(md5("123", "f0q0s"));
        System.out.println(md5Two("hao123", "076a0c97d0"));
    }
}
