package cn.zvo.key.apiKeyCount.util;

import java.io.IOException;
import java.util.Properties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;


/**
 * 读取 config.properties 属性，可以直接使用 ConfigPropertiesUtil.getProperty("key"); 进行调用
 * @author 管雷鸣
 *
 */
public class ConfigPropertiesUtil {
	
	private static Properties properties;	//config.properties
	
	
	/**
	 * 获取 application.properties 的配置属性
	 * @param key 要获取的配置的名字，如 database.name
	 * @return 获取的配置的值。需要判断是否为null
	 */
	public static String getProperty(String key){
		if(properties == null){
			try {
//				if(SystemUtil.isWindowsOS()) {
//					FileSystemResource resource = new FileSystemResource("G:\\git\\translate_tcdn\\user\\src\\main\\resources\\config.properties");
//					properties = PropertiesLoaderUtils.loadProperties(resource);
//				}else {
//					//centos
//					FileSystemResource resource = new FileSystemResource("/mnt/key/config.properties");
//					properties = PropertiesLoaderUtils.loadProperties(resource);
//				}
				FileSystemResource resource = new FileSystemResource("/mnt/key/config.properties");
				properties = PropertiesLoaderUtils.loadProperties(resource);
//				FileSystemResource resource = new FileSystemResource("/mnt/config.properties");
				
//				Resource resource = new ClassPathResource("/application.properties");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(properties == null){
			return null;
		}
		return properties.getProperty(key);
	}
	
	public static void main(String[] args) {
		System.out.println(getProperty("translate.tcdn.api.jsParser.url"));
	}
}
