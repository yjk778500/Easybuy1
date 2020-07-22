package com.web.backend;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;

import com.dao.NewsMapper;
import com.entity.EasybuyNews;
import com.service.news.impl.NewsServiceImpl;
import com.utils.EmptyUtils;
import com.utils.MyBatisUtil;
import com.utils.Pager;
import com.web.AbstractServlet;

/**
 * 新闻资讯列表控制层！
 * Servlet implementation class AdminNewsServlet
 */
@WebServlet("/AdminNewsServlet")
public class AdminNewsServlet extends AbstractServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AdminNewsServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
		
	}
	
	
	
	/**
	 * 获取资讯列表信息！
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String queryNewsList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int currentPage = 1;  //默认显示第一页
		//判断是否用户指定参数
		if(null != request.getParameter("currentPage")){
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		}
		SqlSession ss = MyBatisUtil.getSqlSession();
		NewsServiceImpl nsi = new NewsServiceImpl(ss.getMapper(NewsMapper.class));
		//得到总记录数
		int count = nsi.getTotalCount();
		//创建pager对象
		Pager pager = new Pager(count,10,currentPage);
		//设置url
		pager.setUrl("AdminNewsServlet?action=queryNewsList");
		//得到所有咨询
		List<EasybuyNews> list = nsi.queryNewsList(pager);
		MyBatisUtil.closeSqlSession(ss);
		//存储到request中
		request.setAttribute("newsList", list);
		request.setAttribute("pager", pager);
		return "/backend/news/newsList";
	}
	
   
	
	/**
	 * 新闻详情！
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String newsDeatil(HttpServletRequest request,HttpServletResponse response)throws Exception{
		int id = Integer.parseInt(request.getParameter("newsId"));
		SqlSession ss = MyBatisUtil.getSqlSession();
		NewsServiceImpl nsi = new NewsServiceImpl(ss.getMapper(NewsMapper.class));
		//得到当前新闻详情
		EasybuyNews news = nsi.findNewsById(id);
		MyBatisUtil.closeSqlSession(ss);
		//存储到request中
		request.setAttribute("news", news);
		return "/backend/news/newsDetail";
	}

	@Override
	public Class getServletClass() {
		return AdminNewsServlet.class;
	}

}
