package cn.zvo;


import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.xnx3.Log;

import cn.zvo.key.apiKeyCount.system.InitRedis;


/**
 * 运行入口
 * @author 管雷鸣
 *
 */
@SpringBootApplication
@ServletComponentScan
@ComponentScan(basePackages = {"com.xnx3", "cn.zvo"})
@EntityScan(basePackages = {"com.xnx3", "cn.zvo"})
public class Application {
	
	// 运行时的参数
	private static String[] args;
	// spring容器的配合容器组件
	public static ConfigurableApplicationContext context;
	
	public static void main(String[] args) {
		System.out.println("Start Key API Service ...");
		new InitRedis();
		
		context = SpringApplication.run(Application.class, args);
		Application.args = args;
	}
	
}
