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
* MsgLogger class save information(filename)  
* who come in or come out.
* @author nkarakus
*
*/
class MsgLogger extends SpringRouteBuilder{

	void configure() {
		from('direct:msg_logger')

				.unmarshal().ghl7()

				.setHeader(Exchange.FILE_NAME) {exchange ->
					exchange.in.body.MSH[10].value+
							'_'+
							exchange.in.body.MSH[7].value+
							'.hl7'
				}
				.marshal().ghl7()

				.to('file:'+IPFCore.getConfigFile().getProperty("MSG_LOG_PATH"))
	}
}
