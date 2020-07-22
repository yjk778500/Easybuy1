package com.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.entity.EasybuyUser;
import com.utils.EmptyUtils;

/**
 *控制访问权限/未登录不允许用户访问有些页面！访问控制过滤器！
 * Servlet Filter implementation class EncodingFilter
 */
@WebFilter(filterName="loginFilter",urlPatterns= {"/EasybuyOrderServlet","/EasybuyUserAddressServlet","/ProductCategoryService"})

public class loginFilter implements Filter {
	String extUrl = "queryUserList";
	
	public void destroy() {
		
	}

	/**
	 * 对所有页面设置字符集!
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		HttpSession session =req.getSession();
		EasybuyUser user = (EasybuyUser)session.getAttribute("easybuyUserLogin");
		if(user == null) {
			// 跳转登录界面！
			resp.sendRedirect(req.getContextPath()+"/pre/Login.jsp");
			return;
		}
		String action = req.getParameter("action");
		if(EmptyUtils.isEmpty(action)) {
			resp.sendRedirect(req.getContextPath()+"/pre/Login.jsp");
		}else {
			if(extUrl.contains(action) && user.getType() != 1) {
				resp.sendRedirect(req.getContextPath()+"/pre/Login.jsp");
				return;
			}
		}
		chain.doFilter(request, response);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}
}
