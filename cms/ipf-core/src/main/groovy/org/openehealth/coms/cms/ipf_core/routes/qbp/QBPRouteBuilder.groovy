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

package org.openehealth.coms.cms.ipf_core.routes.qbp

import org.openehealth.coms.cms.ipf_core.routes.qbp.transmogrifier.QBP_Q91_QBP_Q11Transmogrifier
import org.openehealth.coms.cms.ipf_core.routes.qbp.transmogrifier.QBP_Q92_QBP_Q11Transmogrifier
import org.openehealth.coms.cms.ipf_core.routes.qbp.transmogrifier.QBP_Q93_QBP_Q11Transmogrifier
import org.apache.camel.spring.SpringRouteBuilder

/**
* QBP Route Builder class handles with QBP Q91,Q92 and Q93 messages
* and transmogrify this.
* @author nkarakus
*
*/
class QBPRouteBuilder extends SpringRouteBuilder{

	void configure() {

		from('direct:QBP^Q91^QBP_Q11')
				.unmarshal().ghl7()

				.transmogrify(new QBP_Q91_QBP_Q11Transmogrifier())

				.marshal().ghl7()

				.to('mock:output')


		from('direct:QBP^Q92^QBP_Q11')
				.unmarshal().ghl7()

				.transmogrify(new QBP_Q92_QBP_Q11Transmogrifier())

				.marshal().ghl7()

				.to('mock:output')


		from('direct:QBP^Q93^QBP_Q11')
				.unmarshal().ghl7()

				.transmogrify(new QBP_Q93_QBP_Q11Transmogrifier())

				.marshal().ghl7()

				.to('mock:output')
	}
}
