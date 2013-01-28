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

package org.openehealth.coms.cc.web_frontend.bootstrap;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;
import org.apache.log4j.Logger;
import org.openehealth.coms.cc.web_frontend.consentcreator.service.ConnectionFactory;

/**
 * Use of this class pushes the database-creation script onto the database.
 * 
 * @author Lennart Koester
 * @version 1.0 14.02.2011
 */
public class ConsentCreatorBootstrap {

	public static void main(String[] args) {

		try {
			URL url = ConsentCreatorBootstrap.class
					.getResource("bootstrap.sql");
			File file = new File(url.toURI());

			InputStream stream = new FileInputStream(file);
			byte[] bytes = new byte[stream.available()];
			stream.read(bytes);
			String sql = new String(bytes);
			stream.close();

			if (sql.indexOf('$') != -1) {
				sql = sql.replace("'", "''");
				sql = sql.replace("$$", "'");
			}

			Connection con = ConnectionFactory.getInstance().getConnection();

			Statement statement = con.createStatement();
			statement.executeUpdate(sql);
			statement.close();

			con.close();

		} catch (Exception e) {
			Logger.getLogger(ConsentCreatorBootstrap.class.getClass()).error(e);
		}
	}
}
