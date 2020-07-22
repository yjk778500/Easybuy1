package com.web.pre;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;

import com.dao.EasybuyCollectMapper;
import com.dao.EasybuyUserAddressMapper;
import com.dao.OrderDetailMapper;
import com.dao.OrderMapper;
import com.dao.ProductCategoryMapper;
import com.entity.EasybuyCollect;
import com.entity.EasybuyOrder;
import com.entity.EasybuyOrderDetail;
import com.entity.EasybuyProduct;
import com.entity.EasybuyUser;
import com.entity.EasybuyUserAddress;
import com.entity.EasybuyProductCategory;
import com.service.address.impl.EasybuyUserAddressServiceImpl;
import com.service.order.impl.CartServiceImpl;
import com.service.order.impl.EasybuyOrderServiceImpl;
import com.service.product.impl.ProductCategoryServiceImpl;
import com.service.product.impl.ProductServiceImpl;
import com.utils.Constants;
import com.utils.EmptyUtils;
import com.utils.MyBatisUtil;
import com.utils.ReturnResult;
import com.utils.ShoppingCart;
import com.utils.ShoppingCartItem;
import com.utils.StringUtils;
import com.web.AbstractServlet;

/**
 * 购物车控制层！
 * @author Administrator
 *
 */
@WebServlet("/CartServlet")
public class CartServlet extends AbstractServlet {

	@Override
	public Class getServletClass() {
		return CartServlet.class;
	}
	
	
	/**
	 * 添加到购物车！
	 *
	 * @return
	 */
	public ReturnResult add(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int productId = Integer.parseInt(request.getParameter("productId"));
		int num = Integer.parseInt(request.getParameter("num"));  
		int userId = Integer.parseInt(request.getParameter("userId"));
		int type = 1; //购物车
		ReturnResult result = new ReturnResult();
		//判断添加的商品是否已经存在,如果存在则数量+1
		SqlSession ss = MyBatisUtil.getSqlSession();
		CartServiceImpl csi = new CartServiceImpl(ss.getMapper(EasybuyCollectMapper.class));
		int flag  = csi.checkCart(userId, productId);
		if(flag > 0 ){ //商品已经存在
			int addnum = csi.addNum(userId, productId); //该商品的数量+1
			ss.commit();
			MyBatisUtil.closeSqlSession(ss);
			return result.returnSuccess();
		}else{
			//商品不存在
			int count = csi.addCollect(userId, productId, num, type);
			ss.commit();
			MyBatisUtil.closeSqlSession(ss);
			if(count > 0){
				return result.returnSuccess();
			}else{
				return result.returnFail("添加失败!");
			}
		}
		
	}

	/**
	 * 添加到收藏！
	 *
	 * @return
	 */
	public ReturnResult addCollect(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int id = Integer.parseInt(request.getParameter("id"));
		int type = Integer.parseInt(request.getParameter("type"));
		EasybuyUser user = (EasybuyUser)request.getSession().getAttribute("easybuyUserLogin");
		int userid = user.getId();
		int productNum = 1;
		ReturnResult result = new ReturnResult();
		SqlSession ss = MyBatisUtil.getSqlSession();
		CartServiceImpl csi = new CartServiceImpl(ss.getMapper(EasybuyCollectMapper.class));
		//判断用户是否重复收藏
		int flag = csi.checkCollect(userid, id);
		if(flag > 0){
			ss.commit();
			MyBatisUtil.closeSqlSession(ss);
			return result.returnFail("该商品已在收藏夹！");
		}else{//不存在
			int count = csi.addCollect(userid, id, productNum, type);
			if(count > 0){
				ss.commit();
				MyBatisUtil.closeSqlSession(ss);
				return result.returnSuccess("收藏成功");
			}else{
				return result.returnFail("收藏失败");
			}
		}
		
	}
	
