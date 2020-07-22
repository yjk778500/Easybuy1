package com.service.order.impl;
import java.sql.Connection;
import java.util.List;

import com.dao.EasybuyCollectMapper;
import com.entity.EasybuyCollect;
import com.service.order.CartService;
import com.utils.DataBaseUtil;
/**
 * 购物车业务逻辑层实现类！！
 * @author Administrator
 *
 */
public class CartServiceImpl implements CartService {
	private EasybuyCollectMapper ecd = null;
	public CartServiceImpl(EasybuyCollectMapper ecd) {
		this.ecd = ecd;
	}

    /**
     * 添加收藏！
     */
	@Override
	public int addCollect(int userId, int productId, int productNum, int type) {
		return ecd.addCollect(userId, productId, productNum, type);
	}

	@Override
	/**
     * 根据ID查询购物车！
     * @param userId
     * @param productId
     * @return
     */
	public List<EasybuyCollect> selectId(int userId) {
		return ecd.selectId(userId);
	}

	@Override
	/**
     * 查询用户的收藏夹！
     * @param userId
     * @return
     */
	public List<EasybuyCollect> selectByUserId(int userId) {
		return ecd.selectByUserId(userId);
	}

	/**
	 * 删除
	 */
	public int delCollect(EasybuyCollect easybuyCollect) {
		return ecd.delCollect(easybuyCollect);
	}

	/**
	 * 判断用户是否重复收藏
	 */
	public int checkCollect(int userId, int productId) {
		return ecd.checkCollect(userId, productId);
	}

	/**
	 * 购物车商品数量+1
	 */
	public int addNum(int userId, int productId) {
		return ecd.addNum(userId, productId);
	}

	/**
	 * 购物车商品是否重复
	 */
	public int checkCart(int userId, int productId) {
		return ecd.checkCart(userId, productId);
	}

	/**
	 * 购物车商品数量-1
	 */
	public int jianNum(int userId, int productId) {
		return ecd.jianNum(userId, productId);
	}

	/**
	 * 清空该用户所有购物车
	 */
	public int delCart(int userId) {
		return ecd.delCart(userId);
	}

	
}
