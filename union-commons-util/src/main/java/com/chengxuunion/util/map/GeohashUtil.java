package com.chengxuunion.util.map;

import java.util.BitSet;  
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;  
  
  
/**
 * GeoHash算法
 * 将二维空间坐标转换为字符串编码
 * 
 * @author youpanpan   
 * @date 2019-03-26 15:58:18
 * @version V1.0
 */
public class GeohashUtil {  
  
    /**
     * base32编码表
     */
    private final static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',  
            '9', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p',  
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };  
      
    /**
     * key:编码，value:位置索引
     */
    private final static HashMap<Character, Integer> lookup = new HashMap<Character, Integer>();  
    
    /**
     * geoHash长度与半径的关系
     * key:半径,单位dm，分米, value:geoHash长度
     */
    private final static Map<Long, Integer> geoHashRadiusMap = new TreeMap<Long, Integer>(); 
    
    static {  
        int i = 0;  
        for (char c : digits) {
        	lookup.put(c, i++);          	
        }
        
        geoHashRadiusMap.put(6L, 10);
        geoHashRadiusMap.put(24L, 9);
        geoHashRadiusMap.put(190L, 8);
        geoHashRadiusMap.put(760L, 7);
        geoHashRadiusMap.put(6100L, 6);
        geoHashRadiusMap.put(24000L, 5);
        geoHashRadiusMap.put(195000L, 4);
        geoHashRadiusMap.put(780000L, 3);
        geoHashRadiusMap.put(6300000L, 2);
        geoHashRadiusMap.put(25000000L, 1);
    }  
    
    private GeohashUtil() {
    	
    }
    
    /**
     * 对指定经纬度进行geohash编码，编码长度为geoHashLength
     * <p>
     * 1、对经度、纬度进行逼近值编码
     * 2、偶数位放经度，奇数位放纬度进行组合
     * 3、对组合后的二进制串进行base32编码
     * </p>
     * 
     * @param lat	纬度
     * @param lon	经度
     * @param geoHashLength	编码长度
     * @return	字符串编码格式
     */
    public static String encode(double lat, double lon, int geoHashLength) { 
    	int numbits = geoHashLength / 2 * 5 + 5;
    	if (geoHashLength % 2 == 0) {
    		numbits = geoHashLength / 2 * 5;
    	}
        BitSet latbits = getBits(lat, -90, 90, numbits);  
        BitSet lonbits = getBits(lon, -180, 180, numbits);  
        StringBuilder buffer = new StringBuilder();  
        for (int i = 0; i < numbits; i++) {  
            buffer.append( (lonbits.get(i))?'1':'0');  
            buffer.append( (latbits.get(i))?'1':'0');  
        }  
        return base32(Long.parseLong(buffer.toString(), 2));  
    }  

    /**
     * 对指定geohash编码的字符串进行解码
     * 
     * @param geohash	geohash编码串
     * @return	解码后的经纬度信息[0]:纬度,[1]:经度
     */
    public static double[] decode(String geohash) {  
        StringBuilder buffer = new StringBuilder();  
        for (char c : geohash.toCharArray()) {  
  
            int i = lookup.get(c) + 32;  
            buffer.append(Integer.toString(i, 2).substring(1));  
        }  
        
        BitSet lonset = new BitSet();  
        BitSet latset = new BitSet();  
        
        int length = geohash.length();
        int numbits = length / 2 * 5 + 5;
        if (length % 2 == 0) {
        	numbits = length / 2 * 5;
        }
          
        //经度 bits  
        int j = 0;  
        for (int i = 0; i < numbits * 2; i += 2) {  
            boolean isSet = false;  
            if ( i < buffer.length() )  
              isSet = buffer.charAt(i) == '1';  
            lonset.set(j++, isSet);  
        }  
          
        //纬度 bits  
        j=0;  
        for (int i = 1; i < numbits * 2; i += 2) {  
            boolean isSet = false;  
            if (i < buffer.length()) {
            	isSet = buffer.charAt(i) == '1';              	
            }
            latset.set(j++, isSet);  
        }  
          
        double lon = decode(lonset, -180, 180);  
        double lat = decode(latset, -90, 90);  
          
        return new double[] {lat, lon};       
    }  
    
    /**
     * 根据半径获取最接近的geohash长度
     * 
     * @param radius	半径，单位dm，分米
     * @return	最接近的geohash长度
     */
    public static int getGeoHashLength(Long radius) {
    	int geoHashLength = geoHashRadiusMap.size();
    	for (Entry<Long, Integer> entry : geoHashRadiusMap.entrySet()) {
    		if (entry.getKey() >= radius) {
    			geoHashLength = entry.getValue();
    			break;
    		}
    	}
    	
    	return geoHashLength;
    }
      
    /**
     * 对指定bit进行解码
     * 
     * @param bs	逼近编码
     * @param floor	范围最小值	
     * @param ceiling 范围最大值
     * @return
     */
    private static double decode(BitSet bs, double floor, double ceiling) {  
        double mid = 0;  
        for (int i = 0; i < bs.length(); i++) {  
            mid = (floor + ceiling) / 2;  
            if (bs.get(i)) {
            	floor = mid;              	
            } else {
            	ceiling = mid;              	
            }
        }  
        return mid;  
    }  
      
    
  
    /**
     * 获取指定值在指定范围内的逼近编码
     * 
     * @param lat		指定值，经度或纬度
     * @param floor		范围最小值
     * @param ceiling	范围最大值
     * @param numbits	逼近的位数
     * @return	编码
     */
    private static BitSet getBits(double lat, double floor, double ceiling, int numbits) {  
        BitSet buffer = new BitSet(numbits);  
        for (int i = 0; i < numbits; i++) {  
            double mid = (floor + ceiling) / 2;  
            if (lat >= mid) {  
                buffer.set(i);  
                floor = mid;  
            } else {  
                ceiling = mid;  
            }  
        }  
        return buffer;  
    }  
  
    /**
     * 对long进行base32编码
     * 
     * @param i
     * @return
     */
    public static String base32(long i) {  
        char[] buf = new char[65];  
        int charPos = 64;  
        boolean negative = (i < 0);  
        if (!negative)  {
        	i = -i;          	
        }
        
        while (i <= -32) {  
            buf[charPos--] = digits[(int) (-(i % 32))];  
            i /= 32;  
        }
        
        buf[charPos] = digits[(int) (-i)];  
  
        if (negative) {
        	buf[--charPos] = '-';          	
        }
        return new String(buf, charPos, (65 - charPos));  
    }  
    
    public static void main(String[] args)  throws Exception{  
        System.out.println(GeohashUtil.encode(23.494237, 113.549182, 10));
        System.out.println(GeohashUtil.encode(23.494238, 113.541182, 10));
        //System.out.println(GeohashUtil.decode(geoHashCode)[0]);
              
    }  
  
}