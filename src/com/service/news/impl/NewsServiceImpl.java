package com.service.news.impl;
import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;

import com.dao.BaseDao;
import com.dao.NewsMapper;
import com.entity.EasybuyNews;
import com.service.news.NewsService;
import com.utils.DataBaseUtil;
import com.utils.Pager;
/**
 * 资讯列表业务逻辑层实现类！
 * @author Administrator
 *
 */
public class NewsServiceImpl implements NewsService {

	/**
	 * 使用Logger记录日志！
	 */
	public static Logger logger = Logger.getLogger(BaseDao.class.getName());
	
	private NewsMapper newsmapper = null;
	
	public NewsServiceImpl(NewsMapper newsmapper) {
		super();
		this.newsmapper = newsmapper;
	}

	@Override
	/**
	 * 获取资讯列表业务！
	 * @param pager
	 * @return
	 */
	public List<EasybuyNews> queryNewsList(Pager pager) {
		return newsmapper.queryNewsList(pager);
	}

	@Override
	/**
	 * 获取资讯列表总记录数业务！
	 * @return
	 */
	public int getTotalCount() {
		return newsmapper.getTotalCount();
	}
	
	/**
	 * 根据ID获取资讯列表详情业务！
	 * @param id
	 * @return
	 */
	public EasybuyNews findNewsById(int id) {
		return newsmapper.getNewsById(id);
	}

}
