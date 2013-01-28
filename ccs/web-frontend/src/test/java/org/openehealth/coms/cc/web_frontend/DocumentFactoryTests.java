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
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Test;
import org.openehealth.coms.cc.web_frontend.consentcreator.model.User;
import org.openehealth.coms.cc.web_frontend.consentcreator.service.DocumentFactory;
import org.openehealth.coms.cc.web_frontend.consentcreator.service.ServiceException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


/**
 * Tests for the class DocumentFactory
 * 
 * @author Lennart Koester
 * @version 1.0 14.02.2011
 *
 */

public class DocumentFactoryTests {

	DocumentFactory documentFactory = new DocumentFactory();

	@Test
	public void testDocumentFactoryMethods() {
		try {
			User user1 = new User();
			
			user1.setActive(1);
			user1.setBirthdate(new Date());
			user1.setCity("Heidelberg");
			user1.setEmailaddress("allyourbase@arebelongto.us");
			user1.setForename("Homo");
			user1.setName("Heidelbergensis");
			user1.setGender("male");
			user1.setID("123");
			user1.setPassword("admin");
			user1.setPrivileges(0);
			user1.setStreet("Sesamstraße");
			user1.setZipcode(69120);
			
			
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
			
			assertNotNull(documentFactory.constructRule("UKHD", "ALL", "1.1.1", "ALL", "1.1", "read", true));
			
			Document rule1 = documentFactory.constructRule("UKHD", "ALL", "1.1.1", "ALL", "1.1", "read", true);
						
			assertNotNull(documentFactory.addRuleToPolicySet(domPolicySet, rule1, 2));
			
			assertNotNull(documentFactory.updateTargetElements(domPolicySet));
			
			File storeFile = new File("cda.xml");
			
			File pdffile = new File("consentDFT.pdf");
				
			FileOutputStream outStream = new FileOutputStream(pdffile);
			
			try {
				
				// Prepare the DOM document for writing
				Source source = new DOMSource(documentFactory.constructCDA(user1, domPolicySet, user1, true, outStream));

				// Prepare the output file
				Result result = new StreamResult(storeFile);

				// Write the DOM document to the file
				Transformer xformer = TransformerFactory.newInstance().newTransformer();
				xformer.transform(source, result);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			assertFalse(documentFactory.getMD5Hash(domPolicySet).equalsIgnoreCase(""));
			
			assertFalse(documentFactory.getMD5Hash(policySetURL).equalsIgnoreCase(""));
			
			assertTrue(documentFactory.getResources().size() != 0);
		
			
//			documentFactory.deleteUnclearedConsent("filename"); Cannot be tested due to store path relative to the directories on the server
			
//			documentFactory.storeUnclearedConsent(domPolicySet, "filename"); Cannot be tested due to store path relative to the directories on the server
			
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
