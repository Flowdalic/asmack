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

package org.apache.harmony.auth;

import javax.security.auth.Subject;

/**
 * The implementation for this interface provides a way to determine whether
 * this object implies a specified subject. Objects that implements
 * java.security.Principle normally implement this interface. *
 */
public interface PrincipalComparator {

    /**
     * Determine whether this object implies the specified subject.
     * 
     * @param subject
     *            The subject to be compared.
     * @return true if this object implies the subject.
     */
    public boolean implies(Subject subject);
}
