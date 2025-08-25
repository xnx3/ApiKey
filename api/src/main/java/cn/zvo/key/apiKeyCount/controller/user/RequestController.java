package cn.zvo.key.apiKeyCount.controller.user;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xnx3.BaseVO;
import com.xnx3.DateUtil;
import com.xnx3.Log;
import com.xnx3.StringUtil;

import cn.zvo.http.Response;
import cn.zvo.key.apiKeyCount.Global;
import cn.zvo.key.apiKeyCount.util.CacheUtil;
import cn.zvo.key.apiKeyCount.util.ConfigPropertiesUtil;
import cn.zvo.key.apiKeyCount.util.HttpProxy;
import cn.zvo.key.apiKeyCount.util.IpUtil;
import cn.zvo.log.datasource.file.FileDataSource;
import cn.zvo.log.framework.springboot.LogUtil;
import net.sf.json.JSONObject;

/**
 * API 请求拦截
 * @author 管雷鸣
 */
@Controller(value = "ApiRequest")
@RequestMapping("/")
public class RequestController{
	
	/**
	 * API
	 */
	@RequestMapping(value="**")
	@ResponseBody
	public String all(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam(value = "key", defaultValue = "") String key
			) throws IOException{
		if(key.length() != 64) {
			response.setStatus(403);
			return JSONObject.fromObject(BaseVO.failure("Key format error")).toString();
		}
		
		//这个key当前设置的次数上限
		Integer countObj = (Integer) CacheUtil.get(CacheUtil.getCacheKey(CacheUtil.KEY_COUNT_ALL, key));
		if(countObj == null) {
			response.setStatus(403);
			return JSONObject.fromObject(BaseVO.failure("key not find")).toString();
		}
		int count = countObj;
		
		//已使用次数
		Integer currentUseCountObj = (Integer) CacheUtil.get(CacheUtil.getCacheKey(CacheUtil.KEY_COUNT_USE, key));
		int currentUseCount = 0;
		if(currentUseCountObj == null) {
			currentUseCount = 0;
		}else {
			currentUseCount = currentUseCountObj;
		}
		//Log.info("currentUseCount:"+currentUseCount);
		
		//判断一下是否还有次数
		if(count - currentUseCount > 0) {
			//正常， 允许
		}else {
			//次数已用完
			response.setStatus(403);
			return JSONObject.fromObject(BaseVO.failure("The number of times has been used up, it has been used "+currentUseCount+" times")).toString();
		}
		
		//判断是否有限定接口
		String keyUrl = CacheUtil.getKeyUrl(key);
		if(keyUrl.trim().length() == 0) {
			//不限定接口，允许访问 config.properties 设置的 api.domain 的所有资源
		}else {
			//限制访问特定接口
			
			if(!keyUrl.equalsIgnoreCase(request.getServletPath())) {
				Log.debug("key "+key+" 被限制访问 "+keyUrl+", 但实际上访问了 "+request.getServletPath()+" ,被拦截");
				response.setStatus(403);
				return JSONObject.fromObject(BaseVO.failure("当前key被限制访问 "+keyUrl+", 但实际上访问了 "+request.getServletPath())).toString();
			}
		}
		
		//判断是否有限定必传字段
		String keyFromFieldRequired = CacheUtil.getKeyFromFieldRequired(keyUrl);
		if(keyFromFieldRequired != null && keyFromFieldRequired.length() > 0) {
			String[] fields = keyFromFieldRequired.split(",");
			for(int f = 0; f<fields.length; f++) {
				String field = fields[f].trim();
				if(field.length() < 0) {
					continue;
				}
				if(StringUtils.isEmpty(request.getParameter(field))) {
					return JSONObject.fromObject(BaseVO.failure("当前key的"+field+"为必传，您尚未传此参数")).toString();
				}
			}
		}
		
		//将请求url转化为源站的
		String targetUrl = Global.ApiDomain+getRequestUrlRemoveDomain(request);
		Log.info(request.getServletPath());
		Response res = HttpProxy.proxy(targetUrl,request);
		response.setStatus(res.getCode());
		Map<String, List<String>> headers = res.getHeaderFields();
		//Log.info("response headers: ");
		if(headers != null && headers.size() > 0) {
			for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
				if(entry.getKey() == null) {
					continue;
				}
				if(entry.getKey().equalsIgnoreCase("Content-Encoding")) {
					continue;
				}
				if(entry.getKey().equalsIgnoreCase("transfer-encoding")) {
					continue;
				}
				
				//Log.info(entry.getKey()+": "+JSONArray.fromObject(entry.getValue()).toString());
				if(entry.getValue() != null) {
					for(int i = 0; i<entry.getValue().size(); i++) {
						response.addHeader(entry.getKey(), entry.getValue().get(i));
					}
				}else {
					response.setHeader(entry.getKey(), "");
				}
			}
		}
		
		
		//Log.info("response：\n"+res.content);
		
