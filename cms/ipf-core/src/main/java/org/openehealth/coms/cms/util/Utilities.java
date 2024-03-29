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

package org.openehealth.coms.cms.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.openehealth.coms.cms.exception.PropertieFileException;


/**
 * Utility class with some useful functions for file handling.
 * 
 * @author mbirkle
 * 
 */
public class Utilities {
	
	/**
	 * Loads properties form a file into a properties object.
	 * 
	 * @param path
	 * @return Properties object
	 * @throws PropertieFileException
	 * @throws FileNotFoundException
	 */
	public static Properties loadPropertieFile(String path)
			throws PropertieFileException, FileNotFoundException {
		File propertieFile = new File(path);
		FileInputStream inStream = null;
		if (propertieFile.exists()) {
			try {
				inStream = new FileInputStream(propertieFile);
				Properties properties = new Properties();
				properties.load(inStream);
				return properties;
			} catch (IOException e) {
				throw new PropertieFileException(
						"Propertie file can note be loaded properly.");
			} finally {
				try {
					if (inStream != null)
						inStream.close();
				} catch (IOException e) {
					;
				}
			}
		} else {
			throw new FileNotFoundException("Propertie file " + path
					+ " was not found!");
		}
	}
}
