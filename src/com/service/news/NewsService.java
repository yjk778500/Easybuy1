package com.service.news;

import java.util.List;
import com.entity.EasybuyNews;
import com.utils.Pager;
/**
 * 资讯列表业务逻辑层！
 * @author Administrator
 *
 */
public interface NewsService {
	/**
	 * 获取资讯列表业务！
	 * @param pager
	 * @return
	 */
	public List<EasybuyNews> queryNewsList(Pager pager);
	/**
	 * 获取资讯列表总记录数业务！
	 * @return
	 */
	public int getTotalCount();
	/**
	 * 根据ID获取资讯列表详情业务！
	 * @param id
	 * @return
	 */
	public EasybuyNews findNewsById(int id);
	
}
