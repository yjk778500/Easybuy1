package com.web.pre;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;

import com.dao.NewsMapper;
import com.dao.ProductCategoryMapper;
import com.dao.ProductMapper;
import com.entity.EasybuyNews;
import com.entity.EasybuyProduct;
import com.entity.EasybuyProductCategory;
import com.service.news.impl.NewsServiceImpl;
import com.service.product.impl.ProductCategoryServiceImpl;
import com.service.product.impl.ProductServiceImpl;
import com.utils.MyBatisUtil;
import com.utils.Pager;
import com.web.AbstractServlet;

/**
 * Servlet implementation class IndexServlet
 */
@WebServlet("/IndexServlet")
public class IndexServlet extends AbstractServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public IndexServlet() {
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
	 * 主页面加载
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SqlSession ss = MyBatisUtil.getSqlSession();
		ProductCategoryServiceImpl pcsi = new ProductCategoryServiceImpl(ss.getMapper(ProductCategoryMapper.class));
		NewsServiceImpl nsi = new NewsServiceImpl(ss.getMapper(NewsMapper.class));
		ProductServiceImpl psi = new ProductServiceImpl(ss.getMapper(ProductMapper.class));
		//加载全部商品信息
		List<EasybuyProduct> productList = psi.getEasybuyProductList();
		//创建分页工具对象
		Pager pager = new Pager(nsi.getTotalCount(),5,1);
		List<EasybuyNews> newsList = nsi.queryNewsList(pager);
		//加载分类
		List<EasybuyProductCategory> list = pcsi.getProductCategoryListOne();
		List<EasybuyProductCategory> list2 = pcsi.getProductCategoryListTwo();
		List<EasybuyProductCategory> list3 = pcsi.getProductCategoryListThree();
		MyBatisUtil.closeSqlSession(ss);
		//存到request作用域中
		request.setAttribute("list", list);
		request.setAttribute("list2", list2);
		request.setAttribute("list3", list3);
		request.setAttribute("newsList", newsList);
		request.setAttribute("productList", productList);
		
		return "pre/Index";
	}
	@Override
	public Class getServletClass() {
		return IndexServlet.class;
	}

}
