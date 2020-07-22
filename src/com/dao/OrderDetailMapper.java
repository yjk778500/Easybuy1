package com.dao;

import java.sql.SQLException;

import org.apache.ibatis.annotations.Param;

import com.entity.EasybuyOrderDetail;
/**
 * 订单详情
 * @author 99266
 *
 */
public interface OrderDetailMapper {
	//保存订单详情
    public void saveOrderDetail(@Param("detail")EasybuyOrderDetail detail, @Param("orderId")int orderId) throws SQLException ;
}
