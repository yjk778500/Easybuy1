package com.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
/**
 * 控制页面/jsp文件乱码过滤器！！
 * Servlet Filter implementation class EncodingFilter
 */
@WebFilter(urlPatterns= {"/*"},
filterName="EncodingFilter",
initParams= {@WebInitParam(name="encode",value="utf-8")})
public class EncodingFilter implements Filter {

	private String encode =null;

	public void destroy() {
		encode = null;
	}

	// 对所有页面设置字符集
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (null == request.getCharacterEncoding()) {
			request.setCharacterEncoding(encode);
		}
		chain.doFilter(request, response);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		String encode = filterConfig.getInitParameter("encode");
		if (this.encode == null) {
			this.encode = encode;
		}
	}
}
