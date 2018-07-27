package com.ypm.data;

import java.sql.SQLException;
import javax.sql.DataSource;
import com.sunsw.sql.PooledDataSource;

public class DataFactory {

	private static DataFactory factory = null;

	public DataFactory() {
		factory = this;
	}

	private static DataFactory getFactory() {
		if (factory == null) {
			factory = new DataFactory();
		}
		return factory;
	}

	public static void setJWrapper() throws SQLException {
		PooledDataSource ds = PooledDataSource.DS;
		if (ds == null) {
			ds = new PooledDataSource();
			ds.setDriver("org.gjt.mm.mysql.Driver");
			
			// yingmiao
			ds.setJdbcUrl("jdbc:mysql://121.196.195.179:6500/ymgr?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&useSSL=true&verifyServerCertificate=false");
			ds.setUsername("root");
			ds.setPassword("123Y567");
		}
		getFactory().setDataSource(ds);
	}

	/** 主数据源 */
	public void setDataSource(DataSource dataSource) {
		if (dataSource == null) {
			// Ignored
		} else {
			JPrepare.jw = this.initConfig(dataSource, true);
			JPrepare.jw.setDataSource(dataSource);
		}
	}

	private JWrapper initConfig(DataSource ds, boolean create) {
		String url = "jdbc:mysql://localhost:3306";
		String driver = null, user = null, pass = null;
		try {
			if (ds instanceof PooledDataSource) {
				PooledDataSource s = (PooledDataSource) ds;
				driver = s.getDriver();
				url = s.getJdbcUrl();
				user = s.getUsername();
				pass = s.getPassword();
			}
			return new MySQL(create, driver, url, user, pass);
		} catch (Throwable e) {
			return null;
		} finally {
			driver = user = pass = url = null;
		}
	}
}
