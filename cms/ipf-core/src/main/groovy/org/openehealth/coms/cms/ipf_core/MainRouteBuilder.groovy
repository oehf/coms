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

package org.openehealth.coms.cms.ipf_core

import org.apache.camel.spring.SpringRouteBuilder
import org.openehealth.ipf.modules.hl7.AckTypeCode
import org.openehealth.ipf.modules.hl7.HL7v2Exception
import org.openehealth.ipf.modules.hl7dsl.MessageAdapters.*

import ca.uhn.hl7v2.validation.ValidationException


/**
* Route Builder class with functions for route and exception handling.
*
* @author mbirkle,nkarakus
*
*/
class MainRouteBuilder extends SpringRouteBuilder {
	
	/**
	 * Configuration of the routing. The incoming
	 * messages will be validated and routed according
	 * to the massage type according end point
	 */
	void configure() {

		from('direct:mainroute')
				.to('direct:msg_logger')
				.to('direct:msg_router')

		from('direct:msg_router')

				.unmarshal().ghl7()

				.choice()
				
				//routing for MDM^T02^MDM_T02 messages
				.when {
					it.in.body.MSH[9][1].value == 'MDM' &&
					it.in.body.MSH[9][2].value == 'T02' &&
					it.in.body.MSH[9][3].value == 'MDM_T02'
				}

				.to('direct:MDM^T02^MDM_T02')
				
				//routing for MDM^T02^MDM_T02 messages
				.when {
					it.in.body.MSH[9][1].value == 'MDM' &&
					it.in.body.MSH[9][2].value == 'T91' &&
					it.in.body.MSH[9][3].value == 'MDM_T02'
				}

				.to('direct:MDM^T91^MDM_T02')


				//routing for QBP^Q91^QBP_Q11 messages
				.when {
					it.in.body.MSH[9][1].value == 'QBP' &&
					it.in.body.MSH[9][2].value == 'Q91' &&
					it.in.body.MSH[9][3].value == 'QBP_Q11'
				}
				.to('direct:QBP^Q91^QBP_Q11')

				//routing for QBP^Q92^QBP_Q11 messages
				.when {
					it.in.body.MSH[9][1].value == 'QBP' &&
					it.in.body.MSH[9][2].value == 'Q92' &&
					it.in.body.MSH[9][3].value == 'QBP_Q11'
				}
				.to('direct:QBP^Q92^QBP_Q11')

				//routing for QBP^Q93^QBP_Q11 messages
				.when {
					it.in.body.MSH[9][1].value == 'QBP' &&
					it.in.body.MSH[9][2].value == 'Q93' &&
					it.in.body.MSH[9][3].value == 'QBP_Q11'
				}
				.to('direct:QBP^Q93^QBP_Q11')
				.otherwise().end()
	}

	/**
	 * Configuration of error handling.
	 */
	void configureErrorRoutes() {

		onException(ValidationException.class)
				.handled(true)
				.to('direct:bad_request')

		from('direct:bad_request')
				.unmarshal().ghl7()

				//create a negative acknowledge message
				.transmogrify{ msg ->
					def nak = msg.nak(new HL7v2Exception('A valadiation exception occured. The HL7 message may be malformed.', 200), AckTypeCode.AE)
					nak.ERR[3][5] = ''
					msg = nak
					msg
				}

				.marshal().ghl7()
				.to('mock:output')
	}
}