	/**
	 * 移除收藏
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ReturnResult delCollect(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ReturnResult result = new ReturnResult();
		EasybuyCollect ebc = new EasybuyCollect();
		int type = 0; //收藏
		int productId = Integer.parseInt(request.getParameter("id"));
		int userId = Integer.parseInt(request.getParameter("userId"));
		SqlSession ss = MyBatisUtil.getSqlSession();
		CartServiceImpl csi = new CartServiceImpl(ss.getMapper(EasybuyCollectMapper.class));
		ebc.setUserId(userId);
		ebc.setProductId(productId);
		ebc.setType(type);
		int count = csi.delCollect(ebc);
		ss.commit();
		MyBatisUtil.closeSqlSession(ss);
		if(count > 0){
			return result.returnSuccess("移除成功!");
		}else{
			return result.returnFail("移除失败!");
		}
	}
	
	/**
	 * 刷新购物车！
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	public String refreshCart(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		ShoppingCart sessionSC = this.getCartFromSession(request);
		//清空之前的购物车集合
		session.removeAttribute("cart");
		int userId = ((EasybuyUser)session.getAttribute("easybuyUserLogin")).getId(); //用户id
		//购物车工具类
		ShoppingCart sc = new ShoppingCart();
		SqlSession ss = MyBatisUtil.getSqlSession();
		CartServiceImpl csi = new CartServiceImpl(ss.getMapper(EasybuyCollectMapper.class));
		//得到用户购物车中所有商品
		List<EasybuyCollect> listec  = csi.selectId(userId);
		MyBatisUtil.closeSqlSession(ss);
		//遍历每个购物车,并得到每个购物车中具体商品
		for(int i=0;i<listec.size();i++){
			List<EasybuyProduct> listep = listec.get(i).getProduct();
			System.out.println("listec:"+listec.size());
			System.out.println("listep:"+listep.size());
				//添加到购物车类中
				sc.addItem(listep.get(0), listec.get(i).getProductNum());
				//设置商品的数量
				sc.getItems().get(i).setQuantity(listec.get(i).getProductNum());
		}
		sc.getTotalCost(); //计算总价格
		//如果session中没有购物车对象,则新存储一个
		if(null != request.getParameter("flag") && request.getParameter("flag").equals("1")){ //添加商品加入购物车
			session.setAttribute("cart", sc);
		}else{//读取session中的购物车
			session.setAttribute("cart", sessionSC);
		}
		return "/common/pre/searchBar";
	}
	/**
	 * 当点击继续购物按钮时  先重新加载修改后的购物车信息 再跳去首页继续购物
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String reCartToIndex(HttpServletRequest request, HttpServletResponse response) throws Exception{
		this.refreshCart(request, response);
		//System.out.println("回显的"+this.getCartFromSession(request).getItems().get(0).getQuantity());
		return "pre/Index";
	}
	/**
	 * 从session中获取购物车
	 *
	 * @param request
	 * @return
	 */
	private ShoppingCart getCartFromSession(HttpServletRequest request) throws Exception {
		ShoppingCart sc = (ShoppingCart)request.getSession().getAttribute("cart");
		return sc;
	}
	
