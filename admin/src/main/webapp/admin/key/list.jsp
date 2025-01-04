<%@page import="com.xnx3.j2ee.Global"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.xnx3.com/java_xnx3/xnx3_tld" prefix="x"%>
<jsp:include page="/wm/common/head.jsp">
	<jsp:param name="title" value="列表" />
</jsp:include>

<jsp:include page="/wm/common/list/formSearch_formStart.jsp"></jsp:include>
<!-- [tag-5] -->
<jsp:include page="/wm/common/list/formSearch_input.jsp">
	<jsp:param name="iw_label" value="API Key" />
	<jsp:param name="iw_name" value="key" />
</jsp:include>

<a class="layui-btn" href="javascript:wm.list(1);" style="margin-left: 15px;">搜索</a>

<a href="javascript:editItem('', '');" class="layui-btn layui-btn-normal" style="float: right; margin-right: 10px;">添加</a>
</form>

<table class="layui-table iw_table">
	<thead>
		<tr>
		    <!-- [tag-6] -->
			<th>API</th>
			<th>添加时间</th>
			<th>总次数</th>
			<th>剩余次数</th>
			<th>操作</th>
		</tr>
	</thead>
	<tbody>
		<tr v-for="item in list" id="list">
		    <!-- [tag-7] -->
			<td>{{item.key}}</td>
			<td>{{item.addtime}}</td>
			<td>{{item.count}}</td>
			<td>{{item.surplus}}</td>
			<td style="width: 120px;">
				<botton class="layui-btn layui-btn-sm"
					:onclick="'editItem(\'' + item.key + '\', \'key=' + item.key + '\');'" style="margin-left: 3px;">编辑</botton>
				<!-- <botton class="layui-btn layui-btn-sm"
					:onclick="'detailsItem(\'' + item.key + '\', \'id=' + item.key + '\');'" style="margin-left: 3px;">详情</botton> -->
				<botton class="layui-btn layui-btn-sm"
					:onclick="'deleteItem(\'' + item.key + '\', \'key=' + item.key + '\');'" style="margin-left: 3px;">删除</botton>
			</td>
		</tr>
	</tbody>
</table>
<!-- 通用分页跳转 -->
<jsp:include page="/wm/common/page.jsp"></jsp:include>
<script type="text/javascript">

// 刚进入这个页面，加载第一页的数据
wm.list(1, '/admin/key/list.json');

/**
 * 添加、编辑记录信息
 * @param {Object} key 要编辑的记录的key ，空标识添加
 * @param {Object} name 要编辑的记录的名称
 */
function editItem(key, name) {
	layer.open({
		type: 2, 
		title: key.length > 0 ? '编辑【' + key + '】' : '添加', 
		area: ['450px', '460px'],
		shadeClose: true, // 开启遮罩关闭
		content: '/admin/key/edit.jsp?key=' + key
	});
}

/**
 * 查看记录详情
 * @param {Object} key 要查看的记录的key
 * @param {Object} name 要查看记录的名称
 */
function detailsItem(key, name) {
	layer.open({
		type: 2, 
		title: '详情&nbsp;【' + name + '】', 
		area: ['450px', '460px'],
		shadeClose: true, // 开启遮罩关闭
		content: '/admin/key/details.jsp?key=' + key
	});
}

/**
 * 根据id删除一条记录
 * @param {Object} key 要删除的记录key
 * @param {Object} name 要删除的记录的名称
 */
function deleteItem(key, name) {
	msg.confirm('是否删除【' + name + '】？', function() {
		// 显示“删除中”的等待提示
		parent.msg.loading("删除中");
		$.post('/admin/key/delete.json?key=' + key, function(data) {
			// 关闭“删除中”的等待提示
			parent.msg.close();
			if(data.result == '1') {
				parent.msg.success('操作成功');
				// 刷新当前页
				window.location.reload();
			} else if(data.result == '0') {
				parent.msg.failure(data.info);
			} else { 
				parent.msg.failure();
			}
		});
	}, function() {
		
	});
}

</script>

<jsp:include page="/wm/common/foot.jsp"></jsp:include>