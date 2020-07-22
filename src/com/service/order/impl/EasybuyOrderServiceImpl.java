package com.service.order.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.dao.BaseDao;
import com.dao.OrderDetailMapper;
import com.dao.OrderMapper;
import com.entity.EasybuyOrder;
import com.entity.EasybuyOrderDetail;
import com.service.order.EasybuyOrderService;
import com.utils.DataBaseUtil;
import com.utils.Pager;

/**
 * 订单信息业务逻辑层实现类！
 * 
 * @author Administrator
 *
 */
public class EasybuyOrderServiceImpl implements EasybuyOrderService {

	/**
	 * 使用Logger记录日志！
	 */
	public static Logger logger = Logger.getLogger(BaseDao.class.getName());
	
	private OrderMapper ordermapper = null;
	private OrderDetailMapper detailmapper = null;
	public EasybuyOrderServiceImpl(OrderMapper ordermapper,OrderDetailMapper detailmapper) {
		super();
		this.ordermapper = ordermapper;
		this.detailmapper = detailmapper;
	}
	/**
	 * 保存订单
	 * @param eo
	 */
	public void saveOrder(EasybuyOrder eo){
		ordermapper.saveOrder(eo);
	}
	/**
	 * 根据订单号 查询订单id
	 */
	public int getorderByserialNumber(String serialNumber) {
		return ordermapper.getorderByserialNumber(serialNumber);
	}
	/**
	 * 保存订单详情
	 */
	public void saveOrderDetail(EasybuyOrderDetail detail, int orderId) {
		try {
			detailmapper.saveOrderDetail(detail, orderId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	@Override
	/**
	 * 获得订单总条数
	 * 
	 * @return
	 */
	public int getTotalCount() {
		return ordermapper.getTotalCount();
	}
	// *********
	/**
	 * 得到该用户所有订单
	 */
	public List<EasybuyOrder> findEasybuyOrderList(int userId,Pager pager) {
		return ordermapper.findEasybuyOrderList(userId, pager);
	}
	/**
	 * 得到订单详情
	 */
	public List<EasybuyOrderDetail> getOrderDetail(int orderId) {
		return ordermapper.getOrderDetail(orderId);
	}
	/**
	 * 得到该用户订单条数
	 */
	public int getOrderCount(int userId) {
		return ordermapper.getOrderCount(userId);
	}
	/**
	 * 得到所有订单
	 */
	public List<EasybuyOrder> getAllOrder(Pager pager) {
		return ordermapper.getAllOrder(pager);
	}
	/**
	 * 确认订单
	 */
	public int confrimOrder(int orderId) {
		return ordermapper.confrimOrder(orderId);
	}
	/**
	 * 删除该用户所有订单
	 */
	public int delOrderByUserId(int userId) {
		return ordermapper.delOrderByUserId(userId);
	}
	/**
	 * 删除指定订单中的所有商品
	 */
	public int delOrderProductByOrderId(int orderId) {
		return ordermapper.delOrderProductByOrderId(orderId);
	}
}
