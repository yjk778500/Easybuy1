package com.web.backend;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;

import com.dao.OrderDetailMapper;
import com.dao.OrderMapper;
import com.entity.DetailProduct;
import com.entity.EasybuyOrder;
import com.entity.EasybuyOrderDetail;
import com.entity.EasybuyProduct;
import com.entity.EasybuyUser;
import com.service.order.impl.EasybuyOrderServiceImpl;
import com.service.product.impl.ProductServiceImpl;
import com.utils.EmptyUtils;
import com.utils.MyBatisUtil;
import com.utils.Pager;
import com.utils.ReturnResult;
import com.web.AbstractServlet;

/**
 * 订单详情控制层！ 
 * Servlet implementation class EasybuyOrderServlet
 */
@WebServlet("/EasybuyOrderServlet")
public class EasybuyOrderServlet extends AbstractServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EasybuyOrderServlet() {
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
	 * 订单列表  获得用户订单
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public String index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int currentPage = 1; //默认显示第一页
		int userId = ((EasybuyUser)request.getSession().getAttribute("easybuyUserLogin")).getId(); //从session中获取userId
		//如果传递的currentPage不为空
		if(null != request.getParameter("currentPage")){
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		}
		SqlSession ss = MyBatisUtil.getSqlSession();
		EasybuyOrderServiceImpl eosi = new EasybuyOrderServiceImpl(ss.getMapper(OrderMapper.class),ss.getMapper(OrderDetailMapper.class));
		//得到该用户所有订单条数
		int pageCount = eosi.getOrderCount(userId); 
		//System.out.println(pageCount);
		//创建分页对象
		Pager pager = new Pager(pageCount,2,currentPage);
		pager.setUrl("EasybuyOrderServlet?action=index");
		//查询该用户所有的订单
		List<EasybuyOrder> list = eosi.findEasybuyOrderList(userId,pager);
		List<EasybuyOrderDetail> list2 = null;  //订单中具体商品的集合
		EasybuyOrder order = null;
		//循环遍历订单
		for(int i=0;i<list.size();i++){
			order = list.get(i); //得到其中一个订单
			list2 = eosi.getOrderDetail(order.getId());
			//System.out.println(order.getId());
			//循环得到该订单中所有商品信息
			for(EasybuyOrderDetail detail : list2){
				//System.out.println(detail.getName());
				order.addOrderDetail(detail); //将订单中的每个商品添加到集合中
			}
		}
		//System.out.println("第一个订单中的第二个商品名:"+list.get(0).getOrderdetail().get(1).getName());
		//System.out.println("第二个订单中的第一个商品名:"+list.get(1).getOrderdetail().get(0).getName());
		//存储到request中
		ss.commit();
		MyBatisUtil.closeSqlSession(ss);
		request.setAttribute("orderList", list);
		request.setAttribute("pager", pager);
		request.setAttribute("isAll", "1");  //判断管理员点击的是全部订单还是我的订单
		return "backend/order/orderList";
	}

	/**
	 * 查询订单明细！
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public String queryOrderDeatil(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int orderId = Integer.parseInt(request.getParameter("orderId")); //订单编号
		SqlSession ss = MyBatisUtil.getSqlSession();
		EasybuyOrderServiceImpl eosi = new EasybuyOrderServiceImpl(ss.getMapper(OrderMapper.class),ss.getMapper(OrderDetailMapper.class));
		//得到该订单的详情信息
		List<EasybuyOrderDetail> detailList = eosi.getOrderDetail(orderId);
		MyBatisUtil.closeSqlSession(ss);
		//存储到request中
		request.setAttribute("orderId", orderId);
		request.setAttribute("listOrderDetail", detailList);
		
		return "/backend/order/orderDetailList";
	}

	/**
	 * 全部订单！
	 */
	public String queryAllOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int currentPage = 1; //默认第一页
		//判断是否传递currentPage
		if(null != request.getParameter("currentPage")){
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		}
		SqlSession ss = MyBatisUtil.getSqlSession();
		EasybuyOrderServiceImpl eosi = new EasybuyOrderServiceImpl(ss.getMapper(OrderMapper.class),ss.getMapper(OrderDetailMapper.class));
		//总订单条数
		int count = eosi.getTotalCount();
		//创建分页类
		Pager pager = new Pager(count,2,currentPage);
		//设置pager url
		pager.setUrl("EasybuyOrderServlet?action=queryAllOrder");
		//得到所有订单
		List<EasybuyOrder> orList = eosi.getAllOrder(pager);
		List<EasybuyOrderDetail> deList = null;
		EasybuyOrder order = null; //订单
		EasybuyOrderDetail orderdetail = null; //订单详情
		//循环订单
		for(int i=0;i<orList.size();i++){
			order = orList.get(i); //得到一个订单
			//查询该订单详情
			deList = eosi.getOrderDetail(order.getId());
			//循环遍历订单详情
			for(EasybuyOrderDetail detail : deList){
				//向订单表的 订单详情集合中添加数据
				order.addOrderDetail(detail);
			}
		}
		ss.commit();
		MyBatisUtil.closeSqlSession(ss);
		//存储到作用域中
		request.setAttribute("orderList", orList);
		request.setAttribute("pager", pager);
		return "/backend/order/orderList";
	}
	/**
	 * 确认收货
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ReturnResult confirmOrder(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ReturnResult result = new ReturnResult();
		int orderId = Integer.parseInt(request.getParameter("orderId"));
		int currentPage = Integer.parseInt(request.getParameter("currentPage")); //在第几页进行操作
		SqlSession ss = MyBatisUtil.getSqlSession();
		EasybuyOrderServiceImpl eosi = new EasybuyOrderServiceImpl(ss.getMapper(OrderMapper.class),ss.getMapper(OrderDetailMapper.class));
		//得到结果
		int count = eosi.confrimOrder(orderId);
		ss.commit();
		MyBatisUtil.closeSqlSession(ss);
		if(count > 0){
			request.setAttribute("currentPage", currentPage);
			return result.returnSuccess("收货成功!");
		}else{
			return result.returnFail("收货失败!请联系管理员!");
		}
	}
	@Override
	public Class getServletClass() {
		return EasybuyOrderServlet.class;
	}

}