	/**
	 * 购物车订单中 数量+1
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ReturnResult addNum(HttpServletRequest request, HttpServletResponse response) throws Exception{
		int index = Integer.parseInt(request.getParameter("index"));
		int quantity = Integer.parseInt(request.getParameter("quantity"))+1;  //数量加1
		int userId = Integer.parseInt(request.getParameter("userId")); //用户id
		int productId = Integer.parseInt(request.getParameter("productId"));
		ShoppingCart sc = this.getCartFromSession(request); //得到session中存储的购物车对象
		sc.modifyQuantity(index, quantity); //修改购物车中商品的数量
		SqlSession ss = MyBatisUtil.getSqlSession();
		CartServiceImpl csi = new CartServiceImpl(ss.getMapper(EasybuyCollectMapper.class));
		//修改数据库中 购物车商品的数量+1
		csi.addNum(userId, productId);
		ss.commit();
		MyBatisUtil.closeSqlSession(ss);
		//删除之前session中存储的购物车对象
		request.getSession().removeAttribute("cart");
		//重新存储修改后的购物车对象
		request.getSession().setAttribute("cart", sc);
		return new ReturnResult().returnFail("返回-1,从session中获取购物车");
	}
	/**
	 * 购物车订单中 数量-1
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ReturnResult jianNum(HttpServletRequest request, HttpServletResponse response) throws Exception{
		int index = Integer.parseInt(request.getParameter("index"));
		int quantity = Integer.parseInt(request.getParameter("quantity"))-1;  //数量减一
		int userId = Integer.parseInt(request.getParameter("userId")); //用户id
		int productId = Integer.parseInt(request.getParameter("productId"));
		ShoppingCart sc = this.getCartFromSession(request); //得到session中存储的购物车对象
		sc.modifyQuantity(index, quantity); //修改购物车中商品的数量
		SqlSession ss = MyBatisUtil.getSqlSession();
		CartServiceImpl csi = new CartServiceImpl(ss.getMapper(EasybuyCollectMapper.class));
		//修改数据库中 购物车商品的数量-1
		csi.jianNum(userId, productId);
		ss.commit();
		MyBatisUtil.closeSqlSession(ss);
		//删除之前session中存储的购物车对象
		request.getSession().removeAttribute("cart");
		//重新存储修改后的购物车对象
		request.getSession().setAttribute("cart", sc);
		return new ReturnResult().returnFail("返回-1,从session中获取购物车");
	}
	/**
	 * 跳到结算页面！
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	public String toSettlement(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SqlSession ss = MyBatisUtil.getSqlSession();
		ProductCategoryServiceImpl pcsi = new ProductCategoryServiceImpl(ss.getMapper(ProductCategoryMapper.class));
		//加载分类
		List<EasybuyProductCategory> list1 = pcsi.getProductCategoryListOne();
		List<EasybuyProductCategory> list2 = pcsi.getProductCategoryListTwo();
		List<EasybuyProductCategory> list3 = pcsi.getProductCategoryListThree();
		MyBatisUtil.closeSqlSession(ss);
		//存到request作用域中
		request.setAttribute("list", list1);
		request.setAttribute("list2", list2);
		request.setAttribute("list3", list3);
		return "/pre/settlement/toSettlement";

	}

	/**
	 * 跳转到购物车页面！
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	public String settlement1(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SqlSession ss = MyBatisUtil.getSqlSession();
		ProductCategoryServiceImpl pcsi = new ProductCategoryServiceImpl(ss.getMapper(ProductCategoryMapper.class));
		//加载分类
		List<EasybuyProductCategory> list1 = pcsi.getProductCategoryListOne();
		List<EasybuyProductCategory> list2 = pcsi.getProductCategoryListTwo();
		List<EasybuyProductCategory> list3 = pcsi.getProductCategoryListThree();
		//存到request作用域中
		MyBatisUtil.closeSqlSession(ss);
		request.setAttribute("list", list1);
		request.setAttribute("list2", list2);
		request.setAttribute("list3", list3);
		return "/pre/settlement/settlement1";
	}

	/**
	 * 初始化订单信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */

	public String settlement2(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//加载用户的地址
		int userId = Integer.parseInt(request.getParameter("userId")); //用户id
		SqlSession ss = MyBatisUtil.getSqlSession();
		EasybuyUserAddressServiceImpl easi = new EasybuyUserAddressServiceImpl(ss.getMapper(EasybuyUserAddressMapper.class));
		List<EasybuyUserAddress> list = easi.getEasybuyUserAddressAll(userId);
		MyBatisUtil.closeSqlSession(ss);
		//存储到request中
		request.setAttribute("userAddressList", list);
		return "/pre/settlement/settlement2";
	}

