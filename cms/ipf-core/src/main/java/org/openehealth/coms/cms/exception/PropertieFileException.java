/*
* Copyright 2012 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.openehealth.coms.cms.exception;

/**
 * Exception thrown if some problems with the properties file
 * occurred.
 * 
 * @author mbirkle
 *
 */
public class PropertieFileException extends Exception {

	private static final long serialVersionUID = 5459401386221704458L;

	public PropertieFileException() {
	}

	public PropertieFileException(String st) {
		super(st);
	}
}
