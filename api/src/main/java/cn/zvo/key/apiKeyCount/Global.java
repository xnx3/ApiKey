package cn.zvo.key.apiKeyCount;

import java.util.HashMap;
import java.util.Map;

import com.xnx3.FileUtil;
import com.xnx3.Log;

import net.sf.json.JSONObject;

/**
 * 全局
 * @author 管雷鸣
 *
 */
public class Global {
	
	public static String ApiDomain = "http://192.168.31.100";
	public static Map<String, String> appendParams; //代理像后端发送请求时，是否追加参数，这个追加的参数如果跟本身请求携带参数的名字一致时，会覆盖本身请求的参数，以这个单独传入设置的为准
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
		
		String paramsStr = FileUtil.read("/mnt/key/params.json");
		if(paramsStr != null && paramsStr.length() > 0) {
			appendParams = new HashMap<String, String>();
			JSONObject paramsJson = JSONObject.fromObject(paramsStr);
			for (Object key : paramsJson.keySet()) {
				appendParams.put((String) key, paramsJson.get(key).toString());
				Log.info("load params.json : "+(String) key+"="+paramsJson.get(key).toString());
			}
		}
		
	}
}
