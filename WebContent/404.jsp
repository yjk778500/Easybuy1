<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script type="text/javascript">
  var contextPath = "${ctx}";
</script>
<meta charset="utf-8">
<title>404错误页面</title>
<link href="${ctx}/statics/css/pintuer.css" rel="stylesheet"/>
</head>
<body>
<div class="container" style=" margin-top:8%;"> 
   <div class="panel margin-big-top">
      <div class="text-center">
         <br>
         <h2 class="padding-top"> <stong>404错误！抱歉您要找的页面不存在</stong> </h2>
         <div class=""> 
            <div class="float-left">
                <img src="http://www.pintuer.com/images/ds-1.gif">
                <div class="alert"> 哎呀！页面不见了！ </div>
            </div>
            <div class="float-right">
               <img src="http://www.pintuer.com/images/ds-2.png" width="260"> 
            </div>
          </div>
          <div class="padding-big">
               <a href="${ctx }/pre/Index.jsp" class="button bg-yellow">返回首页</a>
               <a href="javascript:;" class="button">保证不打死程序员</a>
          </div> 
      </div> 
   </div> 
</div>
</body>
</html>
