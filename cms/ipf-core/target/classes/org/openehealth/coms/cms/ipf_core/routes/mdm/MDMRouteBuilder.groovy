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

package org.openehealth.coms.cms.ipf_core.routes.mdm

import org.apache.camel.spring.SpringRouteBuilder
import org.openehealth.coms.cms.ipf_core.IPFCore
import org.openehealth.coms.cms.ipf_core.routes.mdm.transmogrifier.MDM_T02_MDM_T02Transmogrifier
import org.openehealth.coms.cms.ipf_core.routes.mdm.transmogrifier.MDM_T91_MDM_T02Transmogrifier

/**
* MDM Route Builder class handles with MDM T02 messages 
* and transmogrify this.
* @author nkarakus
*
*/
class MDMRouteBuilder extends SpringRouteBuilder {

	void configure() {

		from('direct:MDM^T02^MDM_T02')

				.unmarshal().ghl7()

				.transmogrify(new MDM_T02_MDM_T02Transmogrifier())

				.marshal().ghl7()

				.to('mock:output')
				
				
		from('direct:MDM^T91^MDM_T02')
				
				.unmarshal().ghl7()
				
				.transmogrify(new MDM_T91_MDM_T02Transmogrifier())
				
				.marshal().ghl7()
				
				.to('mock:output')
	}
}
