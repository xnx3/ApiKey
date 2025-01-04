package cn.zvo.key.apiKeyCount.util;


public class CacheUtil extends com.xnx3.CacheUtil{
	
	/**
	 * 当前key拥有的总次数
	 */
	public static final String KEY_COUNT_ALL = "{key}:count:all";
	/**
	 * 当前key已使用的次数
	 */
	public static final String KEY_COUNT_USE = "{key}:count:use";
	
	/**
	 * 当前key使用失败的次数，也就是接口的http响应码不是200就是失败
	 */
	public static final String KEY_COUNT_FAILURE = "{key}:count:failure";
	
	/**
	 * 当前key所允许的url，也就是 key数据表的url字段的值
	 */
	public static final String KEY_URL = "{key}:url";
	
	/**
	 * 生成用于redis缓存的key
	 * @param cacheKey 用于redis缓存的key，这个key会包含一个变量{key} 传入如  CacheUtil.KEY_COUNT_FAILURE
	 * @param key 数据表key中的key值
	 * @return 返回如 xasdashjkahshkjashdj:count:all
	 */
	public static String getCacheKey(String cacheKey, String key) {
		return cacheKey.replace("{key}", key);
	}
	
	/**
	 * 设置某个key的允许使用次数
	 * @param key 数据表key
	 * @param count 总允许使用次数
	 */
	public static void setKeyCountAll(String key, int count) {
		set(getCacheKey(KEY_COUNT_ALL, key), count);
	}
	
	/**
	 * 设置某个key的已使用次数+1
	 * @param key 数据表key
	 * @param value 设置的值
	 */
	public static synchronized void setKeyCountUse(String key, int value) {
		String cacheKey = getCacheKey(KEY_COUNT_USE, key);
//		int currentUseCount = (int) get(cacheKey);
//		if(currentUseCount < 1) {
//			Log.error("key "+key+" 已经用完了，但是还在减");
//			return;
//		}
		set(cacheKey, value);
	}
	
	/**
	 * 设置某个key的允许 url
	 * @param key 数据表key
	 * @param url 数据表key的url
	 */
	public static void setKeyUrl(String key, String url) {
		if(url == null) {
			url = "";
		}
		set(getCacheKey(KEY_URL, key), url);
	}
	
	/**
	 * 获取某个key的允许 url
	 * @param key 数据表key
	 * @return url 数据表key的url ，如果不存在，返回 空字符串
	 */
	public static String getKeyUrl(String key) {
		String url = (String) get(getCacheKey(KEY_URL, key));
		if(url == null) {
			url = "";
		}
		return url;
	}
}
