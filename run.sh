#!/bin/bash
#
# Since: September, 2012
# Author: gvenzl
# Name: run.sh
# Description: A unix run script for the loader.
#
# Copyright 2012 Gerald Venzl
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

JAVA_HOME=/usr/java/latest
export JAVA_HOME

PATH=$JAVA_HOME/bin:$PATH
export PATH

java -jar ./SimpleDBLoadGenerator-*.jar $@
