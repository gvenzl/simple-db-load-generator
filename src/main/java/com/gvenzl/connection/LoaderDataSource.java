package com.gvenzl.connection;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import oracle.kv.Consistency;
import oracle.kv.Durability;
import oracle.kv.FaultException;
import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.KVStoreFactory;

public class LoaderDataSource
{
	private DataSource dbDataSource;
	private KVStoreConfig kvStoreConfig;

	public LoaderDataSource(DataSource dbs) {
		dbDataSource = dbs;
	}
	
	public LoaderDataSource(KVStoreConfig kvsc) {
		kvStoreConfig = kvsc;
		kvStoreConfig.setConsistency(Consistency.ABSOLUTE);
		kvStoreConfig.setDurability(Durability.COMMIT_SYNC);
	}
	
	public Connection getDBConnection() throws SQLException {
		return dbDataSource.getConnection();
	}
	
	public KVStore getKVConnection() throws FaultException {
		return KVStoreFactory.getStore(kvStoreConfig);
	}

}
