package com.xinlian.common.utils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class UniqueNoUtil {
    private static long systemNo = 0;
    private static AtomicLong currentTime = new AtomicLong(0);
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyMMdd");

    public static void setSystemNo(long systemNo){
        UniqueNoUtil.systemNo = systemNo;
    }

    public static long getMillisecond(){
        long update = System.currentTimeMillis();
        long expect = currentTime.get();
        while(!currentTime.compareAndSet(expect, update) || update <= expect){
            expect = currentTime.get();
            update = System.currentTimeMillis();
        }
        return update;
    }

    public static String createNo(){
        return String.valueOf(sdf.format(new Date(getMillisecond())))
                + String.valueOf(UniqueNoUtil.systemNo);
    }

    public static String uuid(){
       return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static void main(String[] args){
        System.out.println(sdf1.format(new Date()));
        System.out.println((int) 'A');
        System.out.println((int) 'Z');
        System.out.println((int) 'a');
        System.out.println((int) 'z');

       /* Random random = new Random();
        System.out.println(random.nextInt(26) + 65);

        System.out.println(createCodes(4));*/
        Map<String ,String> map = new HashMap<>(1000000);
        for(int i=0; i<1000000; i++){
            String key = createInvitationCode();
            map.put(key,key);
        }
        System.err.println("大小："+map.size());
    }

    public static String createInvitationCode(){
        return createInvitationCode(4);
    }
    private static String createInvitationCode(int count){
        String code = sdf1.format(new Date());
        code = randomUpperCase() + code;
        for(int i=1; i<count; i++){
            code = code + createRandomCodes();
        }
        return code;
    }

    public static char createRandomCodes(){
        Random random = new Random();
        int cc = random.nextInt(2);
        if(cc==0){
            return randomLowerCase();
        }else {
            return randomUpperCase();
        }
    }

    private static char randomUpperCase(){
        return randomChar(65,90);
    }


    private static char randomLowerCase(){
        return randomChar(97,122);
    }

    private static char randomChar(int min,int max){
        Random random = new Random();
        int s = random.nextInt(max)%(max-min+1) + min;
        return (char)s;
    }
}
