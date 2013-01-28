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

package org.openehealth.coms.cms.ipf_core.routes.mdm.transmogrifier;

import groovy.util.XmlSlurper

import java.io.File
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.Schema
import javax.xml.validation.SchemaFactory
import javax.xml.validation.Validator

import net.iharder.Base64

import org.openehealth.coms.cms.ipf_core.IPFCore
import org.openehealth.coms.cms.util.FileUtilities
import org.openehealth.ipf.commons.core.modules.api.Transmogrifier
import org.openehealth.ipf.modules.hl7.AckTypeCode
import org.openehealth.ipf.modules.hl7.HL7v2Exception
import org.w3c.dom.Document
import org.xml.sax.InputSource

/**
* MDM T01 MDM T02 Transmogrifier 
* 
* @author nyueksekogul
*
*/
class MDM_T02_MDM_T02Transmogrifier implements Transmogrifier{

	//TODO Kommentar
	/**
	 * 
	 * @param msg
	 * @param params
	 * 
	 */
	public Object zap(Object msg, Object[] params) {

		String CDA = extractCDAtoString((String)msg.OBSERVATION.OBX[5][5]);

		boolean validated = true;
		
		if(IPFCore.getConfigFile().getProperty("CDA_SCHEMA_VALIDATION_ENABLED").equals("true")){
			
			validated = validateCDA(CDA);
		
		}		

		if(validated){
		
			//retreving information for filename
			def clinicalDocument = new XmlSlurper().parseText(CDA);

			String givenName=clinicalDocument.recordTarget.patientRole.patient.name.given;
			String familyName=clinicalDocument.recordTarget.patientRole.patient.name.family;
			String PID=clinicalDocument.recordTarget.patientRole.id[0].@extension.text();
			String assigningAuth=clinicalDocument.recordTarget.patientRole.id[0].@root.text();

			String msgGivenName= msg.PID[5][2].value;
			String msgFamilyName=msg.PID[5][1].value;
			String msgPID=msg.PID[3].value;
			String msgAssigningAuth=msg.PID[3][4][2].value;
			
			if(givenName.toUpperCase().equals(msgGivenName)&&familyName.toUpperCase().equals(msgFamilyName)&&PID.equals(msgPID)&&assigningAuth.equals(msgAssigningAuth)){

				//create filename
				String documentID = clinicalDocument.id.@extension.text();
			
				String timeStamp = clinicalDocument.effectiveTime.@value.text();				
				
				String filename = "CDA_"+PID + "_" + documentID+ "_" + timeStamp + ".xml";			
						
					//	search file by pid
					//if a file to the pid does not exist, save cda with filename
					if(fileSearch(IPFCore.configFile.getProperty("CDA_STORE_PATH"),PID) == false){
					
						//write to cda file system
						boolean written = writeToFile(CDA, filename);						
						
						if(written){																				
							
							//create ack
							msg = msg.ack();																			
																								
						}else{													
																			
							//create nak							
							msg = msg.nak(new HL7v2Exception('For further information view logfiles.', 207), AckTypeCode.AE);
						
							msg.ERR[3][5] = '';
							
						}
						
					}else{
					
						try{
						
							String hashFile = PID + ".hash";
							
							//search hash file by pid
							if(searchFile(IPFCore.configFile.getProperty("CDA_HASH_STORE_PATH"),hashFile)){
								
								String hashFilePath="//home//mis-admin//cm//cda_hash//"+hashFile;
								
								//read hash file and convert to String
								byte[] hashFileBytes = FileUtilities.readFile(hashFilePath.toString());
							
								String convertHashFile= new String (hashFileBytes);
													
								//convert cda to md5 hash
								String hashCDA=getMD5Hash(CDA);	
								
								//compare cda hash with the hash from hash file
								if(hashCDA.equals(convertHashFile)){								
								
									boolean written = writeToFile(CDA, filename);						
								
									if(written){															
	
										//delete existing hash file
										File deleteFile = new File(hashFilePath.toString());
										deleteFile.delete();
									
										//create ack
										msg = msg.ack();
																						
									}else{																								

										//create nak
										msg = msg.nak(new HL7v2Exception('For further information view logfiles.', 207), AckTypeCode.AE);
								
										msg.ERR[3][5] = '';
										
									}							
								}else{	

									//create nak
									msg = msg.nak(new HL7v2Exception('Hash value does not match the document hash.', 207), AckTypeCode.AE);
							
									msg.ERR[3][5] = '';
									
								}	
															
							}else{;
								
								msg = msg.nak(new HL7v2Exception('Consent information has not Hash document', 207), AckTypeCode.AE);	
							
								msg.ERR[3][5] = '';
								
							}
							
						}catch(IOException e){
						
							e.printStackTrace();
						}
												
					}
						
			}else{		
				
				//create nak
				msg = msg.nak(new HL7v2Exception('Consent information does not the PID.', 200), AckTypeCode.AE);
			
				msg.ERR[3][5] = '';	
				
			}	
					
		}else{	

			//create naksearchFileToPID
			msg = msg.nak(new HL7v2Exception('A schema validation exception occured. The CDA document may be malformed.', 200), AckTypeCode.AE);
			
			msg.ERR[3][5] = '';	
						
		}	
			
		return msg;	
		
	}

	

