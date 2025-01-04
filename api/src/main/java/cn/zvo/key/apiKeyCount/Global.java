package cn.zvo.key.apiKeyCount;

import com.xnx3.Log;

/**
 * 全局
 * @author 管雷鸣
 *
 */
public class Global {
	
	public static String ApiDomain = "http://192.168.31.100";
	public static String logPath = "/mnt/key/logs/"; //log日志文件存放再哪个目录
	
	static {
		String api_domain_str = cn.zvo.key.apiKeyCount.util.ConfigPropertiesUtil.getProperty("api.domain");
		if(api_domain_str != null && api_domain_str.trim().length() > 0) {
			ApiDomain = api_domain_str.trim();
			Log.info("load api domain : "+ApiDomain+" by config.properties");
		}
		
		String logPathObj = cn.zvo.key.apiKeyCount.util.ConfigPropertiesUtil.getProperty("log.path");
		if(logPathObj != null && logPathObj.trim().length() > 0) {
			logPath = logPathObj.trim();
			Log.info("load log path : "+logPath+" by config.properties");
		}
	}
}
