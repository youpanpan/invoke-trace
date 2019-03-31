package com.chengxuunion.util.map;

/**
 * 距离计算工具类
 * 
 * @author youpanpan   
 * @date 2019-03-26 17:49:54
 * @version V1.0
 */
public class DistanceUtil {

	private static final double EARTH_RADIUS = 6371393; // 平均半径,单位：m
	
	private DistanceUtil() {
		
	}
	 
    /**
     * 通过AB点经纬度获取距离
     * @param longitudeA A点经度
     * @param latitudeA A点纬度
     * @param longitudeB B点经度
     * @param latitudeB B点纬度
     * @return 距离(单位：米)
     */
    public static double getDistance(double longitudeA, double latitudeA, double longitudeB, double latitudeB) {
        // 经纬度（角度）转弧度。弧度用作参数，以调用Math.cos和Math.sin
        double radiansAX = Math.toRadians(longitudeA); // A经弧度
        double radiansAY = Math.toRadians(latitudeA); // A纬弧度
        double radiansBX = Math.toRadians(longitudeB); // B经弧度
        double radiansBY = Math.toRadians(latitudeB); // B纬弧度
 
        // 公式中“cosβ1cosβ2cos（α1-α2）+sinβ1sinβ2”的部分，得到∠AOB的cos值
        double cos = Math.cos(radiansAY) * Math.cos(radiansBY) * Math.cos(radiansAX - radiansBX)
                + Math.sin(radiansAY) * Math.sin(radiansBY);
        double acos = Math.acos(cos); // 反余弦值
        return EARTH_RADIUS * acos; // 最终结果
    }
	
    public static void main(String[] args) {
		System.out.println(getDistance(113.549182, 23.494237, 113.549188, 23.494238));
	}
}
