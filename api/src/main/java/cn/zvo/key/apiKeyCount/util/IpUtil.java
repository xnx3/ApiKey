package cn.zvo.key.apiKeyCount.util;

import javax.servlet.http.HttpServletRequest;

import com.xnx3.Lang;

/**
 * IP相关操作
 * @author 管雷鸣
 *
 */
public class IpUtil {
	
	/**
	 * 获取IP地址，只会返回一个IP
	 * @param request
	 * @return <li>成功：返回一个ip
	 * 			<li>失败：返回null
	 */
	public static String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		
		//如果有多个ip，拿最前面的
		if(ip.indexOf(",")>0){
			ip=Lang.subString(ip, null, ",", 2);
		}
		
		if(ip.equals("0:0:0:0:0:0:0:1")){
			ip = "127.0.0.1";
		}
		return ip;
	} 
	
	//判断字符是否是IP
	public static boolean isIp(String ipString) {
		//1、判断是否是7-15位之间（0.0.0.0-255.255.255.255.255）
		if (ipString.length()<7||ipString.length()>15) {
			return false;
		}
		//2、判断是否能以小数点分成四段
		String[] ipArray = ipString.split("\\.");		
		if (ipArray.length != 4) {
			return false;
		}
		for (int i = 0; i < ipArray.length; i++) {
			//3、判断每段是否都是数字
			try {
				int number = Integer.parseInt(ipArray[i]);
				//4.判断每段数字是否都在0-255之间
				if (number <0||number>255) {
						return false;
				}
			} catch (Exception e) {
				return false;
			}
		}		
		return true;
	}
	
	public static void main(String[] args) {
		System.out.println(isIp("12.43.12.31"));
	}
	
}
