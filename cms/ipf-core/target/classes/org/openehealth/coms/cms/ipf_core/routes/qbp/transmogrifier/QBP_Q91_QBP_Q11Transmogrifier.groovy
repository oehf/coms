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

import net.iharder.Base64

import org.openehealth.ipf.commons.core.modules.api.Transmogrifier
import org.openehealth.ipf.modules.hl7.AckTypeCode

import ca.uhn.hl7v2.model.v251.message.QBP_Q11
import ca.uhn.hl7v2.model.v251.segment.QPD
import ca.uhn.hl7v2.parser.GenericParser
import org.openehealth.coms.cms.ipf_core.IPFCore
import  org.openehealth.coms.cms.util.FileUtilities

/**
* QBP Q91 Transmogrifier class handles with QBP Q91 messages
* and retrieves the current consent document. 
* Search criterion is patient id.
* @author nkarakus
*
*/
public class QBP_Q91_QBP_Q11Transmogrifier implements Transmogrifier{

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

		//transform String to QBP_Q11 format
		QBP_Q11 qbpMsg = p.parse(msg.toString());
		// get the segment QPD
		QPD qpd = qbpMsg.getQPD();IPFCore.configFile.getProperty("CDA_STORE_PATH")

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
		Vector<File> fileList=FileUtilities.getFileList(IPFCore.configFile.getProperty("CDA_STORE_PATH"), pid);

		File cdaFile = null;

		if(fileList.size() != 0){
			File tmpCdaFile = null;
			SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmssZ");

			// search the current document from the file list
			for(int i=0; i<fileList.size(); i++){
				tmpCdaFile = fileList.elementAt(i);

				if(cdaFile != null){
					Date dateCdaFile = sd.parse(getDateFromFilename(cdaFile.getName()));
					Date dateTmpCdaFile = sd.parse(getDateFromFilename(tmpCdaFile.getName()));

					if(dateTmpCdaFile.getTime() > dateCdaFile.getTime()){
						cdaFile = tmpCdaFile;
					}
				}else{
					cdaFile = tmpCdaFile;
				}
			}
		}

		if(cdaFile != null){
			def cdaXml = new XmlSlurper().parse(cdaFile);

			//create positive response
			def rspMsg = msg.respond('DOC', 'T12');

			//set MSH fields
			rspMsg.MSH[9][1] = "RSP";
			rspMsg.MSH[9][2] = "Q91";

			//set PID fields
			rspMsg.RESULT(0).PID[1] = "1"
			rspMsg.RESULT(0).PID[2][1] = cdaXml.recordTarget.patientRole.id[0].@extension.text();
			rspMsg.RESULT(0).PID[2][4][1] = "UKHD"
			rspMsg.RESULT(0).PID[2][4][2] = cdaXml.recordTarget.patientRole.id[0].@root.text();
			rspMsg.RESULT(0).PID[2][4][3] = "ISO"
			rspMsg.RESULT(0).PID[4][1] = cdaXml.recordTarget.patientRole.patient.name.family;
			rspMsg.RESULT(0).PID[4][2] = cdaXml.recordTarget.patientRole.patient.name.given;

			//set PV1 fields
			rspMsg.RESULT(0).PV1[2]="N"

			//set TXA fields
			rspMsg.RESULT(0).TXA[1]="1"
			rspMsg.RESULT(0).TXA[2]="CCDA"
			rspMsg.RESULT(0).TXA[3]="multipart"
			rspMsg.RESULT(0).TXA[11]="OID"
			rspMsg.RESULT(0).TXA[16]="LA"

			//load CDA and convert to Base64 String
			byte[] cdaBytes = FileUtilities.readFile(cdaFile.toString());
			String cdaBase64 = Base64.encodeBytes(cdaBytes);

			//set OBX Segment
			rspMsg.RESULT(0).OBX.parse("OBX||ED|59284-0^Consent Document Patient^LN||^multipart^x-hl7-cda-level-three^A^MIME-Version: 1.0 Content-Type: multipart/mixed; boundary=\"HL7-CDA-boundary\" Content-Transfer-Encoding: Base64 --HL7-CDA-boundary Content-Type: application/x-hl7-cda-level-three+xml $cdaBase64 --HL7-CDA-boundary||||||F")

			msg = rspMsg;

		} else{
			//create negative response
			msg = msg.nak("Query can not be processed!", AckTypeCode.AE)
		}

		return msg;
	}

	
	
	/**
	* Returns the date from the filename.
	* 
	* @param filename
	* @return
	* @author mbirkle
	*/
	private String getDateFromFilename(String filename){
		String[] tokens1 = filename.tokenize(".");
		String[] tokens2 = tokens1[0].tokenize("_");

		return tokens2[3];
	}
}

