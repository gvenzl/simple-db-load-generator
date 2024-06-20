/*
 * Since: October, 2013
 * Author: gvenzl
 * Name: DbType.java
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

/**
 * This enumeration represents the supported databases/stores to generate load against
 * @author gvenzl
 *
 */
public enum DbType {
	ORACLE,
	MYSQL,
	NOTSUPPORTED;
	
	/**
	 * Returns the database type
	 * @param type The string to evaluate for the appropriate database type
	 * @return The database type or null if no appropriate type could be found
	 */
	public static DbType getType(String type) {
		switch (type)
		{
			case "oracle": return ORACLE;
			case "mysql": return MYSQL;
			default: return NOTSUPPORTED;
		}
	}
}
