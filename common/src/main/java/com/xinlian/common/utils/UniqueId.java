package com.xinlian.common.utils;

import java.text.ParseException;
import java.util.Random;

/**
 * 生成订单编号 long 64 bit
 */
public class UniqueId {
	/**
	 * 基准时间  2018-01-01 00:00:00
	 */
	private static final long standardTime = 1514736000000L;
	
	/**
	 * 系统编号 8 bit
	 */
	private static long systemNo = 0L << 8;
	
	/**
	 * 毫秒内计数的最大值 8 bit
	 */
	private static final long countMax = 255;
	
	/**
	 * 毫秒内计数的最小值
	 */
	private static final long countMin = 0;
	
	/**
	 * 上次生成订单号的 时间戳
	 */
	private static long lastTimestamp = 0L;
	
	/**
	 * 毫秒内计数
	 */
	private static long count = countMin;
	
	public static void setSystemNo(long systemNo){
		if(systemNo < 0){
			systemNo = 1;
		}
		if(systemNo > 255){
			systemNo = 255;
		}
		UniqueId.systemNo = systemNo << 8;
	}

	public static long getSystemNo(){
		return systemNo;
	}

	public synchronized static long nextId() {
		long timestamp = timeGen();

		if (timestamp < lastTimestamp) {
			throw new RuntimeException("系统时间出错");
		}

		if (lastTimestamp == timestamp) {
			count++;
			// 等待下一毫秒
			if (count >= countMax ) {
				timestamp = tailNextMillis(lastTimestamp);
				count = countMin + new Random().nextInt(127);
			}
		} else {
			count = countMin + new Random().nextInt(127);
		}
		lastTimestamp = timestamp;
		return createNo(timestamp, count);
	}

	private static long tailNextMillis(final long lastTimestamp) {
		long timestamp = timeGen();

		while (timestamp <= lastTimestamp) {
			timestamp = timeGen();
		}
		return timestamp;
	}
	
	private static long createNo(final long timestamp, final long count) {
		long time = (timestamp - standardTime) << 16;
		return time | systemNo | count;
	}

	protected static long timeGen() {
		return System.currentTimeMillis();
	}

	public static void main(String[] args) throws ParseException {
		/*long data = 255L << 55;
		System.out.println(data);
		System.out.println(Long.toBinaryString(data));
		System.out.println(255L << 56);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long time = sdf.parse("3019-8-13 00:00:00").getTime();
		System.out.println(time);
		System.out.println(Long.toBinaryString(time));*/
		System.out.println(Long.toBinaryString(255L << 8));
		for(int i=0; i<2000; i++){
			//System.out.println(Long.toBinaryString(nextId()));
			System.out.println(nextId());
		}
	}
}
