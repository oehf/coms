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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

/**
 * Utility class with some useful functions for file handling.
 * 
 * @author mbirkle, nkarakus
 * 
 */
public class FileUtilities {

	/**
	 * Writes the given byte array to a file.
	 * 
	 * @param fileData
	 *            Data to be written t a file.
	 * @param path
	 *            Path where the file should be written to.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void writeFile(byte[] fileData, String path)
			throws FileNotFoundException, IOException {
		File fileToWrite = new File(path);
		if (!fileToWrite.exists()) {
			File pathToFile = new File(fileToWrite.getParent());
			pathToFile.mkdirs();
			fileToWrite.createNewFile();
		}
		FileOutputStream outStream = null;
		try {
			outStream = new FileOutputStream(fileToWrite);
			outStream.write(fileData);
		} finally {
			try {
				if (outStream != null)
					outStream.close();
			} catch (IOException e) {
				;
			}
		}
	}

	/**
	 * Reads a given file into a byte array.
	 * 
	 * @param path
	 *            Path form where the file should be read.
	 * @return Byte array
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static byte[] readFile(String path) throws FileNotFoundException,
			IOException {
		File fileToReade = new File(path);
		if (fileToReade.exists()) {
			byte[] buffer = new byte[(int) fileToReade.length()];
			FileInputStream inStream = null;
			try {
				inStream = new FileInputStream(fileToReade);
				inStream.read(buffer);
			} finally {
				try {
					if (inStream != null)
						inStream.close();
				} catch (IOException e) {
					;
				}
			}

			return buffer;
		} else {
			throw new FileNotFoundException("File " + path + "not found!");
		}
	}


	/**
	 * Returns the fileList.
	 * 
	 * @author nKarakus
	 * @param path
	 * @param searchString
	 * @return Vector<File>
	 */
	public static Vector<File> getFileList(String path, String searchString) {
		Vector<File> fileList = new Vector<File>();
		File dir = new File(path);
		File[] files = dir.listFiles();

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().contains(searchString)) {
					fileList.add(files[i]);
				}
			}
			Collections.sort(fileList);
			return fileList;
		}
		return null;
	}

}
