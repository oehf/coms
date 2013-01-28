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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.openehealth.coms.cc.web_frontend.consentcreator.model.User;
import org.openehealth.coms.cc.web_frontend.consentcreator.service.DocumentFactory;
import org.openehealth.coms.cc.web_frontend.consentcreator.service.ServiceException;
import org.openehealth.coms.cc.web_frontend.consentcreator.service.Shipper;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


/**
 * Tests for the class Shipper
 * 
 * @author Lennart Koester
 * @version 1.0 14.02.2011
 *
 */

public class ShipperTests {

	Shipper shipper = new Shipper();
	DocumentFactory documentFactory = new DocumentFactory();
	
	@Test
	public void testShipperTests() {
		try {
			User user1 = new User();
			
			user1.setActive(1);
			user1.setBirthdate(new Date());
			user1.setCity("Heidelberg");
			user1.setEmailaddress("allyourbase@arebelongto.us");
			user1.setForename("Homo");
			user1.setName("Heidelbergensis");
			user1.setGender("male");
			user1.setID("1234567890");
			user1.setPassword("admin");
			user1.setPrivileges(0);
			user1.setStreet("Sesamstraße");
			user1.setZipcode(69120);
			
			assertTrue(shipper.registerUser(user1));
			
			assertTrue(!(shipper.existsUser(user1).equals("-1")));
			
			assertTrue(shipper.storeSignedConsent(documentFactory.constructCDA(user1, null, user1, true, null), user1));
		
			assertNotNull(shipper.getLatestPolicySet(user1.getID()));
			
			File consentLatest = new File("consentST.pdf");
			
			FileOutputStream outStream = new FileOutputStream(consentLatest);
			
			shipper.getLatestPDFConsent(user1.getID(), outStream);
			
			assertTrue(consentLatest.canRead());
			
			assertNotNull(shipper.getConsentList(user1.getID()));
			
			Document consentList = shipper.getConsentList(user1.getID());
			
			assertNotNull(consentList);
			
			File consent = new File("consent2ST.pdf");
			
			FileOutputStream outS = new FileOutputStream(consent);
			
			shipper.getSpecificPDFConsent(consentList.getDocumentElement().getElementsByTagName("ConsentDocument").item(1).getAttributes().item(1) +"^" + consentList.getDocumentElement().getElementsByTagName("ConsentDocument").item(1).getAttributes().item(0), outS);
			
			assertTrue(consent.canRead());
			
			assertNotNull(shipper.getTreeOrganisations());
			
			assertTrue(shipper.sendPasswordLinkToEmail(0, user1, "ref"));
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			Document domPolicySet = null;
			String policySetURL = DocumentFactoryTests.class.getResource("PolicySet.xml").toString();
			
			DocumentBuilder db = null;
			try {
				db = dbf.newDocumentBuilder();
				domPolicySet = db.parse(policySetURL);
			} catch (ParserConfigurationException e) {
				throw new ServiceException(e);
			} catch (SAXException e) {
				throw new ServiceException(e);
			} catch (IOException e) {
				throw new ServiceException(e);
			}
			
			Document cda = documentFactory.constructCDA(user1, domPolicySet, user1, true, null);
			
			assertTrue(shipper.sendUnclearedConsentHash(user1.getID(), documentFactory.getMD5Hash(cda)));
			
			assertTrue(shipper.rejectUnclearedConsent(user1.getID()));
			
			assertTrue(shipper.sendUnclearedConsentHash(user1.getID(), documentFactory.getMD5Hash(cda)));
			
		//	assertTrue(shipper.sendUnclearedConsent(user1.getID(), consentList));
			
			assertNotNull(shipper.getLatestPolicySet(user1.getID()));
			
			
		}
		catch (Exception e) {
			fail();
		}
	}
}
