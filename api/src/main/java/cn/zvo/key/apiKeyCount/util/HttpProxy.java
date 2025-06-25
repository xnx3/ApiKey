package cn.zvo.key.apiKeyCount.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.xnx3.BaseVO;
import com.xnx3.Log;
import com.xnx3.StringUtil;

import cn.zvo.http.Http;
import cn.zvo.http.Response;
import cn.zvo.key.apiKeyCount.Global;

/**
 * http请求代理转发
 * @author 管雷鸣
 *
 */
public class HttpProxy {
	
	/**
	 * 获取某个源站的代理的规则,哪些url是要通过代理转发的
	 */
	public static void getProxyRuleBySourceDomain() {
		
	}
	
	/**
	 * 代理
	 * @param url 要实际访问的url，代理的后端访问url
	 * @param request 
	 * @return
	 */
	public static Response proxy(String url, HttpServletRequest request) throws IOException {
		// 获取请求头信息
		Map<String, String> headers = new HashMap<String, String>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            //System.out.println("请求头: " + headerName + " = " + headerValue);
            if(headerName.trim().equalsIgnoreCase("host")) {
            	//过滤来源页面
            	continue;
            }
            if(headerName.trim().equalsIgnoreCase("referer")) {
            	//过滤来源页面
            	continue;
            }
            headers.put(headerName, headerValue);
        }
        
        // 获取请求体信息
        StringBuilder requestBodyBuilder = new StringBuilder();
        try (InputStream inputStream = request.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine())!= null) {
                requestBodyBuilder.append(line);
            }
        }
        String requestBody = requestBodyBuilder.toString();
        //System.out.println("请求体: " + requestBody);
        
        
        Http http = new Http();
        //http.setTimeout(30);
        Response res = null;
        if(request.getMethod().equalsIgnoreCase("POST")) {
        	if(requestBody != null && requestBody.length() > 0) {
        		// payload 方式
        		res = http.post(url, requestBody, headers);
        	}else {
        		//form data方式
        		Map<String, String> params = new HashMap<String, String>(); //http发送的
        		Map<String, String[]> oriParams = request.getParameterMap();
                for (Map.Entry<String, String[]> entry : oriParams.entrySet()) {
                	if(entry.getValue().length > 1) {
                		//传递的值是个数组
                		for(int v = 0; v<entry.getValue().length; v++) {
                			params.put(entry.getKey()+"["+v+"]", StringUtil.stringToUrl(entry.getValue()[v]));
                    	}
                	}else if(entry.getValue().length == 1){
                		//单个参数
                		params.put(entry.getKey(), StringUtil.stringToUrl(entry.getValue()[0]));
                	}
                }
                if(Global.appendParams != null && Global.appendParams.size() > 0) {
                	for (Map.Entry<String, String> entry : Global.appendParams.entrySet()) {
                		params.put(entry.getKey(), entry.getValue());
                	}
                }
        		res = http.post(url, params, headers);
        	}
        }else if(request.getMethod().equalsIgnoreCase("GET")){
        	res = http.get(url, new HashMap<String, String>(), headers);
        }else {
        	Log.info("未发现的代理METHOD： "+request.getMethod()+", "+url);
        	res = new Response();
        	res.setCode(500);
        	res.setContent("not find proxy method:"+request.getMethod()+", "+url);
        }
        //Log.info(res.getCode()+" , "+res.getContent());
        
        return res;
	}
	
	public static void ungzip() {
		
	}
}
