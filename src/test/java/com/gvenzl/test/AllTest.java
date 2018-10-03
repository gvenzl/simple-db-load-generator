package com.gvenzl.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author gvenzl
 *
 */
public class AllTest extends TestSuite
{
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for all JUnit tests");
		suite.addTestSuite(SimpleLoadGeneratorTest.class);
		suite.addTestSuite(LoggerTest.class);
		suite.addTestSuite(ParameterTest.class);
		suite.addTestSuite(CommandsReaderTest.class);
		suite.addTestSuite(RandomIteratorTest.class);
		suite.addTestSuite(CommandTest.class);
		suite.addTestSuite(LoaderDataSourceTest.class);
		
		return suite;
	}
}
