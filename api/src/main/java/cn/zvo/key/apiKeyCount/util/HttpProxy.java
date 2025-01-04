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

import cn.zvo.http.Http;
import cn.zvo.http.Response;

/**
 * http请求代理转发
 * @author 管雷鸣
 *
 */
public class HttpProxy {
	
	
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
        		res = http.post(url, requestBody, headers);
        	}else {
        		res = http.post(url, new HashMap<String, String>(), headers);
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
