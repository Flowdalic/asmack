/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.harmony.auth.module;

import java.util.Map;

public class DebugUtil {
	
	private boolean debugFlag = false;
	
	private StringBuilder loginDebugInfo = null;
	
	public DebugUtil(final Map<String,?> options){
		processDebugSwitch(options);
	}
	
	protected void recordDebugInfo(String debugInfo){
        if(debugFlag)
            loginDebugInfo.append(debugInfo);
    }
    
    protected void printAndClearDebugInfo(){
        if(debugFlag)
        {
            System.out.print(loginDebugInfo.toString());
            loginDebugInfo = new StringBuilder();
        }
    }
    
    private void processDebugSwitch(final Map<String,?> options){
    	Object optionValue = options.get("debug");
    	if (optionValue != null && optionValue.equals("true")) {
            debugFlag = true;
            loginDebugInfo = new StringBuilder();
        }
    }
}