		/*** 设置编码 ****/
		String responseContentType = null;
		if(headers != null && headers.size() > 0) {
			for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
				if(entry.getKey() == null) {
					continue;
				}
				if(!entry.getKey().equalsIgnoreCase("Content-Type")) {
					continue;
				}
				if(entry.getValue() != null) {
					for(int i = 0; i<entry.getValue().size(); i++) {
						if(entry.getValue().get(i) != null && entry.getValue().get(i).trim().length() > 0) {
							responseContentType = entry.getValue().get(i).trim();
							break;
						}
					}
					if(responseContentType != null) {
						break;
					}
				}
			}
		}
		
		//如果响应头里没有 Content-Type ，那么从请求头里找 Accept 赋予
		if(responseContentType == null && request.getHeader("Accept") != null && request.getHeader("Accept").trim().length() > 0) {
			responseContentType = request.getHeader("Accept").trim();
		}
		
		//请求头也没有类型，那就直接赋予文本的编码
		if(responseContentType == null) {
			responseContentType = "text/plain";
		}
		
		//判断 responseContentType 中是否有 charset 编码设置
		if(responseContentType.toLowerCase().indexOf("charset") == -1) {
			//没有设置编码
			String encode = "UTF-8";
			
			//判断是否有 ; 存在
			if(responseContentType.indexOf(";") > -1) {
				//有，那就只是截取 ; 前面部分使用，后面的丢掉
				responseContentType = responseContentType.split(";")[0];
			}
			responseContentType = responseContentType + ";charset="+encode;
		}
		response.setHeader("Content-Type", responseContentType);
		/*** 设置 Content-Type 结束 ***/
		
		/* 将当前有多少次、使用了多少次加入进去 */
		response.setHeader("count", count+""); //当前共有多少次
		response.setHeader("use_count", (currentUseCount+1)+"");//当前已使用了多少次（加本次的也算上了）
		if(res.getCode() - 200 == 0) {
			//API响应成功
			/** key次数++ **/
			CacheUtil.setKeyCountUse(key, currentUseCount+1);
			
			FileDataSource file = new FileDataSource(Global.logPath+"key/"+key+"/");
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("path", request.getServletPath());  //比如 http://localhost:8080/myapp/api/resource?param1=value1&param2=value2  这里则是 /myapp/api/resource
			map.put("time", DateUtil.timeForUnix13()); 	
			map.put("size", res.getContent().length()); //响应的字符串的长度
			map.put("ip", IpUtil.getIpAddress(request));
			map.put("count", count);
			map.put("use_count", (currentUseCount+1));
			list.add(map);
			
			FileDataSource.directoryInit(file.path);
			file.commit("use", list);
		}else {
			//响应失败
			//CacheUtil.set(CacheUtil.getCacheKey(CacheUtil.KEY_COUNT_FAILURE, key), );
		}
		
        return res.content;
    }
	
	/**
	 * 获取当前请求的整个url - 不包含域名部分， 比如当前访问的 http://zvo.cn/ab/c.html?a=1&b=2 那么将会返回 /ab/c.html?a=1&b=2
	 * @param request
	 * @return
	 */
	public static String getRequestUrlRemoveDomain(HttpServletRequest request) {
		String path = request.getRequestURI();
		if(request.getQueryString() != null && request.getQueryString().length() > 0) {
			path = path + "?" + request.getQueryString();
		}
		return path;
	}
}