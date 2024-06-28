/*
 * Since: June 2024
 * Author: gvenzl
 * Name: Parameter.java
 * Description: The parameter enum
 *
 * Copyright 2024 Gerald Venzl
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

package com.gvenzl.parameters;

public enum Parameter {
    USER("user"),
    PASSWORD("password"),
    HOST("host"),
    PORT("port"),
    DB_NAME("dbName"),
    DB_TYPE("dbType"),
    SESSIONS("sessions"),
    INPUT_FILE("inputFile"),
    VERBOSE("verbose"),
    IGNORE_ERRORS("ignoreErrors");

    public final String label;

    private Parameter(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
