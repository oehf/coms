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

package org.openehealth.coms.cms.ipf_core.routes.qbp.transmogrifier

import java.io.File
import java.text.SimpleDateFormat

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

import net.iharder.Base64

import org.openehealth.ipf.commons.core.modules.api.Transmogrifier
import org.openehealth.ipf.modules.hl7.AckTypeCode
import org.w3c.dom.Document
import org.w3c.dom.Node

import ca.uhn.hl7v2.model.v251.message.QBP_Q11
import ca.uhn.hl7v2.model.v251.segment.QPD
import ca.uhn.hl7v2.parser.GenericParser
import org.openehealth.coms.cms.ipf_core.IPFCore
import  org.openehealth.coms.cms.util.FileUtilities


/**
* QBP Q92 Transmogrifier class handles with QBP Q92 messages
* and retireves a list of all consent documents.
* Search criterion is patient id.
* @author nkarakus
*
*/
public class QBP_Q92_QBP_Q11Transmogrifier implements Transmogrifier{

	private static String urlConsentListSkeleton = "..//ipf-core//src/main//resources//consentDocumentList.xml";
	private static String urlConsentDocumentSkeleton= "..//ipf-core//src/main//resources//consentDocument.xml";

	private SimpleDateFormat hl7DateTime = new SimpleDateFormat("yyyyMMddHHmm");
	private GenericParser p = new GenericParser();

	//TODO Kommentar
	/**
	*
	* @param msg
	* @param params
	* @return
	*/
	public Object zap(Object msg, Object[] params) {

		String pid;
		File file=null;
		String cdaAsString="";
		String assignAuth;
		String familyName;
		String givenName;
		String cdaBase64 = "";

		QBP_Q11 qbpMsg = p.parse(msg.toString());
		QPD qpd = qbpMsg.getQPD();

		String field3 = qpd.getField(3, 0).encode();

		if(null!=field3 && 0<field3.length()){

			String[] fields = field3.split("\\~");

			for(int i=0; i<fields.length; i++){
				String field = fields[i];

				String[] parameter = field.split("\\^");
				String parameter1 = parameter[0];
				String parameter2 = parameter[1];

				//extract PID out of query pattern
				if(parameter1.equals("@PID.3")){
					pid = parameter2;
				}
			}
		}
		// save all consent documents to this pid in a file List
		Vector<File> filelist = FileUtilities.getFileList(IPFCore.configFile.getProperty("CDA_STORE_PATH"), pid);

		if(filelist != null){		
			DocumentBuilder db = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			Document consentDocumentList = null;
			Document consentDocument = null;
			try{
				db = dbf.newDocumentBuilder();
				consentDocumentList = db.parse(urlConsentListSkeleton);
				consentDocument = db.parse(urlConsentDocumentSkeleton);
				
				consentDocumentList.getElementsByTagName("setId").item(0).getAttributes().item(0).setNodeValue("");
				consentDocumentList.getElementsByTagName("setId").item(0).getAttributes().item(1).setNodeValue("");
				
				for(int i=0; i<filelist.size(); i++){
					file = filelist.get(i);
					
					Document cdaXml = db.parse(file);
					
					// set values for consentDocumentList
					String idExtension = cdaXml.getElementsByTagName("id").item(0).getAttributes().item(0).getNodeValue();
					String idRoot = cdaXml.getElementsByTagName("id").item(0).getAttributes().item(1).getNodeValue();;
					String effectiveTime = cdaXml.getElementsByTagName("effectiveTime").item(0).getAttributes().item(0).getNodeValue();;
					String versionNumber = "1";
					
					consentDocument.getElementsByTagName("id").item(0).getAttributes().item(0).setNodeValue(idExtension);
					consentDocument.getElementsByTagName("id").item(0).getAttributes().item(1).setNodeValue(idRoot);;
					consentDocument.getElementsByTagName("effectiveTime").item(0).getAttributes().item(0).setNodeValue(effectiveTime);;
					consentDocument.getElementsByTagName("versionNumber").item(0).getAttributes().item(0).setNodeValue(versionNumber);;
									
					//add consentDocument to consentDocumentList 
					Node copyConsentDocumentNode = consentDocumentList.importNode(consentDocument.getDocumentElement(), true);
					Node consentDocumentNode = consentDocumentList.getElementsByTagName("ConsentDocumentList").item(0)
					consentDocumentNode.insertBefore(copyConsentDocumentNode, null)
					
					// Take from the first document the details of the patient in order to use the ACK.
					if(i==0){
						assignAuth=cdaXml.getElementsByTagName("id").item(1).getAttributes().item(1).getNodeValue();
						familyName=cdaXml.getElementsByTagName("family").item(0).getTextContent();
						givenName=cdaXml.getElementsByTagName("given").item(0).getTextContent();
					}
				}
			}catch(Exception e){
				System.out.println(e);
			}
		
			try {
				TransformerFactory transFactory = TransformerFactory.newInstance();
				Transformer trans = transFactory.newTransformer();
				trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
				trans.setOutputProperty(OutputKeys.INDENT, "yes");

				StringWriter sw = new StringWriter();
				StreamResult result = new StreamResult(sw);
				DOMSource source = new DOMSource(consentDocumentList);
				trans.transform(source, result);
				cdaAsString = sw.toString();
				//load CDA and convert to Base64 String
				cdaBase64 = Base64.encodeBytes(cdaAsString.getBytes("UTF-8"));
			} catch (Exception e) {
				e.printStackTrace();
			}

			//create positive response
			def rspMsg = msg.respond('DOC', 'T12');

			//set MSH fields
			rspMsg.MSH[9][1] = "RSP";
			rspMsg.MSH[9][2] = "Q92";

			//set PID fields
			rspMsg.RESULT(0).PID[1] = "1"
			rspMsg.RESULT(0).PID[2][1] = pid;
			rspMsg.RESULT(0).PID[2][4][1] = "UKHD"
			rspMsg.RESULT(0).PID[2][4][2] = assignAuth;
			rspMsg.RESULT(0).PID[2][4][3] = "ISO"
			rspMsg.RESULT(0).PID[4][1] = familyName;
			rspMsg.RESULT(0).PID[4][2] = givenName;

			//set PV1 fields
			rspMsg.RESULT(0).PV1[2]="N"

			//set TXA fields
			rspMsg.RESULT(0).TXA[1]="1"
			rspMsg.RESULT(0).TXA[2]="CCDA"
			rspMsg.RESULT(0).TXA[3]="multipart"
			rspMsg.RESULT(0).TXA[11]="OID"
			rspMsg.RESULT(0).TXA[16]="LA"
			//set OBX Segment
			rspMsg.RESULT(0).OBX.parse("OBX||ED|59284-0^Consent List^LN||^multipart^x-hl7-cda-level-three^A^MIME-Version: 1.0 Content-Type: multipart/mixed; boundary=\"HL7-CDA-boundary\" Content-Transfer-Encoding: Base64 --HL7-CDA-boundary Content-Type: application/x-hl7-cda-level-three+xml $cdaBase64 --HL7-CDA-boundary||||||F")

			msg = rspMsg;

		} else{
			//create negative response
			msg = msg.nak("Query can not be processed!", AckTypeCode.AE)
		}
		return msg;
	}
}
