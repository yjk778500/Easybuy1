<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script type="text/javascript">
	var contextPath = "${ctx}";
</script>
<meta charset="utf-8">
<title>500错误页面</title>
<link href="${ctx}/statics/css/pintuer.css" rel="stylesheet" />
</head>
<body>
	<div class="main">
		<div class="main_left">
			<img src="${ctx}/statics/images/img2.png" width="229" height="128" />
		</div>
		<div class="main_right">
			<div class="main_radius">
				<p class="main_p">500错误</p>
				<p class="main_p">服务器开小差了，请等等再试试吧！</p>
			</div>
			<div class="padding-big">
				<a href="${ctx }/pre/Index.jsp" class="button bg-yellow">返回首页</a> <a
					href="javascript:;" class="button">保证不打死管理员</a>
			</div>
		</div>
	</div>
</body>
</html>