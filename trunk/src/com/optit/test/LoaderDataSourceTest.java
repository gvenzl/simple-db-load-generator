package com.optit.test;

import junit.framework.TestCase;
import oracle.kv.KVStoreConfig;

import org.junit.Test;

import sun.jdbc.odbc.ee.DataSource;

import com.optit.connection.LoaderDataSource;

public class LoaderDataSourceTest extends TestCase
{
	@Test
	public void test_ConstructorDataSource() {
		new LoaderDataSource(new DataSource());
	}
	
	@Test
	public void test_ConstructorKvStoreConfig() {
		new LoaderDataSource(new KVStoreConfig("",""));
	}
}
