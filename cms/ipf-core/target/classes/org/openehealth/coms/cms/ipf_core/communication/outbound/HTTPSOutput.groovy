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

import org.apache.camel.spring.SpringRouteBuilder;

import org.openehealth.coms.cms.ipf_core.IPFCore

/**
* HTTPS Output class handles output https
*
* @author nkarakus
*
*/
class HTTPSOutput extends SpringRouteBuilder{

	void configure() {
		if(IPFCore.getConfigFile().getProperty("HTTPS_OUTPUT_ENABLED").equals("true")){

			// setting of properties for https output connection
			System.setProperty("javax.net.ssl.trustStore", IPFCore.getConfigFile().getProperty("HTTPS_OUTPUT_TRUSTSTORE_PATH"));
			System.setProperty("javax.net.ssl.trustStorePassword", IPFCore.getConfigFile().getProperty("HTTPS_OUTPUT_TRUSTSTORE_PASSWORD"));

			from('direct:https_output')

					.to('https://'+IPFCore.getConfigFile().getProperty("HTTPS_OUTPUT_PATH"))
		}
	}
}