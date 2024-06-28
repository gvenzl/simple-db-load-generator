/*
 * Since: September, 2012
 * Author: gvenzl
 * Name: ParameterTest.java
 * Description: Tests the Parameters class.
 *
 * Copyright 2012 Gerald Venzl
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

import com.gvenzl.parameters.Parameter;
import org.junit.Assert;
import org.junit.Test;

import com.gvenzl.parameters.Parameters;

public class ParameterTest {

    @Test
    public void test_values()
    {
        Assert.assertEquals("dbType", Parameter.DB_TYPE.toString());
        Assert.assertEquals("verbose", Parameter.VERBOSE.toString());
        Assert.assertEquals("host", Parameter.HOST.toString());
        Assert.assertEquals("password", Parameter.PASSWORD.toString());
        Assert.assertEquals("port", Parameter.PORT.toString());
        Assert.assertEquals("sessions", Parameter.SESSIONS.toString());
        Assert.assertEquals("dbName", Parameter.DB_NAME.toString());
        Assert.assertEquals("inputFile", Parameter.INPUT_FILE.toString());
        Assert.assertEquals("user", Parameter.USER.toString());
    }
}
