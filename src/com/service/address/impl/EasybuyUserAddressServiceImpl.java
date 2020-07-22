package com.service.address.impl;

import java.sql.Connection;
import java.util.List;

import com.dao.EasybuyUserAddressMapper;
import com.entity.EasybuyUserAddress;
import com.service.address.EasybuyUserAddressService;
import com.utils.DataBaseUtil;

/**
 * 用户地址信息业务逻辑层实现类！
 * 
 * @author Administrator
 *
 */
public class EasybuyUserAddressServiceImpl implements EasybuyUserAddressService {
	private EasybuyUserAddressMapper addressmapper = null;
	
	public EasybuyUserAddressServiceImpl(EasybuyUserAddressMapper addressmapper) {
		super();
		this.addressmapper = addressmapper;
	}

	@Override
	/**
	 * 根据用户ID查找对应的地址信息！
	 * 
	 * @param userId
	 * @return
	 */
	public List<EasybuyUserAddress> getEasybuyUserAddressAll(int userId) {
		return addressmapper.findEasybuyUserAddressAll(userId);
	}

	@Override
	/**
	 * 新增地址！
	 * 
	 * @param easybuyUserAddress
	 * @return
	 */
	public int addUserAddress(EasybuyUserAddress easybuyUserAddress) {
		return addressmapper.updateEasybuyUserAddressById(easybuyUserAddress);
	}

	@Override
	/**
	 * 根据用户ID获得相依地址信息！
	 * 
	 * @param id
	 * @return
	 */
	public EasybuyUserAddress getUserAddressById(int id) {
		return addressmapper.getUserAddressById(id);
	}

	// ********
	@Override
	/**
	 * 根据用户Id判断该编号是否存在地址信息！
	 * 
	 * @param userId
	 * @return
	 */
	public int getUserByIdAddress(int userId) {
		return 0;
	}
	// ********
}
