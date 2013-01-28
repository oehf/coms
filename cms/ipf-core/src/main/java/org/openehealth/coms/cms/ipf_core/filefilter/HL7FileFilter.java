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

package org.openehealth.coms.cms.ipf_core.filefilter;

import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileFilter;

/**
 *  Instance of GenerciFileFilter which returns true
 *  if the given file is a HL7 File.
 * 
 * @author mbirkle
 *
 */
@SuppressWarnings("rawtypes")
public class HL7FileFilter implements GenericFileFilter{

	/**
	 * @return true if file is a hl7 file
	 */
	public boolean accept(GenericFile pathname) {
		
		if(pathname.getFileName().contains(".hl7")){
			return true;
		}
		
		return false;
    }
}
