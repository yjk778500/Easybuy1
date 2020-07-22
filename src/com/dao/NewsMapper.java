package com.dao;

import java.util.List;

import com.entity.EasybuyNews;
import com.utils.Pager;

/**
 * 资讯列表数据访问层！
 * 
 * @author Administrator
 *
 */
public interface NewsMapper {
	/**
	 * 查询所有资讯列表！
	 * 
	 * @param pager
	 * @return
	 */
	public List<EasybuyNews> queryNewsList(Pager pager);

	/**
	 * 获取资讯列表总记录数！
	 * 
	 * @return
	 */
	public int getTotalCount();

	/**
	 * 根据ID查询资讯列表详情！
	 * 
	 * @param id
	 * @return
	 */
	public EasybuyNews getNewsById(int id);
}
