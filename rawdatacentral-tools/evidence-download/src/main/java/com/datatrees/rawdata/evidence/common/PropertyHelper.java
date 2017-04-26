package com.datatrees.rawdatacentral.evidence.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyHelper {
	private static String host;
	private static String username;
	private static String password;
	private static String database;

	static {
		Properties prop = new Properties();
		InputStream in = Object.class.getResourceAsStream("/db.properties");
		try {
			prop.load(in);
			host = prop.getProperty("dtboss.db.host").trim();
			username = prop.getProperty("dtboss.db.username").trim();
			password = prop.getProperty("dtboss.db.password").trim();
			database = prop.getProperty("dtboss.db.database").trim();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 私有构造方法，不需要创建对象
	 */
	private PropertyHelper() {
	}

	public static String getHost() {
		return host;
	}

	public static String getUsername() {
		return username;
	}

	public static String getPassword() {
		return password;
	}

	public static String getDatabase() {
		return database;
	}

}
