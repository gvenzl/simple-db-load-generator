package com.gvenzl.test;

import junit.framework.TestCase;
import oracle.jdbc.pool.OracleDataSource;
import oracle.kv.KVStoreConfig;

import org.junit.Test;

import com.gvenzl.connection.LoaderDataSource;

import java.sql.SQLException;

public class LoaderDataSourceTest extends TestCase
{
	@Test
	public void test_ConstructorDataSource() throws SQLException {
		new LoaderDataSource(new OracleDataSource());
	}
	
	@Test
	public void test_ConstructorKvStoreConfig() {
		new LoaderDataSource(new KVStoreConfig("",""));
	}
}
