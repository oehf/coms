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

import org.apache.camel.spring.SpringRouteBuilder

import org.openehealth.coms.cms.ipf_core.IPFCore

/**
* MLLP Output class handles output mllp and send
* with tcp 
* @author nkarakus
*
*/
class MLLPOutput extends SpringRouteBuilder{
	
	void configure() {
		if(IPFCore.getConfigFile().getProperty("MLLP_OUTPUT_ENABLED").equals("true")){
			from('direct:mllp_output')

					.to('mina:tcp://'+IPFCore.getConfigFile().getProperty("MLLP_OUTPUT_PATH")
						+'?sync=true'
						+'&codec=#mllpStoreCodec')
		}
	}
}

