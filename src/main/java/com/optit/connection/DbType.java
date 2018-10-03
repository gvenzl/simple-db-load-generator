package com.optit.connection;

/**
 * This enumeration represents the supported databases/stores to generate load against
 * @author gvenzl
 *
 */
public enum DbType {
	ORACLE,
	MYSQL,
	NOSQL;
	
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
			case "nosql": return NOSQL;
			default: return null;
		}
	}
}
