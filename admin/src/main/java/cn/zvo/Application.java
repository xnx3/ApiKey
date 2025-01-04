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
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.xnx3.j2ee.util.ConsoleUtil;
import com.xnx3.j2ee.util.SystemUtil;

import cn.zvo.key.apiKeyCount.system.InitRedis;


/**
 * 运行入口
 * @author 管雷鸣
 *
 */
@EnableTransactionManagement // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
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
		ConsoleUtil.debug = true;
		ConsoleUtil.info = true;
		ConsoleUtil.error = true;
		startFinish();
		System.out.println("Start Key Admin Service ...");
		new InitRedis();
		
		
		context = SpringApplication.run(Application.class, args);
		Application.args = args;
	}
	
	/**
	 * 进行Spring相关的信息进行重新加载
	 * @author 李鑫
	 */
	public static void restart(){
		ExecutorService threadPool = new ThreadPoolExecutor (1,1,0, TimeUnit.SECONDS,new ArrayBlockingQueue<> ( 1 ),new ThreadPoolExecutor.DiscardOldestPolicy ());
		threadPool.execute (()->{
			Application.context.close ();
			Application.context = SpringApplication.run (Application.class, args);
		} );
		threadPool.shutdown ();
	}
	
	/**
	 * 当项目运行起来之后，要进行的东西
	 */
	public static void startFinish(){
		new Thread(new Runnable() {
			public void run() {
				while(SystemUtil.get("USER_REG_ROLE") == null){
					//循环，一直到数据库加载完毕
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				//数据库加载完毕了，等待3秒，出现运行成功，已经运行起来的提醒
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				String str = "\n"
						+ "*****************************\n"
						+ "Key Admin 已启动，您可以使用了\n"
						+ "*****************************\n"
						+ "  微信公众号： wangmarket\n"
						+ "  作者微信：  xnx3com   祝您使用愉快！  \n"
						+ "*****************************\n"
						+ "************使用方式**********\n\n"
						+ "打开你的浏览器（推荐使用谷歌浏览器或谷歌内核的浏览器）\n"
						+ "打开网址： http://127.0.0.1:82 \n账号密码都是 admin";
				System.out.println(str);
				
				
			}
		}).start();
	}
}
