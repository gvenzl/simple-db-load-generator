/*
 * Since: October, 2013
 * Author: gvenzl
 * Name: LoaderDataSourceTest.java
 * Description: Tests the LoaderDataSource class
 *
 * Copyright 2013 Gerald Venzl
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

package com.gvenzl.test;

import oracle.jdbc.pool.OracleDataSource;

import org.junit.Test;

import com.gvenzl.connection.LoaderDataSource;

import java.sql.SQLException;

public class LoaderDataSourceTest {

	@Test
	public void test_ConstructorDataSource() throws SQLException {
		new LoaderDataSource(new OracleDataSource());
	}
}
