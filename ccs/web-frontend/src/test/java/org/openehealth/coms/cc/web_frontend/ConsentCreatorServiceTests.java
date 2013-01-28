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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.junit.Test;
import org.openehealth.coms.cc.web_frontend.consentcreator.model.Base64;
import org.openehealth.coms.cc.web_frontend.consentcreator.model.User;
import org.openehealth.coms.cc.web_frontend.consentcreator.service.ConsentCreatorService;
import org.openehealth.coms.cc.web_frontend.consentcreator.service.DocumentFactory;
import org.openehealth.coms.cc.web_frontend.consentcreator.service.ServiceException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


/**
 * Tests for the class ConsentCreatorService
 * 
 * @author Lennart Koester
 * @version 1.0 14.02.2011
 *
 */

public class ConsentCreatorServiceTests {

	DocumentFactory documentFactory = new DocumentFactory();

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
			user1.setID("123");
			user1.setPassword("admin");
			user1.setPrivileges(0);
			user1.setStreet("Sesamstra�e");
			user1.setZipcode(69120);
			
			ConsentCreatorService ccs = ConsentCreatorService.getInstance();
			
			
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
			
			
			File baseDir = new File(".");

			File pdffile = new File(baseDir, "consent.pdf");

			FileOutputStream outStream = new FileOutputStream(pdffile);
			
//			Document policySet = documentFactory.getSkeletonPolicySet(true, user1);
			
			Document rule1 = ccs.constructRule("UKHD", "ALL", "1.1.1", "ALL", "1.1", "read", true);
			Document rule2 = ccs.constructRule("KKHS", "ALL", "1.1.2", "ALL", "1.1", "read", true);
			Document rule3 = ccs.constructRule("KKHS", "Aerzte", "1.1.2.1", "ALL", "1.1", "read", false);
			Document rule4 = ccs.constructRule("UKHD", "Aerzte", "1.1.1.1", "Arztbiefe", "1.1.1", "read", false);
			Document rule5 = ccs.constructRule("KKHS", "ALL", "1.1.2", "Laborberichte", "1.1.2", "read", false);
			Document rule6 = ccs.constructRule("UKHD", "ALL", "1.1.2", "ALL", "1.1", "write", false);
			
			
			Document rule7 = ccs.constructRule("KKHS", "Aerzte", "1.1.2.1", "ALL", "1.1", "read", true);
			Document rule8 = ccs.constructRule("UKHD", "Aerzte", "1.1.1.1", "Arztbiefe", "1.1.1", "read", false);
			Document rule9 = ccs.constructRule("UKHD", "ALL", "1.1.1", "Laborberichte", "1.1.2", "read", false);
			Document rule10 = ccs.constructRule("UKHD", "ALL", "1.1.1", "ALL", "1.1", "readwrite", true);
			
			
			Document rule11 = ccs.constructRule("UKHD", "Chefaerzte", "1.1.1.1.3.2.1", "Laborberichte", "1.1.2", "read", true); //4
			Document rule12 = ccs.constructRule("KKHS", "Chefaerzte", "1.2.1.1.3.2.1", "Arztbriefe", "1.1.1", "read", true); //2
			Document rule13 = ccs.constructRule("UKHD", "Oberaerzte", "1.1.1.1.3.2", "Arztbrief", "1.1.1", "read", true); //1
			Document rule14 = ccs.constructRule("UKHD", "Aerzte", "1.1.1.1.3", "ALL", "1.1", "read", false); //3
			Document rule15 = ccs.constructRule("Charite", "Klinkleiter", "1.3.1.1.3.2.1.0", "Arztbriefe", "1.1.1", "read", true); //5
			
			
//			ccs.addRuleToPolicySet(policySet, rule1, 2);
//			ccs.addRuleToPolicySet(policySet, rule2, 2);
//			ccs.addRuleToPolicySet(policySet, rule3, 2);
//			ccs.addRuleToPolicySet(policySet, rule4, 1);
//			ccs.addRuleToPolicySet(policySet, rule5, 2); //<-
//			ccs.addRuleToPolicySet(policySet, rule6, 2);
			
			
			
//			ccs.addRuleToPolicySet(policySet, rule9, 1); //<-
//			ccs.addRuleToPolicySet(policySet, rule8, 1); 
//			ccs.addRuleToPolicySet(policySet, rule10, 2);
//			ccs.addRuleToPolicySet(policySet, rule7, 1);
			
//			ccs.addRuleToPolicySet(policySet, rule13, 1);
//			ccs.addRuleToPolicySet(policySet, rule12, 1); 
//			ccs.addRuleToPolicySet(policySet, rule15, 1);
//			ccs.addRuleToPolicySet(policySet, rule14, 1);
//			ccs.addRuleToPolicySet(policySet, rule11, 1);
//			
			documentFactory.updateTargetElements(domPolicySet);
//			
			documentFactory.constructCDA(user1, domPolicySet, null, true, outStream);
			
			String s = "PD94bWwgdmVyc2lvbj0iMS4wIj8+DQo8Q29uc2VudERvY3VtZW50TGlzdD4NCgk8c2V0SWQgZXh0ZW5zaW9uPSI5ODc2NTQzMjEiIHJvb3Q9IjIuMTYuODQwLjEuMTEzODgzLjE5Ii8+DQoJPENvbnNlbnREb2N1bWVudD4NCgkJPGlkIGV4dGVuc2lvbj0iMTIzNDU2Nzg5IiByb290PSIyLjE2Ljg0MC4xLjExMzg4My4xOSIvPg0KCQk8ZWZmZWN0aXZlVGltZSB2YWx1ZT0iMjAxMDEwMTgxNTMxIi8+DQoJCTx2ZXJzaW9uTnVtYmVyIHZhbHVlPSIxIi8+DQoJPC9Db25zZW50RG9jdW1lbnQ+DQoJPENvbnNlbnREb2N1bWVudD4NCgkJPGlkIGV4dGVuc2lvbj0iMTIzNDU2Nzg5IiByb290PSIyLjE2Ljg0MC4xLjExMzg4My4xOSIvPg0KCQk8ZWZmZWN0aXZlVGltZSB2YWx1ZT0iMjAxMDEwMTgxNTMxIi8+DQoJCTx2ZXJzaW9uTnVtYmVyIHZhbHVlPSIxIi8+DQoJPC9Db25zZW50RG9jdW1lbnQ+DQo8L0NvbnNlbnREb2N1bWVudExpc3Q+";
			
			byte[] ar = Base64.decode(s);
			
//			policySet = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(ar));
			
			
			try {
				// Prepare the DOM document for writing
//				Source source = new DOMSource(policySet);

				// Prepare the output file
				File file = new File("pol.xml");
				Result result = new StreamResult(file);

				// Write the DOM document to the file
				Transformer xformer = TransformerFactory.newInstance()
						.newTransformer();
//				xformer.transform(source, result);
			} catch (Exception e) {
			}
			

			//assertTrue(ccs.deactivateUser(user1, null, outStream));
			
			//assertTrue(ccs.registerUser(user1, null));

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	public static byte[] getbytes(Object obj) throws java.io.IOException{
	      ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
	      ObjectOutputStream oos = new ObjectOutputStream(bos); 
	      oos.writeObject(obj);
	      oos.flush(); 
	      oos.close(); 
	      bos.close();
	      byte [] data = bos.toByteArray();
	      return data;
	  }
}
