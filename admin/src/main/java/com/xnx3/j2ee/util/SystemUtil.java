package com.xnx3.j2ee.util;

import java.io.File;

import com.xnx3.Lang;
import com.xnx3.Log;
import com.xnx3.j2ee.Global;

/**
 * 系统工具,比如获取系统变量的参数
 * @author 管雷鸣
 *
 */
public class SystemUtil extends com.xnx3.SystemUtil{
	
	/**
	 * 返回 system 表的值，系统变量的值
	 * @param systemVarName 系统变量的name，也就是 system 表的 name 列
	 * @return 值，也就是 system 表的 value 列。如果没有，则会返回null
	 */
	public static String get(String systemVarName){
		return Global.system.get(systemVarName);
	}
	
	/**
	 * 向缓存中加入一个临时的系统变量 （系统重启后会消失），可以用 SystemUtil.get 进行取
	 * @param name 系统变量的name
	 * @param value 系统变量的值
	 */
	public static void put(String name, String value) {
		Global.system.put(name, value);
	}
	
	/**
	 * 返回 system 表的值（int型的，此取的数据源来源于 {@link #get(String)}，只不过针对Integer进行了二次缓存 ）
	 * @param systemVarName 系统变量的name，也就是 system 表的 name 列
	 * @return 值，也就是 system 表的 value 列。如果没有，会返回0
	 */
	public static int getInt(String systemVarName){
		Integer i = Global.systemForInteger.get(systemVarName);
		if(i == null){
			//没有这个值，那么从system这个原始map中找找
			String s = Global.system.get(systemVarName);
			if(s != null){
				i = Lang.stringToInt(s, 0);
				Global.systemForInteger.put(systemVarName, i);
			}
		}
		if(i == null){
			i = 0;
		}
		Global.systemForInteger.put(systemVarName, i);
		
		return i==null? 0:i;
	}
	
	/**
	 * 当前项目再硬盘的路径，绝对路径。
	 * <br/>返回格式如 /Users/apple/git/wangmarket/target/classes/  最后会加上 /
	 * <br/>如果是在编辑器中开发时运行，返回的是 /Users/apple/git/wangmarket/target/classes/ 这种，最后是有 /target/classes/ 的
	 * <br/>如果是在实际项目中运行，也就是在服务器的Tomcat中运行，返回的是 /mnt/tomcat8/webapps/ROOT/ 这样的，最后是结束到 ROOT/ 下
	 */
	public static String getProjectPath(){
		if(Global.projectPath == null){
			String path = new Global().getClass().getResource("/").getPath();
			Global.projectPath = path.replace("WEB-INF/classes/", "");
			ConsoleUtil.info("projectPath : "+Global.projectPath);
		}
		return Global.projectPath;
	}
	
	private static String classesPath = null;
	/**
	 * 获取当前class文件所在的路径根目录。
	 * <p>如果是在maven项目中开发时，则返回的是 ....../target/classes/</p>
	 * <p>如果是在服务器上tomcat中时，则返回的是 ....../tomcat/webapps/ROOT/WEB-INF/classes/</p>
	 * @return 绝对路径
	 */
	public static String getClassesPath(){
		if(classesPath == null){
			classesPath = new SystemUtil().getClass().getResource("/").getPath();
		}
		return classesPath;
	}
	
	public static boolean isJarRun() {
		return Global.isJarRun;
	}
	
	/**
	 * 当前是否是在开发环境运行的
	 * @return true 是
	 */
	public static boolean isDevelopmentEnvironment() {
		//判断最后是否是这个路径结束
		String str = "/target/classes/";
		String cp = SystemUtil.getClassesPath().toLowerCase();
//		Log.info(cp.length()+"");
//		Log.info(str.length()+"");
//		Log.info(cp.indexOf(str)+"");
		if(cp.indexOf(str) + str.length() == cp.length()) {
			return true;
		}
		return false;
	}
	
	/**
	 * 是否是运行的 打出来的jar包，这个不是开发环境的，而是实际打出jar包，直接运行的jar包
	 * @return true 是
	 */
	public static boolean isJarFileRun() {
		if(new SystemUtil().getClass().getResource("/").getPath().indexOf("jar:file:") == 0) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getJarFilePath() {
		String path = new SystemUtil().getClass().getResource("/").getPath();
		//String path = "jar:file:/G:/git/tcdn_admin_v2/target/translate.admin.v2-1.0.jar!/BOOT-INF/classes!/";
		String np = path.replace("jar:file:/","");
		np = np.replace("!/BOOT-INF/classes!/", "");
		
		//得到如 G:/git/tcdn_admin_v2/target/translate.admin.v2-1.0.jar
		//删除最后的jar文件，也就得到了jar文件所在路径
		np = np.substring(0, np.lastIndexOf("/")+1);
		//System.out.println(np);
		return np;
	}
	
	public static void main(String[] args) {
		System.out.println(getJarFilePath());
	}
}
