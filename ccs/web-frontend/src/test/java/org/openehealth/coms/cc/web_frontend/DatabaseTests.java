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

package org.openehealth.coms.cc.web_frontend;

import static org.junit.Assert.*;

import java.util.Date;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.openehealth.coms.cc.web_frontend.consentcreator.model.User;
import org.openehealth.coms.cc.web_frontend.consentcreator.service.Database;


/**
 * Tests for the class Database
 * 
 * @author Lennart Koester
 * @version 1.0 14.02.2011
 *
 */

public class DatabaseTests {

	Database database = new Database();
	
	@Test
	public void testDatabaseMethods() {
		try {
			User user1 = new User();
			
			user1.setActive(1);
			user1.setBirthdate(new Date());
			user1.setCity("Heidelberg");
			user1.setEmailaddress("allyourbase@arebelongto.us");
			user1.setForename("Homo");
			user1.setName("Heidelbergensis");
			user1.setGender("male");
			user1.setID("999999");
			user1.setPassword("admin");
			user1.setPrivileges(0);
			user1.setStreet("Sesamstra�e");
			user1.setZipcode(69120);
			user1.setPassword(RandomStringUtils.randomNumeric(64));
			
			assertTrue(database.storeUser(user1));
			
			assertTrue(database.existsUser(user1));
			
			assertNotNull(database.retrieveUserByEmail(user1.getEmailaddress()));
			
			assertNotNull(database.retrieveUser(user1.getName(), user1.getForename(), user1.getBirthdate(), user1.getGender()));
			
			assertNotNull(database.retrieveUserByID(user1.getID()));
			
			assertTrue(database.deactivateUser(user1.getID()));
			
			assertTrue(database.activateUser(user1.getID()));
			
			String s = database.storePasswordRequest(user1);
			
			assertTrue(database.existsPasswordRequest(s, false));
			
			assertNotNull(database.retrieveUserByPasswordRequest(s));
			
			assertTrue(database.removeRefID(s));
			
			assertFalse(database.retrievePassword(user1.getEmailaddress()).equalsIgnoreCase(""));
			
			assertTrue(database.updateUser(user1));
			
			assertTrue(database.updateUserPassword(user1.getID(), user1.getPassword()));
			
			assertTrue(database.retrievePassword(user1.getEmailaddress()).equalsIgnoreCase(user1.getPassword()));
			
			assertTrue(database.storeConflict(user1.getID()));
			
			assertTrue(database.showConflicts().size() != 0);
			
			assertTrue(database.removeConflict(user1.getID()));
			
			assertFalse(database.storeUnclearedConsent(user1.getID(), "abc", 1).equalsIgnoreCase(""));
			
			assertTrue(database.existsUnclearedConsent(user1.getID()));
			
			assertFalse(database.retrieveUnclearedConsentFilename(user1.getID()).equalsIgnoreCase(""));
			
			assertTrue(database.showUnclearedConsents().size() != 0);
			
			assertTrue(database.removeUnclearedConsent(user1.getID()));
			
			assertTrue(database.removeUser(user1.getID()));
			
			assertFalse(database.existsUser(user1));
			

		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
