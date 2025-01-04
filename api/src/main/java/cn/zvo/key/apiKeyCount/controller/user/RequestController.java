package cn.zvo.key.apiKeyCount.controller.user;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xnx3.BaseVO;
import com.xnx3.Log;
import cn.zvo.http.Response;
import cn.zvo.key.apiKeyCount.Global;
import cn.zvo.key.apiKeyCount.util.CacheUtil;
import cn.zvo.key.apiKeyCount.util.HttpProxy;
import net.sf.json.JSONObject;

/**
 * API 请求拦截
 * @author 管雷鸣
 */
@Controller(value = "ApiRequest")
@RequestMapping("/")
public class RequestController{
	
	/**
	 * 翻译
	 * url跟to要么不传，要么会都传，如果只传其中一个，那么认为那是源站本身的，不是tcdn的参数。
	 * @param url 源站的url，未翻译之前的页面url
	 * @param to 翻译为什么语言显示，传入如 english
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
		Log.info("currentUseCount:"+currentUseCount);
		
		//判断一下是否还有次数
		if(count - currentUseCount > 0) {
			//正常， 允许
		}else {
			//次数已用完
			response.setStatus(403);
			return JSONObject.fromObject(BaseVO.failure("The number of times has been used up, it has been used "+currentUseCount+" times")).toString();
		}
		
		
		//将请求url转化为源站的
		String targetUrl = Global.ApiDomain+getRequestUrlRemoveDomain(request);
		Log.info("api -- "+targetUrl);
		Response res = HttpProxy.proxy(targetUrl,request);
		response.setStatus(res.getCode());
		Map<String, List<String>> headers = res.getHeaderFields();
		Log.info("response headers: ");
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
		
		/** key次数++ **/
		CacheUtil.setKeyCountUse(key, currentUseCount+1);
		
		
		PrintWriter writer = response.getWriter();
        writer.write(res.content);

        // 手动刷新输出流
        writer.flush();
        return null;
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