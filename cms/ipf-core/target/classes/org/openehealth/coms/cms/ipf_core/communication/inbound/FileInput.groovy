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

package org.openehealth.coms.cms.ipf_core.communication.inbound

import org.apache.camel.spring.SpringRouteBuilder;

import org.openehealth.coms.cms.ipf_core.IPFCore;

/**
* File Input class handles coming files and pass it on the mainroute.
*
* @author nkarakus
*
*/
class FileInput extends SpringRouteBuilder{

	void configure() {
		if(IPFCore.getConfigFile().getProperty("FILE_INPUT_ENABLED").equals("true")){
			from('file:'+IPFCore.getConfigFile().getProperty("FILE_INPUT_PATH")
					+'/?delete=true'
					+'&filter=#hl7FileFilter')
					.to('direct:mainroute')
		}
	}
}