	/**
	 * Produces a MD5 Hash based on the given String
	 * @param s
	 * @return
	 * @author nyueksekogul 
	 * 
	 */
	public String getMD5Hash(String s){
		
		String hash=null;
		
		try{
			
			MessageDigest md5= MessageDigest.getInstance("MD5");
		
			md5.update(s.getBytes(),0,s.length());
		
			hash=new BigInteger(1,md5.digest()).toString(16);
			
		}catch(final NoSuchAlgorithmException e){
			
			System.out.println(e.printStackTrace());
		
		}
		
		return hash;
	}

	/**
	 * Returns the CDA document as String.
	 * @param sttrue
	 * @return
	 * @author mbirkle 
	 */
	private String extractCDAtoString(String st){
		
		int startPos = st.indexOf("application/x-hl7-cda-level-three+xml")+"application/x-hl7-cda-level-three+xml".length()+1;

		String temp = st.substring(startPos);
	
		String CDA_B64 = temp.substring(0, temp.indexOf(" "));

		return new String(Base64.decode(CDA_B64));
	
	}

	/**
	 * Returns true if the CDA document is successfully validated against the given schema
	 * @param cda
	 * @return
	 * @author mbirkle
	 */
	private boolean validateCDA(String cda){
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		factory.setNamespaceAware(true);

		DocumentBuilder builder = factory.newDocumentBuilder();

		StringReader reader = new StringReader(cda);
	
		InputSource inputSource = new InputSource(reader);

		Document doc = builder.parse(inputSource);

		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
		
		StreamSource schemaFile = new StreamSource(new File(IPFCore.getConfigFile().getProperty("CDA_SCHEMA_PATH")));
		
		Schema schema = schemaFactory.newSchema(schemaFile);
		
		Validator validator = schema.newValidator();

		try{
		
			validator.validate(new DOMSource(doc));
		
		}catch(Exception e){
		
			return false;
		
		}
		
		return true;
	
	}
	
	
	/**
	* Returns true if the CDA document is successfully find
	* @param path
	* @param filename
	* @return true 
	* @author mbirkle
	*/
	private boolean searchFile(String path, String filename){
		
		File dir = new File(path);
		File[] files = dir.listFiles();

		if(files != null){
			for(int i=0; i < files.length; i++){
				if(files[i].getName().contains(filename)){
					return true;	
				}	
			}
		}else{
			return false;
		}	
		return false;
	}
	
	
	
	/**
	* Returns true if the PID of the CDA document is successfully find
	* @param path
	* @param pid
	* @return true 
	* @author nyueksekogul
	*/
	private boolean fileSearch(String path, String pid){
	
		File dir = new File(path);
		File[] files = dir.listFiles();
	
		boolean finden=false;
	
		if(files != null){
	
		
			for(int i=0; i < files.length; i++){
			
				if(files[i].getName().indexOf(pid) == -1){
	
					finden= false;
	
				}else{
	
					finden=true;
					
					return finden;
	
				}
			}
		
		}else{

			return false;
	
		}
	
		return false;
	}
	

	/**
	* Returns true if the CDA document not exist and it is successfully written into file
	* @param cda
	* @param filename
	* @return true
	* @author mbirkle
	*/
	private boolean writeToFile(String cda, String filename){
		
		try {
			
			if(searchFile(IPFCore.configFile.getProperty("CDA_STORE_PATH"),filename)){
			
				System.out.println("CDA document exists");
				
				return false;
			
			}else{
			
				BufferedWriter out = new BufferedWriter(new FileWriter(IPFCore.getConfigFile().getProperty("CDA_STORE_PATH")+"/"+filename));
				out.write(cda);
				out.close();
			
			}
		
		}
		
		catch (IOException e) {
		
			System.out.println(e.printStackTrace());
		
			return false;
	
		}
		
		return true;
	
	}

}