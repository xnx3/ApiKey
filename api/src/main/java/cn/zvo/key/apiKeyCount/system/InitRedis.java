package cn.zvo.key.apiKeyCount.system;

import com.xnx3.CacheUtil;
import com.xnx3.Lang;
import com.xnx3.Log;
import com.xnx3.cache.RedisUtil;

import cn.zvo.key.apiKeyCount.util.ConfigPropertiesUtil;

/**
 * 初始化 redis
 * @author 管雷鸣
 *
 */
public class InitRedis {
	public InitRedis() {
		//设置使用redis，使用本地的
		new CacheUtil();
		new RedisUtil();
		
		RedisUtil.host = "127.0.0.1";
		String host = ConfigPropertiesUtil.getProperty("redis.host");
		if(host != null && host.trim().length() > 0) {
			RedisUtil.host = host.trim();
		}
		
		RedisUtil.port = 6379;
		String portStr = ConfigPropertiesUtil.getProperty("redis.port");
		if(portStr != null && portStr.trim().length() > 0) {
			RedisUtil.port = Lang.stringToInt(portStr, 6379);
		}
		
		RedisUtil.password = null;
		String password = ConfigPropertiesUtil.getProperty("redis.password");
		if(password != null && password.trim().length() > 0) {
			RedisUtil.password = password.trim();
		}
		
		Log.info("load redis config by config.properties :\n\thost:\t"+RedisUtil.host+"\n\tport:\t"+RedisUtil.port+"\n\tpassword:\t"+RedisUtil.password+"\n\tconfig document : https://translate.zvo.cn/262223.html");
		RedisUtil.createJedisPool(RedisUtil.host, RedisUtil.port, RedisUtil.password);
	}
}
