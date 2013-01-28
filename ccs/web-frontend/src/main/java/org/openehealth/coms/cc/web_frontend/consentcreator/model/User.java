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

import java.util.Date;

/**
 * This class represents a User registered to the service
 * 
 * @author Lennart Koester
 * @version 1.0 14.02.2011
 *
 */
public class User {
	private String name;
	private String forename;
	private Date birthdate;
	private int zipcode;
	private String street;
	private String city;
	private String gender;
	private String emailaddress;
	private String password;
	private String id = "-1";
	private int privileges = 0;
	private int active = 0;

	public User() {
		
	}

	/**
	 * Sets the name of the user 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the Name of the user
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the forename of the user
	 * @param forename
	 */
	public void setForename(String forename) {
		this.forename = forename;
	}

	/**
	 * Gets the forename of the user
	 * @return forename
	 */
	public String getForename() {
		return this.forename;
	}

	/**
	 * Sets the birthdate of the user
	 * @param birthdate
	 */
	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	/**
	 * Gets the birthdate of the user
	 * @return birthdate
	 */
	public Date getBirthdate() {
		return this.birthdate;
	}

	/**
	 * Sets the zipcode of the town where the user lives
	 * @param zipcode
	 */
	public void setZipcode(int zipcode) {
		this.zipcode = zipcode;
	}

	/**
	 * Gets the zipcode of the town where the user lives
	 * @return zipcode
	 */
	public int getZipcode() {
		return this.zipcode;
	}

	/**
	 * Sets the street where the user lives
	 * @param street
	 */
	public void setStreet(String street) {
		this.street = street;
	}

	/**
	 * Gets the street where the user lives
	 * @return street
	 */
	public String getStreet() {
		return this.street;
	}

	/**
	 * Sets the city where the user lives
	 * @param city
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Gets the city where the user lives
	 * @return city
	 */
	public String getCity() {
		return this.city;
	}

	/**
	 * Sets the gender of the user
	 * @param gender
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * Gets the gender of the user
	 * @return gender
	 */
	public String getGender() {
		return this.gender;
	}

	/**
	 * Sets the privileges
	 * @param privileges
	 */
	public void setPrivileges(int privileges) {
		this.privileges = privileges;
	}

	/**
	 * Gets the privileges
	 * @return privileges
	 */
	public int getPrivileges() {
		return this.privileges;
	}

	/**
	 * Sets the id 
	 * @param id
	 */
	public void setID(String id) {
		this.id = id;
	}

	/**
	 * Gets the id
	 * @return id
	 */
	public String getID() {
		return id;
	}

	/**
	 * Sets the emailaddress of the user
	 * @param emailaddress
	 */
	public void setEmailaddress(String emailaddress) {
		this.emailaddress = emailaddress;
	}

	/**
	 * Sets the emailaddress of the user
	 * @return emailaddress
	 */
	public String getEmailaddress() {
		return emailaddress;
	}

	/**
	 * Sets the the Status of the activity 
	 * @param active
	 */
	public void setActive(int active) {
		this.active = active;
	}

	/**
	 * Gets the Status of the activity
	 * @return active
	 */
	public int isActive() {
		return active;
	}

	/**
	 * Sets the password 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the password
	 * @return password
	 */
	public String getPassword() {
		return password;
	}
}