	/**
	 * 生成订单！
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public Object settlement3(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int addressId = Integer.parseInt(request.getParameter("addressId")); //判断是否新增地址
		String newAddress = request.getParameter("newAddress");  //地址
		String newRemark = request.getParameter("newRemark");	//介绍
		int userId = ((EasybuyUser)request.getSession().getAttribute("easybuyUserLogin")).getId(); //获得当前登录用户的id
		//获得当前时间
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
		String time = sdf.format(date);
		SqlSession ss = MyBatisUtil.getSqlSession();
		EasybuyUserAddressServiceImpl easi = new EasybuyUserAddressServiceImpl(ss.getMapper(EasybuyUserAddressMapper.class));
		EasybuyOrderServiceImpl eosi = new EasybuyOrderServiceImpl(ss.getMapper(OrderMapper.class),ss.getMapper(OrderDetailMapper.class));
		//判断是否新增地址
		if(addressId == -1){
			EasybuyUserAddress address = new EasybuyUserAddress();
			address.setCreateTime(time);
			address.setUserId(userId);
			address.setIsDefault(0);
			address.setAddress(newAddress);
			address.setRemark(newRemark);
			easi.addUserAddress(address);  //新增地址
		}else{ //查找该编号的地址
			EasybuyUserAddress address = easi.getUserAddressById(addressId);
			newAddress = address.getAddress(); //得到查询的地址
			newRemark = address.getRemark(); //得到查询的介绍
		}
		//生成订单
		EasybuyOrder order = new EasybuyOrder();
		String loginName = ((EasybuyUser)request.getSession().getAttribute("easybuyUserLogin")).getLoginName(); //登录名
		double cost = this.getCartFromSession(request).getSum();  //总价
		int status = this.getCartFromSession(request).getItems().size(); //商品数量
		int type =1; 
		String serialNumber = StringUtils.randomUUID(); //订单号
		order.setUserId(userId);
		order.setLoginName(loginName);
		order.setUserAddress(newAddress);
		order.setCreateTime(date);
		order.setCost(cost);
		order.setType(type);
		order.setSerialNumber(serialNumber);
		order.setStatus(status);
		eosi.saveOrder(order); //保存订单
		//将订单中的每个商品 存储到 订单列表中
		int orderid = eosi.getorderByserialNumber(serialNumber); //根据订单号 查询订单id
		//得到购物车所有商品集合
		List<ShoppingCartItem> list = this.getCartFromSession(request).getItems();
		EasybuyOrderDetail detail = null;  //订单详情对象
		ShoppingCartItem item = null; //购物车对象
		//循环保存所有商品
		for(int i=0;i<list.size();i++){
			item = list.get(i);
			detail = new EasybuyOrderDetail();
			detail.setProductId(item.getProduct().getId()); //商品id
			detail.setQuantity(item.getQuantity()); //商品数量
			detail.setCost(item.getCost());  //商品价格
			eosi.saveOrderDetail(detail, orderid);
		}
		CartServiceImpl csi = new CartServiceImpl(ss.getMapper(EasybuyCollectMapper.class));
		//清空数据库中购物车信息
		csi.delCart(userId);
		ss.commit();
		MyBatisUtil.closeSqlSession(ss);
		//将订单存储到作用域中
		request.setAttribute("currentOrder", order);
		
		//订单生成后清空购物车
		request.getSession().removeAttribute("cart");
		return "/pre/settlement/settlement3";
	}

	/**
	 * 删除购物车中一条信息
	 *
	 * @param request
	 * @return
	 */
	public ReturnResult modCart(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int userId = Integer.parseInt(request.getParameter("userId"));
		int productId = Integer.parseInt(request.getParameter("productId"));
		int type = 1; //购物车
		int index = Integer.parseInt(request.getParameter("index"));
		ReturnResult result = new ReturnResult();
		EasybuyCollect ebc = new EasybuyCollect();
		ebc.setUserId(userId);
		ebc.setProductId(productId);
		ebc.setType(type);
		SqlSession ss = MyBatisUtil.getSqlSession();
		CartServiceImpl csi = new CartServiceImpl(ss.getMapper(EasybuyCollectMapper.class));
		int count = csi.delCollect(ebc);
		ss.commit();
		MyBatisUtil.closeSqlSession(ss);
		if(count > 0){
			return result.returnSuccess();
		}else{
			return result.returnFail("删除购物车失败!");
		}
	}
	
}
