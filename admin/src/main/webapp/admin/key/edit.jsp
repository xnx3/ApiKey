<%@page import="com.xnx3.j2ee.system.WMConfig"%>
<%@page import="com.xnx3.j2ee.Global"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="/wm/common/head.jsp">
	<jsp:param name="title" value="编辑" />
</jsp:include>

<!-- 如果有id输入项，隐藏不显示出来 -->
<style>
#item_id{ display:none;}
</style>
<form id="form" class="layui-form"
	style="padding-top: 35px; margin-bottom: 10px; padding-right: 35px;">

    <!-- [tag-11] -->
	<div class="layui-form-item" id="item_key">
		<label class="layui-form-label">API Key</label>
		<div class="layui-input-block">
			<input type="text" id="key" name="key" class="layui-input" value="" />			
		</div>
	</div>
	
	<div class="layui-form-item" id="item_count">
		<label class="layui-form-label">总次数</label>
		<div class="layui-input-block">
			<input type="number" id="count" name="count" class="layui-input" value="" />		
			<div class="explain">
				可以使用的总次数，也就是能成功使用的请求次数。
				<br/>注意，只有接口返回 http 200 的响应码时才记为成功，否则都不认为是成功，不会计入成功次数
				<br/>这个key的次数用完后，key将无法再成功通过，会被拦截，不允许其再请求到后端接口
			</div>	
		</div>
	</div>
	
	<div class="layui-form-item" id="item_count">
		<label class="layui-form-label">限定接口</label>
		<div class="layui-input-block">
			<input type="text" id="url" name="url" class="layui-input" value="" />		
			<div class="explain">
				允许请求的API URL的接口，设置如 /a/b.json  省略掉域名部分、get参数部分。
				<br/>如果这个key请求不在这里设置的接口，则直接返回不允许使用的错误，会被拦截。
				<br/>如果这里留空不设置，那么这个key允许访问 config.properties 配置文件中 api.domain 中的所有资源都能被访问。
				<br/>比如只允许能访问 http://aaa.com/a/b.json 这个接口，那么这里就填写 /a/b.json  注意，一定要按照这个格式填写，这样填写后，如果访问的不是 /a/b.json ，比如访问的是 /a/bcd.json 则会直接响应 403 拒绝访问
			</div>	
		</div>
	</div>
	
	<div class="layui-form-item">
		<div class="layui-input-block">
			<a class="layui-btn" onclick="save()">立即保存</a>
			<button type="reset" class="layui-btn layui-btn-primary">重置</button>
		</div>
	</div>
</form>

<script>
// 获取窗口索引
var index = parent.layer.getFrameIndex(window.name); 
// 获取记录key
var key = getUrlParams('key');
	
/**
 * 保存
 */
function save() {
	msg.loading("保存中");
	// 表单序列化
	var param = wm.getJsonObjectByForm($("form"));
	
	wm.post("/admin/key/save.json", param, function(data) {
		msg.close();
		if (data.result == '1') {
			parent.msg.success("操作成功")
			parent.layer.close(index);
			// 刷新父窗口
			parent.location.reload();
		} else if (data.result == '0') {
			msg.failure(data.info);
		} else {
			msg.failure(result);
		}
	}, "text");

	return false;
}

function generateRandomString() {
    const characters = 'abcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    for (let i = 0; i < 64; i++) {
        const randomIndex = Math.floor(Math.random() * characters.length);
        result += characters[randomIndex];
    }
    return result;
}

/**
 * 加载数据
 */
function loadData() {
	msg.loading("加载中");
	wm.post("/admin/key/details.json", {key : key}, function(data) {
		msg.close();
		if (data.result == '1') {
			// 将接口获取到的数据自动填充到 form 表单中
			wm.fillFormValues($('form'), data.key);
			document.getElementById('item_key').style.display='none';
		} else if (data.result == '0') {
			msg.failure(data.info);
		} else {
			msg.failure(result);
		}
	}, "text");
}
if(key != null && key.length > 0){
	//编辑
	loadData();
}else{
	//新增
	document.getElementById('key').value = generateRandomString();
}

</script>

<jsp:include page="/wm/common/foot.jsp"></jsp:include>
