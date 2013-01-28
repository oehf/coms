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

package org.openehealth.coms.cms.ipf_core;

import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.camel.spring.Main;
import org.openehealth.coms.cms.util.Utilities;

/**
 * 
 * 
 * @author mbirkle
 *
 */
public class IPFCore {
	// Path of configuration file folder
	private static String configFilePath = "./conf/";
	// Name of configuration file for normal purpose
	private static String configFileName = "ipf_core.properties";
	// Name of configuration file for log4j configuration
	private static String log4JConfigFileName = "log4j_config.xml";

	private static Properties configFile = new Properties();

	private static Logger logger = Logger.getLogger(IPFCore.class);

	public static void main(String[] args) throws Exception {
		// Loading configuration files
		configFile = Utilities.loadPropertieFile(configFilePath
				+ configFileName);
		DOMConfigurator.configure(configFilePath + log4JConfigFileName);	
		
		logger.info("Starting up IPF-Core...");
		
		// Starting application
		Main.main("-ac", "/context.xml");
	}  
	
	public static Properties getConfigFile() {
		return configFile;
	}
}




	
