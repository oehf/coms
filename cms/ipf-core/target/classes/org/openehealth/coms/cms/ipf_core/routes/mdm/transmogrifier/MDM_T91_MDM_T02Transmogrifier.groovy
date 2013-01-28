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
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.Schema
import javax.xml.validation.SchemaFactory
import javax.xml.validation.Validator
import net.iharder.Base64
import org.openehealth.ipf.commons.core.modules.api.Transmogrifier
import org.openehealth.ipf.modules.hl7.AckTypeCode
import org.openehealth.ipf.modules.hl7.HL7v2Exception
import org.w3c.dom.Document
import org.xml.sax.InputSource
import org.openehealth.coms.cms.ipf_core.IPFCore

/**
* MDM T91 MDM T02 Transmogrifier 
* 
* @author nyueksekogul
*
*/
class MDM_T91_MDM_T02Transmogrifier implements Transmogrifier{


	/**
	 * Saves the hash into a Hash-document that has the file name as the PID
	 * @param msg
	 * @param params
	 * 
	 */
	public Object zap(Object msg, Object[] params) {
		
		String cdaHash = msg.OBSERVATION.OBX[5].value;
		String pid = msg.PID[3].value;
		
		boolean writeSuccess = writeToFile(cdaHash, pid+".hash");
		
		if(writeSuccess){
			msg = msg.ack();
		}
		else{
			//create nak
			msg = msg.nak(new HL7v2Exception('Hash code can not be written to file.', 207), AckTypeCode.AE);
			msg.ERR[3][5] = '';
		}

		return msg;
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
	* Returns true if the CDA document not exist and it is successfully written into file
	* @param cda
	* @param filename
	* @return true
	* @author mbirkle
	*/
	private boolean writeToFile(String cda, String filename){
		try {
			if(searchFile(IPFCore.configFile.getProperty("CDA_HASH_STORE_PATH"),filename)){
				
				System.out.println("Hash code already stored.");
				return false;
			}else{
				BufferedWriter out = new BufferedWriter(new FileWriter(IPFCore.getConfigFile().getProperty("CDA_HASH_STORE_PATH")+"/"+filename));
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
