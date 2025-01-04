package cn.zvo.key.apiKeyCount;

import com.xnx3.Log;

/**
 * 全局
 * @author 管雷鸣
 *
 */
public class Global {
	
	public static String ApiDomain = "http://192.168.31.100";
	static {
		String api_domain_str = cn.zvo.key.apiKeyCount.util.ConfigPropertiesUtil.getProperty("api.domain");
		if(api_domain_str != null) {
			ApiDomain = api_domain_str.trim();
			Log.info("load api domain : "+ApiDomain+" by config.properties");
		}
	}
}
