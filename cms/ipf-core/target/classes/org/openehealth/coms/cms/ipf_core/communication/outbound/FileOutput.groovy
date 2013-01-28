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

package org.openehealth.coms.cms.ipf_core.communication.outbound

import org.apache.camel.Exchange
import org.apache.camel.spring.SpringRouteBuilder

import org.openehealth.coms.cms.ipf_core.IPFCore

/**
* File Output class handles output files 
* and save them as a file in a output path for files.
* @author nkarakus
*
*/
class FileOutput extends SpringRouteBuilder{
	void configure() {
		if(IPFCore.getConfigFile().getProperty("FILE_OUTPUT_ENABLED").equals("true")){
			from('direct:file_output')
					.unmarshal().ghl7()

					//Structure of the filename
					.setHeader(Exchange.FILE_NAME) {exchange ->
						exchange.in.body.MSH[9][3].value+
								'_'+
								exchange.in.body.PID[3].value+
								'_'+
								exchange.in.body.MSH[10].value+
								'_'+
								exchange.in.body.MSH[7].value+
								'.hl7'
					}
					.marshal().ghl7()

					.to('file:'+IPFCore.getConfigFile().getProperty("FILE_OUTPUT_PATH"))
		}
	}
}
