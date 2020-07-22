/**
 * 用户列表删除点击事件！
 */
function delUser(id,currentPage) {
	mizhu.confirm('温馨提醒', '要删除该用户吗？','确认', '取消', function(flag) {
		if (flag) {
			//alert(id + "\t" + currentPage);
			//使用ajax
			$.ajax({
				url			:		contextPath+"/EasybuyUserServlet",
				method		:		"post",
				data		:		{
					action : "del",
					id	   : id,
					currentPage : currentPage   //在哪一页进行操作
				},
				success		:		function(data){
					var json = eval('('+data+')');
					//判断
					if(json.status == 1){
						mizhu.toast("操作成功!",1200);
						//重新加载用户显示页面
						$(".m_right").load(contextPath+"/EasybuyUserServlet .m_right>*","action=user&currentPage="+currentPage);
					}else{
						mizhu.toast(json.message,1200);
					}
				}
			});
		}
	})
}
/**
 * 点击修改
 * @param id
 * @returns
 */
function updateByUserId(id,currentPage) {
	var ise = {"action":"toUpdateUser","id" : id,"currentPage":currentPage};
	$(".m_right").load(contextPath + "/EasybuyUserServlet .m_right>*",ise);
}
/**
 * 点击添加
 * @returns
 */
function addUser() {
	var ise = {"action":"add"};
	$(".m_right").load(contextPath + "/EasybuyUserServlet .m_right>*",ise);
}