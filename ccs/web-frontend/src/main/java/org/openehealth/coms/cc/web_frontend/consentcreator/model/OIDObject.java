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

package org.openehealth.coms.cc.web_frontend.consentcreator.model;

/**
 * This class represents an Object whose main characteristics are the presence
 * of an OID and the presence of an identifier.
 * 
 * @author Lennart Koester
 * @version 1.0 14.02.2011
 * 
 */
public class OIDObject {

	private String identifier = "";
	private String name = "";
	private boolean hasChildren = false;
	private boolean isActive = false;

	/**
	 * @param identifier
	 * @param name
	 * @param isActive
	 */
	public OIDObject(String identifier, String name, boolean isActive) {
		this.identifier = identifier;
		this.name = name;
		this.isActive = isActive;
	}

	/**
	 * Sets the status whether the patient has children
	 * 
	 * @param bool
	 */
	public void setHasChildren(boolean bool) {
		hasChildren = bool;
	}

 
	/**
	 * Returns boolean whether patient has children
	 * @return hasChildren
	 */
	public boolean hasChildren() {
		return hasChildren;
	}

	/**
	 * Returns the identifier
	 * 
	 * @return identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Returns the Name
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the status of the activity
	 * 
	 * @return isActive
	 */
	public boolean isActive() {
		return isActive;
	}

	@Override
	public String toString() {
		return name;
	}
}
