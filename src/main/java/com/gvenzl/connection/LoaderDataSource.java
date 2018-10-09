/*
 * Since: October, 2013
 * Author: gvenzl
 * Name: LoaderDataSource.java
 * Description:
 *
 * Copyright 2018 Gerald Venzl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
