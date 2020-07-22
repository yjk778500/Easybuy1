package com.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MyBatisUtil {
	private static SqlSessionFactory factory;
	
	static {
		InputStream is = null;
		try {
			is = Resources.getResourceAsStream("MyBatis-Config.xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
		factory = new SqlSessionFactoryBuilder().build(is);
	}
	
	public static SqlSession getSqlSession() {
		return factory.openSession();
	}
	
	public static void closeSqlSession(SqlSession ss) {
		if(ss!= null) {
			ss.close();
		}
	}
}
